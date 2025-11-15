#!/bin/bash

echo "Testing CI application with VP..."

# Test 1: Orden 2 con CI
curl -s -X POST http://localhost:8080/solve \
  -H "Content-Type: application/json" \
  -d '{
    "equation": "y' + y = sin(x)",
    "variable": "x",
    "method": "VP",
    "initialConditions": ["y(0)=1", "y'\''(0)=0"]
  }' | jq '.finalSolution'

