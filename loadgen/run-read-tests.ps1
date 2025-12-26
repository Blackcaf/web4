param(
    [string]$Token = ""
)

$BASE_URL = "http://localhost:8080/web4"
# Note: GET endpoints do not have /check suffix
$REACTIVE_URL = "$BASE_URL/api/reactive/results"
$TRADITIONAL_URL = "$BASE_URL/api/v1/results"
$CACHED_URL = "$BASE_URL/api/v1/cached/results"

Write-Host "=========================================="
Write-Host "READ Load Testing: Reactive vs Traditional vs Cached"
Write-Host "=========================================="
Write-Host ""

$goPath = Get-Command go -ErrorAction SilentlyContinue
if (-not $goPath) {
    Write-Host "ERROR: Go is not installed or not in PATH!" -ForegroundColor Red
    exit 1
}

if ([string]::IsNullOrEmpty($Token)) {
    Write-Host "Warning: No token provided. Requests will likely fail with 401/403." -ForegroundColor Yellow
    Write-Host "Usage: .\run-read-tests.ps1 -Token <your-jwt-token>"
    Write-Host ""
}

$CONCURRENCY = 50
$DURATION = "30s"

Write-Host "Test Configuration:"
Write-Host "  Method: GET"
Write-Host "  Concurrency: $CONCURRENCY workers"
Write-Host "  Duration: $DURATION"
Write-Host ""

if (-not (Test-Path "results")) {
    New-Item -ItemType Directory -Path "results" | Out-Null
}

$TIMESTAMP = Get-Date -Format "yyyyMMdd_HHmmss"

# 1. Reactive Test
Write-Host "=========================================="
Write-Host "Testing REACTIVE endpoint (GET)"
Write-Host "=========================================="
Write-Host "URL: $REACTIVE_URL"
go run main.go -url "$REACTIVE_URL" -method "GET" -token "$Token" -c $CONCURRENCY -d $DURATION | Tee-Object -FilePath "results/read_reactive_$TIMESTAMP.txt"
Write-Host ""

# 2. Traditional Test
Write-Host "=========================================="
Write-Host "Testing TRADITIONAL endpoint (GET)"
Write-Host "=========================================="
Write-Host "URL: $TRADITIONAL_URL"
go run main.go -url "$TRADITIONAL_URL" -method "GET" -token "$Token" -c $CONCURRENCY -d $DURATION | Tee-Object -FilePath "results/read_traditional_$TIMESTAMP.txt"
Write-Host ""

# 3. Cached Test
Write-Host "=========================================="
Write-Host "Testing CACHED endpoint (GET)"
Write-Host "=========================================="
Write-Host "URL: $CACHED_URL"
go run main.go -url "$CACHED_URL" -method "GET" -token "$Token" -c $CONCURRENCY -d $DURATION | Tee-Object -FilePath "results/read_cached_$TIMESTAMP.txt"
Write-Host ""

Write-Host "Done! Results saved in 'results' folder."

