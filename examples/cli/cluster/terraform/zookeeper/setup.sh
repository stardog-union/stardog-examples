#!/bin/bash -v

# Install ZooKeeper
zookeeper_version=3.4.6
sudo wget http://mirror.metrocast.net/apache/zookeeper/zookeeper-${zookeeper_version}/zookeeper-${zookeeper_version}.tar.gz && sudo tar -xvzf zookeeper-${zookeeper_version}.tar.gz
sudo mv zookeeper-${zookeeper_version} /usr/local
sudo chgrp -R ubuntu /usr/local/zookeeper-${zookeeper_version}
sudo chmod 775 -R /usr/local/zookeeper-${zookeeper_version}

# Create zk data directory
sudo mkdir -p /var/zkdata
sudo chgrp -R ubuntu /var/zkdata
sudo chmod 775 -R /var/zkdata
