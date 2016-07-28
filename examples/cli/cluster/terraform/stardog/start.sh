#!/bin/bash -v

# make sure that stardog scripts have execution permissions
chmod 775 /usr/local/stardog/bin/stardog
chmod 775 /usr/local/stardog/bin/stardog-admin

# Create symlink to stardog scripts
sudo ln -s /usr/local/stardog/bin/stardog /usr/local/bin/stardog
sudo ln -s /usr/local/stardog/bin/stardog-admin /usr/local/bin/stardog-admin

tmp_dir=/mnt/.tmp

export STARDOG_HOME=/mnt/data/stardog-home
export STARDOG_JAVA_ARGS="-Xms8g -Xmx8g -XX:MaxDirectMemorySize=10g -Djava.io.tmpdir=${tmp_dir}"
echo "export STARDOG_HOME=${STARDOG_HOME}" >> /home/ubuntu/.bashrc
echo "export STARDOG_JAVA_ARGS='${STARDOG_JAVA_ARGS}'" >> /home/ubuntu/.bashrc

# Execute server start
ulimit -n 2048
nohup stardog-admin server start --disable-security
