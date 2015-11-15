#!/bin/bash 
set -e

test() {
  lein test
}

push() {
  test && git push
}

release() {
  test && lein release && scripts/github-release.sh
}

if type $1 &>/dev/null; then
    $1 $2
else
    echo "usage: $0 <goal>

goal:
    test     -- run unit tests
    push     -- run all tests and push current state
    release  -- release current version"

    exit 1
fi
