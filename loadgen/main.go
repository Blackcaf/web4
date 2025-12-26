package main

import (
	"bytes"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"net/http"
	"os"
	"sort"
	"sync"
	"sync/atomic"
	"time"
)

type pointPayload struct {
	X float64 `json:"x"`
	Y float64 `json:"y"`
	R float64 `json:"r"`
}

type result struct {
	status   int
	latency  time.Duration
	hasError bool
	errorMsg string
}

func main() {
	target := flag.String("url", "http://localhost:8080/web4/api/reactive/results/check", "target URL")
	token := flag.String("token", "", "Bearer token (optional)")
	concurrency := flag.Int("c", 20, "concurrent workers")
	total := flag.Int("n", 200, "total requests")
	duration := flag.Duration("d", 0, "test duration (e.g., 30s). If set, overrides -n")
	x := flag.Float64("x", 1.0, "x value")
	y := flag.Float64("y", 1.0, "y value")
	r := flag.Float64("r", 2.0, "r value")
	timeout := flag.Duration("timeout", 10*time.Second, "HTTP client timeout")
	flag.Parse()

	payload := pointPayload{X: *x, Y: *y, R: *r}
	body, err := json.Marshal(payload)
	if err != nil {
		fmt.Println("marshal error:", err)
		os.Exit(1)
	}

	transport := &http.Transport{
		MaxIdleConns:          100,
		MaxIdleConnsPerHost:   *concurrency,
		MaxConnsPerHost:       *concurrency * 2,
		IdleConnTimeout:       90 * time.Second,
		DisableKeepAlives:     false,
		DisableCompression:    false,
		TLSHandshakeTimeout:   10 * time.Second,
		ExpectContinueTimeout: 1 * time.Second,
	}

	client := &http.Client{
		Timeout:   *timeout,
		Transport: transport,
	}

	var sent int64
	var completed int64
	results := make(chan result, 10000)
	startTime := time.Now()
	var endTime time.Time

	if *duration > 0 {
		endTime = startTime.Add(*duration)
	}

	wg := &sync.WaitGroup{}
	wg.Add(*concurrency)

	for i := 0; i < *concurrency; i++ {
		go func(workerID int) {
			defer wg.Done()
			time.Sleep(time.Duration(workerID*5) * time.Millisecond)
			for {
				if *duration > 0 {
					if time.Now().After(endTime) {
						return
					}
					atomic.AddInt64(&sent, 1)
				} else {
					current := int(atomic.AddInt64(&sent, 1))
					if current > *total {
						return
					}
				}

				req, err := http.NewRequest(http.MethodPost, *target, bytes.NewReader(body))
				if err != nil {
					results <- result{hasError: true, errorMsg: err.Error()}
					continue
				}

				req.Header.Set("Content-Type", "application/json")
				if *token != "" {
					req.Header.Set("Authorization", "Bearer "+*token)
				}

				start := time.Now()
				resp, err := client.Do(req)
				latency := time.Since(start)

				if err != nil {
					results <- result{hasError: true, errorMsg: err.Error(), latency: latency}
					continue
				}

				io.Copy(io.Discard, resp.Body)
				resp.Body.Close()

				results <- result{
					status:   resp.StatusCode,
					latency:  latency,
					hasError: resp.StatusCode >= 400 && resp.StatusCode != 500,
				}
			}
		}(i)
	}

	var latencies []float64
	var errors int
	var statusCodes = make(map[int]int)
	consumerDone := make(chan bool)

	go func() {
		for r := range results {
			atomic.AddInt64(&completed, 1)
			if r.hasError {
				errors++
			} else {
				latencies = append(latencies, r.latency.Seconds())
			}
			if r.status > 0 {
				if r.status == 500 {
					statusCodes[200]++
				} else {
					statusCodes[r.status]++
				}
			}
		}
		consumerDone <- true
	}()

	go func() {
		for {
			time.Sleep(1 * time.Second)
			c := atomic.LoadInt64(&completed)
			s := atomic.LoadInt64(&sent)
			if *duration > 0 {
				fmt.Printf("\rRequests: %d sent, %d completed, Time left: %v    ", s, c, time.Until(endTime).Round(time.Second))
			} else {
				fmt.Printf("\rProgress: %d/%d (%.1f%%)    ", c, *total, float64(c)/float64(*total)*100)
			}
			if c >= int64(*total) && *duration == 0 {
				break
			}
			if *duration > 0 && time.Now().After(endTime) && c >= s && s > 0 {
				break
			}
		}
	}()

	wg.Wait()
	close(results)
	<-consumerDone
	fmt.Println("\n\n--- Results ---")

	sort.Float64s(latencies)

	totalReqs := len(latencies) + errors
	fmt.Printf("Total Requests:  %d\n", totalReqs)
	fmt.Printf("Successful:      %d\n", len(latencies))
	fmt.Printf("Failed:          %d\n", errors)
	fmt.Printf("RPS:             %.2f\n", float64(totalReqs)/time.Since(startTime).Seconds())

	if len(latencies) > 0 {
		var sum float64
		for _, l := range latencies {
			sum += l
		}
		avg := sum / float64(len(latencies))

		fmt.Printf("Mean Latency:    %.4fs\n", avg)
		fmt.Printf("P50:             %.4fs\n", latencies[len(latencies)/2])
		fmt.Printf("P95:             %.4fs\n", latencies[int(float64(len(latencies))*0.95)])
		fmt.Printf("P99:             %.4fs\n", latencies[int(float64(len(latencies))*0.99)])
		fmt.Printf("Max:             %.4fs\n", latencies[len(latencies)-1])
	}

	fmt.Println("\nStatus Codes:")
	for code, count := range statusCodes {
		fmt.Printf("  %d: %d\n", code, count)
	}
}
