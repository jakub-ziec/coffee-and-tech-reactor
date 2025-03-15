#!/bin/bash

if [[ "$#" -eq 0 || "$1" == "-h" || "$1" == "--help" ]]; then
    echo "Usage: $0 <curl|timecurl|load> <blocking|nonblocking|parallel>"
    echo "Description: This script constructs and executes a command based on the provided arguments."
    echo "  - First argument must be one of: curl, timecurl, load."
    echo "  - Second argument must be one of: blocking, nonblocking, parallel."
    echo "  - Use -h or --help to display this message."
    exit 0
fi

if [ "$#" -ne 2 ]; then
    echo "Error: Wrong number of arguments."
    echo "Usage: $0 <curl|timecurl|load> <blocking|nonblocking|parallel>"
    exit 1
fi

if [[ "$1" != "curl" && "$1" != "timecurl" && "$1" != "load" ]]; then
    echo "Error: First argument must be one of: curl, timecurl, load."
    exit 1
fi

if [[ "$2" != "blocking" && "$2" != "nonblocking" && "$2" != "parallel" ]]; then
    echo "Error: Second argument must be one of: blocking, nonblocking, parallel"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CSV_DIR=$SCRIPT_DIR'/baton/data'

if [[ "$2" == "blocking" ]]; then
    URL='http://localhost:8080/blocking/users'
    CSV=$CSV_DIR'/blocking-create-users.csv'
elif [[ "$2" == "nonblocking" ]]; then
    URL='http://localhost:8080/non-blocking/users'
    CSV=$CSV_DIR'/non-blocking-create-users.csv'
elif [[ "$2" == "parallel" ]]; then
    URL='http://localhost:8080/parallel/users'
    CSV=$CSV_DIR'/parallel-create-users.csv'
fi

if [[ "$1" == "curl" ]]; then
    CMD='curl -X POST '$URL'?delay=200 -H "Content-Type: application/json" -d '\''{"email":"joe.doe@example.com"}'\'' --silent | jq'
elif [[ "$1" == "timecurl" ]]; then
    CMD='curl -X POST '$URL'?delay=200 -H "Content-Type: application/json" -d '\''{"email":"joe.doe@example.com"}'\'' -L --output /dev/null --silent --show-error --write-out '\''\n=================================\nTOTAL TIME:         %{time_total}s\n=================================\n'\'''
elif [[ "$1" == "load" ]]; then
    CMD='baton -c 100 -r 110 -z '$CSV
fi

echo "$CMD"

eval $CMD
