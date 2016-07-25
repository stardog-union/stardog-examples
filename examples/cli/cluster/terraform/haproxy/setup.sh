#!/bin/bash -v

# Install haproxy
sudo add-apt-repository ppa:vbernat/haproxy-1.6 -y

sudo apt-get update -y
sudo apt-get install haproxy -y

haproxy_dir=/var/haproxy
sudo mkdir ${haproxy_dir}
sudo chown -R ubuntu ${haproxy_dir}
sudo chmod 775 ${haproxy_dir}

# static http server for copying the stardog binaries
stardog_dir=/opt/stardog
sudo mkdir -p ${stardog_dir}
sudo chown -R ubuntu ${stardog_dir}
cd ${stardog_dir}
sudo chown -R ubuntu /usr/local/bin

echo "python3 -m http.server 8080 > /dev/null &" > /usr/local/bin/httpserver
sudo chmod +x /usr/local/bin/httpserver
nohup httpserver
