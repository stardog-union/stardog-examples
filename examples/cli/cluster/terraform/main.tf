# Specify the provider and access details
provider "aws" {
  region = "${var.aws_region}"
}

##########################
# Cluster's network setup
##########################

resource "aws_vpc" "terraform-vpc" {
  cidr_block = "10.0.1.0/24"
  enable_dns_hostnames = true
  tags {
    Name = "stardog-aws-vpc"
  }
}

resource "aws_internet_gateway" "default" {
  vpc_id = "${aws_vpc.terraform-vpc.id}"
}

resource "aws_subnet" "terraform" {
  availability_zone = "${var.aws_az}"
  vpc_id = "${aws_vpc.terraform-vpc.id}"
  cidr_block = "10.0.1.0/24"

  map_public_ip_on_launch = true

  tags {
    Name = "stardog-terraform-subnet"
  }
}

resource "aws_route_table" "terraform-public" {
  vpc_id = "${aws_vpc.terraform-vpc.id}"
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.default.id}"
  }
  tags {
    Name = "Public subnet"
  }
}

resource "aws_route_table_association" "routing" {
    subnet_id = "${aws_subnet.terraform.id}"
    route_table_id = "${aws_route_table.terraform-public.id}"
}

resource "aws_security_group" "stardog-cluster" {
  name = "stardog-cluster"
  description = "Allow cluster traffic"

  vpc_id = "${aws_vpc.terraform-vpc.id}"

  # allow ssh from anywhere
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # stardog port
  ingress {
    from_port = 5820
    to_port = 5820
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # haproxy stats port
  ingress {
    from_port = 9000
    to_port = 9000
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # zookeeper ports
  ingress {
    from_port = 2181
    to_port = 2181
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 2888
    to_port = 2888
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 3888
    to_port = 3888
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # for serving the stardog binaries
  ingress {
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
