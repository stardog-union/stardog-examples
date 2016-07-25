# Flag to enable the cluster, without this flag set, the rest of the properties have no effect
pack.enabled=true

# this node IP address
pack.node.address=${host}

# the connection string for ZooKeeper where cluster state is stored
pack.zookeeper.address=${zk_conn_string}
