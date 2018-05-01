#! /usr/bin/env bash

#
# Author: Leandro Beretta <lberetta@redhat.com>
#
# Script to create the environments and pipelines configuration.
#

OCP_CLUSTER_URL=$1
GIT_DEMO_URL=$2
GIT_DEMO_BRANCH=$3

oc login $OCP_CLUSTER_URL -u administrator -p administrator

#
# Jenkins
#

oc new-project jenkins --display-name=JENKINS

oc new-app --template=jenkins-persistent -p MEMORY_LIMIT=1Gi -l app=jenkins

#
# Environments creation
#

oc new-project dev --display-name=DEV
oc new-project test --display-name=TEST
oc new-project prod --display-name=PROD

# Grants edit access to developer in dev project
oc adm policy add-role-to-user edit developer -n dev

# Grants view access to developer in test project
oc adm policy add-role-to-user edit developer -n test

# Grants view access to developer in prod project
oc adm policy add-role-to-user view developer -n prod

# Grants view access to developer in jenkins project
oc adm policy add-role-to-user edit developer -n jenkins

# Grants edit access to jenkins service account
oc policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n dev
oc policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n test
oc policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n prod

# Allows prod service account the ability to pull images from dev
oc policy add-role-to-group system:image-puller system:serviceaccounts:test -n dev
oc policy add-role-to-group system:image-puller system:serviceaccounts:prod -n dev

#
# Application deployment
#

#
# Test application
#

oc project dev

# Creates a binary build (the build is not started immediately)
oc new-build --binary=true --name="app" jboss-eap70-openshift:1.5

# Creates the application
oc new-app dev/app:latest --name="app" --allow-missing-imagestream-tags=true

# Removes the triggers
oc set triggers dc/app --remove-all

oc expose dc/app --port 8080
oc expose svc/app

#
# Pipelines deployment
#

oc project jenkins

echo "apiVersion: v1
kind: BuildConfig
metadata:
  name: blue-green-pipeline
spec:
  source:
    type: Git
    git:
      uri: $GIT_DEMO_URL
      ref: $GIT_DEMO_BRANCH
  strategy:
    type: JenkinsPipeline
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.bg" | oc create -f -

echo "apiVersion: v1
kind: BuildConfig
metadata:
  name: ab-pipeline
spec:
  source:
    type: Git
    git:
      uri: $GIT_DEMO_URL
      ref: $GIT_DEMO_BRANCH
  strategy:
    type: JenkinsPipeline
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.ab" | oc create -f -

echo "apiVersion: v1
kind: BuildConfig
metadata:
  name: ci-pipeline
spec:
  source:
    type: Git
    git:
      uri: $GIT_DEMO_URL
      ref: $GIT_DEMO_BRANCH
  strategy:
    type: JenkinsPipeline
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.ci" | oc create -f -

echo "apiVersion: v1
kind: BuildConfig
metadata:
  name: cd-pipeline
spec:
  source:
    type: Git
    git:
      uri: $GIT_DEMO_URL
      ref: $GIT_DEMO_BRANCH
  strategy:
    type: JenkinsPipeline
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.cd" | oc create -f -

#
# Test application
#

oc project test

oc new-app dev/app:latest --name="app" --allow-missing-imagestream-tags=true

# Removes the triggers
oc set triggers dc/app --remove-all

oc expose dc/app --port 8080
oc expose svc/app

#
# Production applications
#

# Blue/Green

oc project prod

# Creates the blue and green applications (observe that in prod is not a BuildConfig object created)
oc new-app dev/app:latest --name="app-green" --allow-missing-imagestream-tags=true
oc new-app dev/app:latest --name="app-blue" --allow-missing-imagestream-tags=true

# Removes the triggers
oc set triggers dc/app-green --remove-all
oc set triggers dc/app-blue --remove-all

oc expose dc/app-blue --port 8080
oc expose dc/app-green --port 8080

oc expose svc/app-green --name blue-green
