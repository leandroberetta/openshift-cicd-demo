#!/usr/bin/env bash

oc apply -R -f ./environments
oc apply -R -f ./tasks
oc apply -R -f ./pipelines
oc apply -R -f ./triggers

export HELLO_EVENT_LISTENER_ROUTE=$(oc get route el-hello-event-listener -o jsonpath='{.spec.host}' -n hello-dev)