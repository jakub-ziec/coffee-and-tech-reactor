#!/bin/bash

if [[ "$#" -eq 0 || "$1" == "-h" || "$1" == "--help" ]]; then
    echo "Usage: $0 <curl|timecurl|load> <servlet|webflux>"
    echo "Description: This script constructs and executes a command based on the provided arguments."
    echo "  - First argument must be one of: curl, timecurl, load."
    echo "  - Second argument must be one of: servlet, webflux."
    echo "  - Use -h or --help to display this message."
    exit 0
fi

if [ "$#" -ne 2 ]; then
    echo "Error: Wrong number of arguments."
    echo "Usage: $0 <curl|timecurl|load> <servlet|webflux>"
    exit 1
fi

if [[ "$1" != "curl" && "$1" != "timecurl" && "$1" != "load" ]]; then
    echo "Error: First argument must be one of: curl, timecurl, load."
    exit 1
fi

if [[ "$2" != "servlet" && "$2" != "webflux" ]]; then
    echo "Error: Second argument must be one of: servlet, webflux"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CSV_DIR=$SCRIPT_DIR'/baton/data'

if [[ "$2" == "servlet" ]]; then
    URL='http://localhost:8080/users'
    CSV=$CSV_DIR'/servlet-create-users.csv'
elif [[ "$2" == "webflux" ]]; then
    URL='http://localhost:8081/users'
    CSV=$CSV_DIR'/webflux-create-users.csv'
fi

if [[ "$1" == "curl" ]]; then
    CMD='curl -X POST '$URL' -H "Content-Type: application/json" -d '\''{"email":"john.doe@example.com"}'\'' --silent | jq'
elif [[ "$1" == "timecurl" ]]; then
    CMD='curl -X POST '$URL' -H "Content-Type: application/json" -d '\''{"email":"john.doe@example.com"}'\'' -L --output /dev/null --silent --show-error --write-out '\''\n=================================\nTOTAL TIME:         %{time_total}s\n=================================\n'\'''
elif [[ "$1" == "load" ]]; then
    CMD='baton -c 100 -r 110 -z '$CSV
fi

echo "$CMD"

eval $CMD
