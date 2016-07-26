resource "template_file" "haproxy_redirect" {
  count = "${var.stardogs.size}"
  template = "${file("haproxy/haproxy_redirect.tpl")}"
  vars {
    id = "${count.index + 1}"
    host = "${lookup(var.stardogs, count.index)}"
  }
}

resource "template_file" "haproxy_conf" {
  template = "${file("haproxy/haproxy.cfg.tpl")}"
  vars {
    redirects = "${join("    ", template_file.haproxy_redirect.*.rendered)}"
  }
}

resource "aws_instance" "haproxy" {

  instance_type = "${var.haproxy_type}"

  availability_zone = "${var.aws_az}"

  # not supported with t1.micro
  placement_group = "stardog-cluster"

  # Lookup the correct AMI based on the region
  # we specified
  ami = "${lookup(var.aws_amis, var.aws_region)}"

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

  private_ip = "${var.haproxy_ip}"

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
    Name = "HA Proxy"
  }

  # Provisioning settings

  # Setup script
  provisioner "remote-exec" {
    scripts = ["haproxy/setup.sh"]
  }

  provisioner "file" {
    source = "${var.stardog_dist}"
    destination = "/opt/stardog/stardog.zip"
  }

  provisioner "remote-exec" {
    inline = [
      "echo '${template_file.haproxy_conf.rendered}' > /var/haproxy/haproxy.cfg",
      "sudo haproxy -f /var/haproxy/haproxy.cfg"
    ]
  }

  # Export system variable to be able to use it in other scripts
  provisioner "local-exec" {
    command = "export STARDOG_CLUSTER='${self.public_dns}'"
  }
}
