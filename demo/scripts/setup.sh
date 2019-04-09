#! /usr/bin/env bash

oc new-project dev
oc new-project test
oc new-project prod

oc new-app --template=jenkins-ephemeral --name=jenkins -n dev

oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n test
oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n prod

oc new-app -f src/main/openshift/template.yaml -n dev -p APP_NAME=openshift-hello-world -p GIT_REPO=https://github.com/leandroberetta/openshift-cicd-demo.git -p GIT_BRANCH=master