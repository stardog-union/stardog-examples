global
    daemon
    maxconn 256

defaults
	option http-no-delay
    timeout connect 5s
    timeout client 1h
    timeout server 1h

frontend stardog-in
	mode http
    option tcpka # keep-alive
    bind *:5820
    default_backend stardogs

backend stardogs
    mode http

    balance roundrobin

    # the following line performs a health check
    option httpchk GET /admin/healthcheck
    # replace these IP addresses with the corresponding node address
    ${redirects}

listen stats
    bind *:9000
    mode http
    stats enable
    stats hide-version
    stats realm HAproxy\ Statistics
    stats uri /
    stats auth admin:admin
    stats refresh 5s
