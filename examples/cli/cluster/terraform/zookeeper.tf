##########################
# ZooKeeper resources
##########################

resource "template_file" "zk_server" {
  count = "${var.zookeepers.size}"
  template = "${file("zookeeper/server-spec.tpl")}"
  vars {
    id = "${count.index + 1}"
    host = "${lookup(var.zookeepers, count.index)}"
  }
}

resource "template_file" "zk_conf" {
  template = "${file("zookeeper/zoo.cfg.tpl")}"
  vars {
    zk_server_list = "${join("", template_file.zk_server.*.rendered)}"
  }
}

resource "aws_instance" "zookeeper" {
  instance_type = "${var.zookeeper_type}"

  count = "${var.zookeepers.size}"

  availability_zone = "${var.aws_az}"

  # not supported with t1.micro
  placement_group = "stardog-cluster"

  # Lookup the correct AMI based on the region
  # we specified
  ami = "${lookup(var.zookeeper_ami, var.aws_region)}"

  # Settings for SSH connection
  connection {
    # The default username for our AMI
    user = "ubuntu"

    # The path to your key file
    key_file = "${var.key_path}"

    agent = false
  }

  key_name = "${var.key_name}"

  vpc_security_group_ids = ["${aws_security_group.stardog-cluster.id}"]

  subnet_id = "${aws_subnet.terraform.id}"

  private_ip = "${lookup(var.zookeepers, count.index)}"

  # making sure the root block has 8 GB
  root_block_device {
    volume_size = "8"
  }

  # adding some storage to hold STARDOG_HOME
  #ebs_block_device {
  #  device_name = "/dev/sdb"
  #  volume_size = "30"
  #  delete_on_termination = true
  #}

  # Instance tags
  tags {
    Name = "ZooKeeper node"
  }

  # Provisioning settings

  # Setup script
  provisioner "remote-exec" {
    scripts = ["zookeeper/setup.sh"]
  }

  provisioner "remote-exec" {
    inline = [
      "echo '${template_file.zk_conf.rendered}' > /usr/local/zookeeper-3.4.6/conf/zoo.cfg",
      "echo ${count.index + 1} > /var/zkdata/myid",
      "/usr/local/zookeeper-3.4.6/bin/zkServer.sh start"
    ]
  }
}
