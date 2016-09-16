#!/bin/bash
mkdir -p junit-res/de/wwu/muggl/binaryTestSuite
rsync -avP --stats build/classes/test/de/wwu/muggl/binaryTestSuite junit-res/de/wwu/muggl/binaryTestSuite
