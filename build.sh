#!/usr/bin/env bash
#cd ./libs/mclab-core/languages/Natlab/
#ant jar
#cd -

# Finally call gradle build jar task
gradle build -x test -PMATMACHJS_LIB=/Users/davidherrera/Documents/Research/Thesis/code/mc2wasm-lib/bin