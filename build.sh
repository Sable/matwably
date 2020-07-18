#!/usr/bin/env bash
# Finally call gradle build jar task
./gradlew make_jar -x test
# If Library
# gradle make_jar -PMATMACHJS_LIB="/Users/davidherrera/Documents/Research/Thesis/code/mc2wasm-lib/bin" -x test
