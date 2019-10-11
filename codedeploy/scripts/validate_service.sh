#!/bin/bash

n=0
until [ $n -ge 24 ]
do
	result=$(curl -s http://localhost:8080/actuator/health)
	if [[ $result == *"UP"* ]]; then
	    exit 0
	else
	    n=$[$n+1]
	    sleep 5
	fi
done
exit 1