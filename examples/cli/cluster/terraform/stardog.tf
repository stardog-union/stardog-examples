##########################
# Stardog node
##########################

resource "template_file" "zk_conn" {
  count = "${var.zookeepers.size}"
  template = "${file("stardog/zk_conn_server.tpl")}"
  vars {
    zk_server = "${lookup(var.zookeepers, count.index)}"
  }
}

resource "template_file" "stardog_props" {
  count = "${var.stardogs.size}"
  template = "${file("stardog/stardog.properties.tpl")}"
  vars {
    host = "${lookup(var.stardogs, count.index)}"
    zk_conn_string = "${replace(join(",", template_file.zk_conn.*.rendered), "\n", "")}"
  }
}

resource "aws_instance" "stardog" {
  depends_on = ["aws_instance.zookeeper", "aws_instance.haproxy"]

  instance_type = "${var.stardog_type}"

  availability_zone = "${var.aws_az}"

  count = "${var.stardogs.size}"

  # not supported with t1.micro
  placement_group = "stardog-cluster"

  # Lookup the correct AMI based on the region
  # we specified
  ami = "${lookup(var.stardog_ami, var.aws_region)}"

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

  private_ip = "${lookup(var.stardogs, count.index)}"

  # making sure the root block has 8 GB
  root_block_device {
    volume_size = "8"
  }

  ephemeral_block_device {
    device_name = "/dev/sdb"
    virtual_name = "ephemeral0"
  }

  ephemeral_block_device {
    device_name = "/dev/sdc"
    virtual_name = "ephemeral1"
  }

  # adding some storage to hold STARDOG_HOME
  ebs_block_device {
    device_name = "/dev/sdd"
    volume_size = "700" // minimum size for max iops (20k)
    volume_type = "io1"
    iops = "10000"
    delete_on_termination = true
  }

  # Instance tags
  tags {
    Name = "stardog cluster node"
  }

  # Provisioning settings

  # Setup scripts
  provisioner "remote-exec" {
    scripts = ["stardog/setup.sh"]
  }

//  # Provision stardog
//  provisioner "file" {
//    source = "${var.stardog_dist}"
//    destination = "/usr/local/stardog"
//  }

  # Provision stardog license
  provisioner "file" {
    source = "${var.stardog_license}"
    destination = "/mnt/data/stardog-home/stardog-license-key.bin"
  }

  # Provision logging settings
  provisioner "file" {
    source = "stardog/log4j2.xml"
    destination = "/mnt/data/stardog-home/log4j2.xml"
  }

  provisioner "remote-exec" {
    inline = [
      "echo '${element(template_file.stardog_props.*.rendered, count.index)}' > /mnt/data/stardog-home/stardog.properties"
    ]
  }

  provisioner "remote-exec" {
    script = "stardog/start.sh"
  }
}
