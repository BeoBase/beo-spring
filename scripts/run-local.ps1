
# run-local.ps1
#
# Retrieves local-development secrets from Bitwarden Secrets Manager
# and outputs them as environment variables for IntelliJ.
#
# This script does NOT start Spring Boot.

$ErrorActionPreference = "Stop"

# ------------------------------------------------------------
# 1. Check BWS authentication
# ------------------------------------------------------------

if (-not $env:BWS_ACCESS_TOKEN) {
    Write-Error "BWS_ACCESS_TOKEN is not set."
    Write-Host ""
    Write-Host "Set it first with:"
    Write-Host '$env:BWS_ACCESS_TOKEN="YOUR_TOKEN_HERE"'
    exit 1
}

# ------------------------------------------------------------
# 2. Check that bws is installed
# ------------------------------------------------------------

if (-not (Get-Command bws -ErrorAction SilentlyContinue)) {
    Write-Error "bws is not installed or is not in PATH."
    exit 1
}

# ------------------------------------------------------------
# 3. Bitwarden Secret IDs
# ------------------------------------------------------------

$DB_PASSWORD_ID = "621ac999-07ba-4acc-8f16-b4a00180f013"
$DB_URL_ID      = "26c92fa6-7d2b-4710-8cbe-b4a00180d274"
$DB_USERNAME_ID = "42309dce-514e-41bd-bd10-b4a00180e2fa"
$JWT_SECRET_ID  = "5cb9a98a-e16e-494c-addd-b4a60144bdd6"

# ------------------------------------------------------------
# 4. Retrieve secrets
# ------------------------------------------------------------

Write-Host "Loading secrets from Bitwarden..."

$dbPassword = bws secret get $DB_PASSWORD_ID --output json | ConvertFrom-Json
$dbUrl      = bws secret get $DB_URL_ID      --output json | ConvertFrom-Json
$dbUsername = bws secret get $DB_USERNAME_ID --output json | ConvertFrom-Json
$jwtSecret  = bws secret get $JWT_SECRET_ID  --output json | ConvertFrom-Json

# ------------------------------------------------------------
# 5. Verify retrieval
# ------------------------------------------------------------

if (-not $dbPassword.value) {
    Write-Error "DB_PASSWORD could not be retrieved."
    exit 1
}

if (-not $dbUrl.value) {
    Write-Error "DB_URL could not be retrieved."
    exit 1
}

if (-not $dbUsername.value) {
    Write-Error "DB_USERNAME could not be retrieved."
    exit 1
}

if (-not $jwtSecret.value) {
    Write-Error "JWT_SECRET could not be retrieved."
    exit 1
}

# ------------------------------------------------------------
# 6. Write secrets to .env file for IntelliJ
# ------------------------------------------------------------

# Resolve the parent directory (project root) relative to where this script lives
$ProjectRoot = Join-Path -Path $PSScriptRoot -ChildPath ".."

@"
# .env file

DB_URL=$($dbUrl.value)
DB_USERNAME=$($dbUsername.value)
DB_PASSWORD=$($dbPassword.value)

JWT_SECRET=$($jwtSecret.value)
"@ | Set-Content -Path "$ProjectRoot\.env" -Encoding utf8

Write-Host "Updated .env file with fresh secrets."
