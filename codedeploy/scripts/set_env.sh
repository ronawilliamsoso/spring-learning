#!/bin/bash
sed -i -- 's/%DEPLOYMENT_GROUP_NAME%/'"$DEPLOYMENT_GROUP_NAME"'/g' /opt/${build.finalName}/${build.finalName}.conf