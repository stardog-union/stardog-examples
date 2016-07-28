output "haproxy" {
  value = "${aws_instance.haproxy.public_dns}"
}
