#!/usr/bin/env bash

oc apply -R -f ./environments
oc apply -R -f ./tasks
oc apply -R -f ./pipelines
oc apply -R -f ./triggers

export HELLO_EVENT_LISTENER_ROUTE=$(oc get route el-hello-event-listener -o jsonpath='{.spec.host}' -n hello-dev)

curl -v http://$HELLO_EVENT_LISTENER_ROUTE \
    -H 'X-GitHub-Event: push' \
    -H 'X-Hub-Signature: sha1=9f257f3c340bf1c2d116f5cd99aeacf62811b657' \
    -H 'Content-Type: application/json' \
    -d '{"ref": "refs/heads/develop","head_commit": {"id": "master"},"repository": {"url": "https://github.com/leandroberetta/openshift-cicd-demo"}}'

curl -v http://$HELLO_EVENT_LISTENER_ROUTE \
    -H 'X-GitHub-Event: push' \
    -H 'X-Hub-Signature: sha1=eaf4126c4193f189bde68af2700b12d629c22674' \
    -H 'Content-Type: application/json' \
    -d '{"ref": "refs/heads/master","head_commit": {"id": "master"},"repository": {"url": "https://github.com/leandroberetta/openshift-cicd-demo"}}'