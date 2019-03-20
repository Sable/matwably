#!/usr/bin/env bash

if [[ ! -f ./build/scripts/matwably ]]; then echo "Runner file not present, build the project first";fi
./build/scripts/matwably $@