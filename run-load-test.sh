#!/bin/bash

# Configurable Load Tester Runner Script
# Usage: ./run-load-test.sh [TPS] [DURATION]

set -e

# Default values
DEFAULT_TPS=60
DEFAULT_DURATION=60

# Get arguments or use defaults
TPS=${1:-$DEFAULT_TPS}
DURATION=${2:-$DEFAULT_DURATION}

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║      Airtel TV Load Tester - Configuration               ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo -e "${GREEN}Target TPS:${NC} $TPS"
echo -e "${GREEN}Duration:${NC} $DURATION seconds"
echo -e "${YELLOW}Starting load test...${NC}\n"

# Check if compiled classes exist
if [ ! -d "target/classes" ]; then
    echo -e "${YELLOW}Compiled classes not found. Compiling...${NC}"
    mvn clean compile -DskipTests
fi

# Run the load tester
mvn exec:java -Dexec.mainClass="in.wynk.secret.manager.loadtest.ConfigurableLoadTester" \
    -Dexec.args="$TPS $DURATION" \
    -Dexec.cleanupDaemonThreads=false

echo -e "\n${GREEN}Load test completed!${NC}"
