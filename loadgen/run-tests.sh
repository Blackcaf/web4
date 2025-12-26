#!/bin/bash

TOKEN=$1
BASE_URL="http://localhost:8080/web4"
REACTIVE_URL="$BASE_URL/api/reactive/results/check"
TRADITIONAL_URL="$BASE_URL/api/v1/results/check"

echo "=========================================="
echo "Load Testing: Reactive vs Traditional"
echo "=========================================="
echo ""

if ! command -v go &> /dev/null; then
    echo "ERROR: Go is not installed or not in PATH!"
    echo "Please install Go from https://golang.org/dl/"
    exit 1
fi

if [ -z "$TOKEN" ]; then
    echo "Warning: No token provided. Some requests may fail."
    echo "Usage: ./run-tests.sh <your-jwt-token>"
    echo ""
fi

CONCURRENCY=50
TOTAL_REQUESTS=1000
DURATION="30s"

echo "Test Configuration:"
echo "  Concurrency: $CONCURRENCY workers"
echo "  Total requests: $TOTAL_REQUESTS"
echo "  Duration: $DURATION"
echo ""

mkdir -p results

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

echo "=========================================="
echo "Testing REACTIVE endpoint"
echo "=========================================="
echo "URL: $REACTIVE_URL"
echo ""

OUTPUT_FILE="results/reactive_$TIMESTAMP.txt"

if [ -z "$TOKEN" ]; then
    go run main.go -url "$REACTIVE_URL" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | tee "$OUTPUT_FILE"
else
    go run main.go -url "$REACTIVE_URL" -token "$TOKEN" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | tee "$OUTPUT_FILE"
fi

if [ -f "$OUTPUT_FILE" ]; then
    echo ""
    echo "Results saved to: $OUTPUT_FILE"
else
    echo "ERROR: Failed to run test."
fi
echo ""

echo "Waiting 5 seconds before next test..."
sleep 5

echo "=========================================="
echo "Testing TRADITIONAL endpoint"
echo "=========================================="
echo "URL: $TRADITIONAL_URL"
echo ""

OUTPUT_FILE="results/traditional_$TIMESTAMP.txt"

if [ -z "$TOKEN" ]; then
    go run main.go -url "$TRADITIONAL_URL" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | tee "$OUTPUT_FILE"
else
    go run main.go -url "$TRADITIONAL_URL" -token "$TOKEN" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | tee "$OUTPUT_FILE"
fi

if [ -f "$OUTPUT_FILE" ]; then
    echo ""
    echo "Results saved to: $OUTPUT_FILE"
else
    echo "ERROR: Failed to run test."
fi
echo ""

echo "=========================================="
echo "Tests completed!"
echo "=========================================="
echo "Compare results in: results/"

