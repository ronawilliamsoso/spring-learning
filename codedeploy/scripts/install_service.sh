#!/bin/bash
mv /opt/${build.finalName}/application.conf /opt/${build.finalName}/${build.finalName}.conf
mv /etc/logrotate.d/application-logrotate /etc/logrotate.d/${build.finalName}
mkdir -p /var/log/${build.finalName}/
ln -sf /opt/${build.finalName}/${build.finalName}.jar /etc/init.d/${build.finalName}
chkconfig --levels 3 ${build.finalName} on