#!/usr/bin/env zsh
dir=$(dirname ${(%):-%N})
str="'$*'"
$dir/gradlew run --args="'$str'"
