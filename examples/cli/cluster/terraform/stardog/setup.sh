#!/bin/bash -v

sudo apt-get update
sudo apt-get install unzip -y

DEVICE=/dev/xvdd
MOUNT_POINT=/mnt/data
# mount the ebs drive
sudo mkfs -t ext4 $DEVICE
sudo mkdir $MOUNT_POINT
sudo mount $DEVICE $MOUNT_POINT

# Create stardog-home
sudo mkdir -p $MOUNT_POINT/stardog-home
sudo chmod 775 -R $MOUNT_POINT/stardog-home
sudo chown -R ubuntu $MOUNT_POINT/stardog-home

# Make some directories writable for the main user
sudo chmod 775 -R /usr/local
sudo chown -R ubuntu /usr/local

sudo mkdir /mnt/.tmp
sudo chown -R ubuntu /mnt/.tmp
sudo chmod 775 -R /mnt/.tmp

pushd /usr/local
	# todo - do not hardcode the address?
	wget http://10.0.1.25:8080/stardog.zip
	unzip stardog.zip
	sd_dir=$(ls | `which grep` stardog-)
	if [ -z "${sd_dir}" ]; then
		echo "error setting up stardog"
		exit 1
	fi
	mv /usr/local/${sd_dir} /usr/local/stardog
popd
