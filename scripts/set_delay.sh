#!/bin/bash

if [[ "$#" -eq 0 || "$1" == "-h" || "$1" == "--help" ]]; then
  echo "Usage: $0 <delay>"
  echo "  <delay>  Fixed delay in milliseconds (integer)"
  exit 0
fi

if [ "$#" -ne 1 ]; then
    echo "Error: Wrong number of arguments."
    echo "Usage: $0 <delay>"
    exit 1
fi

if ! [[ "$1" =~ ^[0-9]+$ ]]; then
  echo "Error: Delay must be an integer."
  exit 1
fi

DELAY=$1

CMD='curl -X POST http://localhost:9999/__admin/settings -H "Content-Type: application/json" -d '\''{"fixedDelay": '$DELAY'}'\'''

echo "$CMD"

eval $CMD

eval 'curl -X GET http://localhost:9999/__admin/settings --silent | jq'

