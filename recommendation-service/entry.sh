#!/bin/bash
echo "Sleeping for 3 seconds while services start"
runsvdir-start&
sleep 3
cd /quickstartapp
./run.sh
bash