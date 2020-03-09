#!/bin/bash
# For the time being use exec in order to cope with hashicorp's go-plugin failure to kill child processes.
# See https://github.com/hashicorp/go-plugin/issues/136
exec /usr/local/bin/java -jar ${project.build.finalName}.jar
