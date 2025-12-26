param(
    [string]$Token = ""
)

$BASE_URL = "http://localhost:8080/web4"
$REACTIVE_URL = "$BASE_URL/api/reactive/results/check"
$TRADITIONAL_URL = "$BASE_URL/api/v1/results/check"
$CACHED_URL = "$BASE_URL/api/v1/cached/results/check"

Write-Host "=========================================="
Write-Host "Load Testing: Reactive vs Traditional vs Cached"
Write-Host "=========================================="
Write-Host ""

$goPath = Get-Command go -ErrorAction SilentlyContinue
if (-not $goPath) {
    Write-Host "ERROR: Go is not installed or not in PATH!" -ForegroundColor Red
    Write-Host "Please install Go from https://golang.org/dl/"
    Write-Host "Or add Go to your PATH environment variable."
    Read-Host "Press Enter to exit"
    exit 1
}

if ([string]::IsNullOrEmpty($Token)) {
    Write-Host "Warning: No token provided. Some requests may fail." -ForegroundColor Yellow
    Write-Host "Usage: .\run-tests.ps1 -Token <your-jwt-token>"
    Write-Host ""
}

$CONCURRENCY = 50
$TOTAL_REQUESTS = 1000
$DURATION = "30s"

Write-Host "Test Configuration:"
Write-Host "  Concurrency: $CONCURRENCY workers"
Write-Host "  Total requests: $TOTAL_REQUESTS"
Write-Host "  Duration: $DURATION"
Write-Host ""

if (-not (Test-Path "results")) {
    New-Item -ItemType Directory -Path "results" | Out-Null
}

$TIMESTAMP = Get-Date -Format "yyyyMMdd_HHmmss"

Write-Host "=========================================="
Write-Host "Testing REACTIVE endpoint"
Write-Host "=========================================="
Write-Host "URL: $REACTIVE_URL"
Write-Host ""

$OUTPUT_FILE = "results\reactive_$TIMESTAMP.txt"

if ([string]::IsNullOrEmpty($Token)) {
    go run main.go -url "$REACTIVE_URL" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | Tee-Object -FilePath $OUTPUT_FILE
} else {
    go run main.go -url "$REACTIVE_URL" -token "$Token" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | Tee-Object -FilePath $OUTPUT_FILE
}

if (Test-Path $OUTPUT_FILE) {
    Write-Host ""
    Write-Host "Results saved to: $OUTPUT_FILE" -ForegroundColor Green
} else {
    Write-Host "ERROR: Failed to run test. Check if Go is installed correctly." -ForegroundColor Red
}
Write-Host ""

Write-Host "Waiting 5 seconds before next test..."
Start-Sleep -Seconds 5

Write-Host "=========================================="
Write-Host "Testing TRADITIONAL endpoint"
Write-Host "=========================================="
Write-Host "URL: $TRADITIONAL_URL"
Write-Host ""

$OUTPUT_FILE = "results\traditional_$TIMESTAMP.txt"

if ([string]::IsNullOrEmpty($Token)) {
    go run main.go -url "$TRADITIONAL_URL" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | Tee-Object -FilePath $OUTPUT_FILE
} else {
    go run main.go -url "$TRADITIONAL_URL" -token "$Token" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | Tee-Object -FilePath $OUTPUT_FILE
}

if (Test-Path $OUTPUT_FILE) {
    Write-Host ""
    Write-Host "Results saved to: $OUTPUT_FILE" -ForegroundColor Green
} else {
    Write-Host "ERROR: Failed to run test. Check if Go is installed correctly." -ForegroundColor Red
}
Write-Host ""

Write-Host "Waiting 5 seconds before next test..."
Start-Sleep -Seconds 5

Write-Host "=========================================="
Write-Host "Testing CACHED endpoint"
Write-Host "=========================================="
Write-Host "URL: $CACHED_URL"
Write-Host ""

$OUTPUT_FILE = "results\cached_$TIMESTAMP.txt"

if ([string]::IsNullOrEmpty($Token)) {
    go run main.go -url "$CACHED_URL" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | Tee-Object -FilePath $OUTPUT_FILE
} else {
    go run main.go -url "$CACHED_URL" -token "$Token" -c $CONCURRENCY -n $TOTAL_REQUESTS -d $DURATION | Tee-Object -FilePath $OUTPUT_FILE
}

if (Test-Path $OUTPUT_FILE) {
    Write-Host ""
    Write-Host "Results saved to: $OUTPUT_FILE" -ForegroundColor Green
} else {
    Write-Host "ERROR: Failed to run test. Check if Go is installed correctly." -ForegroundColor Red
}
Write-Host ""

Write-Host "=========================================="
Write-Host "Tests completed!"
Write-Host "=========================================="
Write-Host "Compare results in: results\" -ForegroundColor Cyan

