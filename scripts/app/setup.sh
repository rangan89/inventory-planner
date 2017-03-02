#!/bin/bash

PACKAGE=inventory-planner
APP_ID=fk-rp-telescope
ENV_NAME=prod-fk-inventory-planner-env
GROUP_IDENTIFIER=app-fk-inventory-planner-4

# adding the hosts in /etc/hosts
echo "10.47.2.22 d42-a-0002.nm.flipkart.com" >> /etc/hosts
echo "10.65.100.196 wzy-mirror.nm.flipkart.com" >> /etc/hosts
echo "10.84.34.241 repo-svc-app-0001.nm.flipkart.com" >> /etc/hosts
echo '10.65.153.190 flo-gem-repo.nm.flipkart.com' >> /etc/hosts
echo "deb http://10.47.2.22/repos/infra-cli/3 /" > /etc/apt/sources.list.d/infra-cli-svc.list

# doing this since confd doesn't seem to be starting
mkdir -p /etc/service/fk-config-service-confd

# install infra-cli (contains repo-service cli)
apt-get update
apt-get install --yes --allow-unauthenticated infra-cli

# include app repo in sources.list.d
echo "Add $PACKAGE sources"
reposervice --host 10.85.51.142 --port 8080 env --name ${ENV_NAME} --appkey ${APP_ID} > /etc/apt/sources.list.d/${APP_ID}.list

# setup fk-supply-chain user/group
APP_USER=fk-supply-chain
APP_UID=4525
GROUP=fk-supply-chain
GROUP_ID=4500

if ! getent group ${GROUP} > /dev/null; then
  groupadd -g ${GROUP_ID} ${GROUP}
fi

if ! getent passwd ${APP_UID} > /dev/null; then
  adduser --system --uid ${APP_UID} --home /usr/share/${PACKAGE} --no-create-home --ingroup ${GROUP} --disabled-password --shell /bin/false ${APP_USER}
fi

apt-get update
sudo apt-get install --yes --allow-unauthenticated python-pip

# install package
apt-get update
apt-get install --yes --allow-unauthenticated fk-config-service-confd
apt-get install --yes --allow-unauthenticated fk-config-service-sidekick
apt-get install --yes --allow-unauthenticated libmysqlclient-dev
apt-get install --yes --allow-unauthenticated libcouchbase2-core
apt-get install --yes --allow-unauthenticated libcouchbase-dev
apt-get install --yes --allow-unauthenticated nginx
apt-get install --yes --allow-unauthenticated stream-relay
apt-get install --yes --allow-unauthenticated fk-rsyslog
apt-get install --yes --allow-unauthenticated fk-w3-product-service
apt-get install --yes --allow-unauthenticated fk-w3-product-service-dashboard
apt-get install --yes --allow-unauthenticated fk-3p-mail
apt-get install --yes --allow-unauthenticated fk-nagios-common

# nagios setup
echo "team_name=retail-rp" >  /etc/default/nsca_wrapper
echo 'nagios_server_ip="10.47.2.198"' >> /etc/default/nsca_wrapper


# restart confd
/etc/init.d/fk-config-service-confd restart

echo "${app_id}-${group_identifier}" > /etc/default/cosmos-service
apt-get install --yes  --allow-unauthenticated cosmos-collectd
apt-get install --yes  --allow-unauthenticated cosmos-jmx
/etc/init.d/cosmos-collectd start
/etc/init.d/cosmos-jmx start
# Enable Apache SSL mod
sudo a2enmod ssl

# install the package
apt-get update
apt-get install --yes --allow-unauthenticated ${package_name}
sed -i -- 's/config-service.nm.flipkart.com/10.85.50.3/g' /usr/bin/fk_sp_download_config

#setup cosmos
echo "${APP_ID}-${GROUP_IDENTIFIER}" > /etc/default/cosmos-service
sudo svc -t /etc/service/stream-relay.svc || true
apt-get install --yes --allow-unauthenticated cosmos-base
apt-get install --yes --allow-unauthenticated cosmos-collectd
apt-get install --yes --allow-unauthenticated cosmos-statsd
/etc/init.d/cosmos-collectd restart
/etc/init.d/cosmos-statsd restart

#TODO: Setup log service

echo "PassengerSpawnMethod smart-lv2
PassengerMaxPoolSize 75
PassengerMinInstances 15
PassengerMaxInstancesPerApp 15
RailsAppSpawnerIdleTime 0" > /etc/apache2/conf.d/passenger

echo "team_name=retail_rp" >  /etc/default/nsca_wrapper
echo 'nagios_server_ip="10.47.0.149"' >> /etc/default/nsca_wrapper

echo "
 #HostName,port and healthcheck_url
 port=32001
 healthcheck_url="/healthcheck"
 nagios_healthcheck_warning=5
 nagios_healthcheck_critical=20

 #Plugins execution Interval in seconds
 interval=300" > /usr/lib/nagios/fk-sp-nagios-healthcheck/configuration/service_healthcheck.conf



mkdir -p /var/log/flipkart/supply-chain/${PACKAGE}/
chmod 777 /var/log/flipkart/supply-chain/${PACKAGE}/*

echo "Starting service"
sudo /etc/init.d/${PACKAGE} restart

# restart log service
sudo /etc/init.d/rsyslog restart
#moving daily logrotate to hourly
sudo mv /etc/cron.daily/logrotate /etc/cron.hourly/logrotate
#starting logrotate
sudo chown root:fk-supply-chain /etc/logrotate.d/${PACKAGE}
sudo /usr/sbin/logrotate /etc/logrotate.conf

echo "It has been done."