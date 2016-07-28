## Stardog Cluster on EC2 via Terraform.io

This example shows configuring and launching a Stardog cluster on [Amazon Web Services (AWS)](https://aws.amazon.com/) in a [Virtual Private Cloud (VPC)](https://aws.amazon.com/documentation/vpc/) using [Terraform](http://terraform.io). This examples is provided "AS IS" without warranty of any kind and the user is responsible for any AWS charges incurred by this example. 

Please read the cluster description carefully and adjust the settings to reflect the design you want for your cluster.

### Cluster overview

These recipes describe three basic components of a Stardog Cluster on AWS: the ZooKeeper Ensemble, an HAProxy instance, and the Stardog Cluster itself. By default it creates 1 HAProxy node, 3 ZooKeeper nodes, and 3 Stardog nodes. The cluster uses a Virtual Private Cloud (VPC) such that only the HAProxy node will be accessible publicly.

The configuration includes a new subnet, a security group and networking rules for that subnet, a routing table, and a gateway. The instance types for HAProxy, ZooKeeper and Stardog nodes can be configured separately. All the nodes are deployed in the same availability zone (us-east-1a by default).

### Organization of scripts

The [`main.tf`](main.tf) file describes the basic network infrastructure on AWS that will be created each time a cluster is created. It includes a new subnet, a security group and networking rules for that subnet, a routing table, and a gateway. This way the whole process of creating a cluster is fully automated and can be destroyed without going to the AWS console.

The files [`stardog.tf`](stardog.tf), [`zookeeper.tf`](zookeeper.tf), and [`haproxy.tf`](haproxy.tf) each describe the EC2 instances that are going to be created. Each of them have a set of corresponding scripts under the folder with their component name (i.e. `stardog/`, `haproxy/`, `zookeeper/`) that are going to be executed while provisioning the instances. The deployment process is currently set up so that it will create the ZooKeeper instances first, then the HAProxy instance where the Stardog binaries will be uploaded, lastly the Stardog instances will be created.

The purpose of provisioning the Stardog binaries to the HAProxy instance is so that we only have to upload the binaries once and then each Stardog instance will grab the binaries from there via HTTP (see [`haproxy/setup.sh`](haproxy/setup.sh), and [`stardog/setup.sh`](stardog/setup.sh)).

## Setting up the AWS cluster

### Prerequisites

- Make sure [AWS Command Line Interface](https://aws.amazon.com/cli/) is installed.

- Make sure [Terraform](http://terraform.io) is installed.

- Find or create an AWS AMI with Java 8 installed (Ubuntu with Oracle JDK is recommended).

### Configuration

- Copy file `variables.tf.template` to `variables.tf` in this directory; then review and modify the contents of `variables.tf` accordingly to reflect your connection options and ssh key. The required parameters in this file are commented out and include `key_path`, `stardog_dist`, `stardog_license`, `stardog_ami` and `zookeeper_ami`. The file includes descriptions about these parameters. Use the AMI setup in prerequisites section for `stardog_ami` and `zookeeper_ami`.

- Change other parameters in `variables.tf` if needed. For example, you can change the availability zone or the number of nodes in the cluster.

- To make sure the variables have been correctly set, run `terraform plan` which will list a set of resources to be created on EC2:

```
$ terraform plan
```

### Launching the cluster

- Apply changes to EC2:

```
$ terraform apply
```

- Once the infrastructure has been created correctly, the public DNS of the HAProxy node will be shown. This address will be the public access point for the cluster. Record this value in an environment variable for easier access:

```
$ export STARDOG_CLUSTER=$(terraform output haproxy)
```

- Run `cluster info` command to see the nodes in the cluster:

```
$ stardog-admin --server http://${STARDOG_CLUSTER}:5820 cluster info
Coordinator:
   10.0.1.100:5820
Nodes:
   10.0.1.101:5820
   10.0.1.102:5820
```

- Go to the address `http://$STARDOG_CLUSTER:9000/` in your browser to check HAProxy status. The username and password for HAProxy are both set to `admin` in the [`haproxy/haproxy.cfg.tpl`](haproxy/haproxy.cfg.tpl) file.

### Changes to the cluster

- If something goes wrong or you need to make changes to your cluster settings edit the terraform scripts and then apply your changes again (Terraform will incrementally update your EC2 instances to reflect the changes):

```
$ terraform apply
```

- Once you are done with the cluster and would like to delete the created EC2 instances you can run:

```
$ terraform destory
```

Note that, with this command all the instances will be deleted and any data stored on the ephemeral storage of these instances will be lost.





