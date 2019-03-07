{{- define "zkservers" -}}
{{- $zk := dict "servers" (list) -}}
{{ $namespace := .Release.Namespace }}
{{- range int .Values.zookeeper.count | until -}}
{{- $noop := printf "zk-%d.zk-service.%s:2181" . $namespace | append $zk.servers | set $zk "servers" -}}
{{- end -}}
{{- join "," $zk.servers -}}
{{- end -}}

{{- define "dockercreds" -}}
{{ printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\"} } }" .Values.dockerrepo.url .Values.dockerrepo.username .Values.dockerrepo.password | b64enc }}
{{- end -}}
