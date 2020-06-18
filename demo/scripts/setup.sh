#! /usr/bin/env bash

oc new-project hello-dev
oc new-project hello-test
oc new-project hello-prod

oc new-app --template=jenkins-ephemeral --name=jenkins -n hello-dev

oc adm policy add-role-to-user edit system:serviceaccount:hello-dev:jenkins -n hello-test
oc adm policy add-role-to-user edit system:serviceaccount:hello-dev:jenkins -n hello-prod

oc new-build https://github.com/leandroberetta/openshift-cicd-demo --name=hello-pipeline --strategy=pipeline -e APP_NAME=hello -n dev
