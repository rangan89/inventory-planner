#!/bin/bash
echo '
mkdir -p /etc/${PACKAGE}/
chmod 777 /etc/${PACKAGE}

mkdir -p /var/log/flipkart/rp/${PACKAGE}/
chmod -R 777 /var/log/flipkart/rp/${PACKAGE}/

chown -R ${USERNAME}:${GROUPNAME} /var/log/flipkart/rp/${PACKAGE}
' >> debian/control/postinst
