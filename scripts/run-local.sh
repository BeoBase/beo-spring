#!/usr/bin/env bash
#
# run-local.sh
#
# Retrieves local-development secrets from Bitwarden Secrets Manager
# and outputs them as environment variables for IntelliJ.
#
# This script does NOT start Spring Boot.

set -e

# ------------------------------------------------------------
# 1. Check BWS authentication
# ------------------------------------------------------------

if [ -z "$BWS_ACCESS_TOKEN" ]; then
    echo "Error: BWS_ACCESS_TOKEN is not set." >&2
    echo ""
    echo "Set it first with:"
    echo 'export BWS_ACCESS_TOKEN="YOUR_TOKEN_HERE"'
    exit 1
fi

# ------------------------------------------------------------
# 2. Check dependencies (bws and jq)
# ------------------------------------------------------------

if ! command -v bws &> /dev/null; then
    echo "Error: bws is not installed or not in PATH." >&2
    echo "Install via Homebrew: brew install bitwarden-cli" >&2
    exit 1
fi

if ! command -v jq &> /dev/null; then
    echo "Error: jq is not installed or not in PATH." >&2
    echo "Install via Homebrew: brew install jq" >&2
    exit 1
fi

# ------------------------------------------------------------
# 3. Bitwarden Secret IDs
# ------------------------------------------------------------

DB_PASSWORD_ID="621ac999-07ba-4acc-8f16-b4a00180f013"
DB_URL_ID="26c92fa6-7d2b-4710-8cbe-b4a00180d274"
DB_USERNAME_ID="42309dce-514e-41bd-bd10-b4a00180e2fa"

# ------------------------------------------------------------
# 4. Retrieve secrets
# ------------------------------------------------------------

echo "Loading secrets from Bitwarden..."

DB_PASSWORD=$(bws secret get "$DB_PASSWORD_ID" --output json | jq -r '.value')
DB_URL=$(bws secret get "$DB_URL_ID" --output json | jq -r '.value')
DB_USERNAME=$(bws secret get "$DB_USERNAME_ID" --output json | jq -r '.value')

# ------------------------------------------------------------
# 5. Verify retrieval
# ------------------------------------------------------------

if [ -z "$DB_PASSWORD" ] || [ "$DB_PASSWORD" == "null" ]; then
    echo "Error: DB_PASSWORD could not be retrieved." >&2
    exit 1
fi

if [ -z "$DB_URL" ] || [ "$DB_URL" == "null" ]; then
    echo "Error: DB_URL could not be retrieved." >&2
    exit 1
fi

if [ -z "$DB_USERNAME" ] || [ "$DB_USERNAME" == "null" ]; then
    echo "Error: DB_USERNAME could not be retrieved." >&2
    exit 1
fi

# ------------------------------------------------------------
# 6. Write secrets to .env file for IntelliJ
# ------------------------------------------------------------

# Resolve the parent directory (project root) relative to where this script lives
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

cat <<EOF > "$PROJECT_ROOT/.env"
# .env file
DB_URL=$DB_URL
DB_USERNAME=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD
EOF

echo "Updated .env file with fresh secrets."