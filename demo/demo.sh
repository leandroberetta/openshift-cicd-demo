#! /usr/bin/env bash

#
# Author: Leandro Beretta <lberetta@redhat.com>
#
# Script to create the demo environments and pipelines configuration.
#

#
# Users creation
#

oc login -u admin -p admin
oc login -u developer -p developer

oc login -u system:admin

# Gives admin role to admin user
oc adm policy add-role-to-user admin admin

#
# Jenkins
#

oc login -u admin -p admin

oc new-project jenkins --display-name=JENKINS

oc new-app --template=jenkins-persistent -p MEMORY_LIMIT=1Gi -l app=jenkins

#
# Environments creation
#

oc new-project dev --display-name=DEV
oc new-project test --display-name=TEST
oc new-project prod --display-name=PROD

# Grant edit access to developer in dev project
oc adm policy add-role-to-user edit developer -n dev

# Grant edit access to developer in test project
oc adm policy add-role-to-user edit developer -n test

# Grant view access to developer in prod project
oc adm policy add-role-to-user view developer -n prod

# Grant view access to developer in prod project
oc adm policy add-role-to-user edit developer -n jenkins

# Grant edit access to jenkins service account
oc policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n dev
oc policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n test
oc policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n prod

# Allow prod service account the ability to pull images from test
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
oc new-app dev/app:DevCandidate-1.0.0 --name="app" --allow-missing-imagestream-tags=true

# Removes the triggers
oc set triggers dc/app --remove-all

oc expose dc/app --port 8080
oc expose svc/app

#
# Pipeline deployment
#

oc project jenkins

echo 'apiVersion: v1
kind: BuildConfig
metadata:
  labels:
    name: "blue-green-pipeline"
  name: "blue-green-pipeline"
spec:
  source:
    type: "Git"
    git:
      uri: "https://github.com/leandroberetta/openshift-cicd-demo"
  strategy:
    type: "JenkinsPipeline"
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.bg' | oc create -f -

echo 'apiVersion: v1
kind: BuildConfig
metadata:
  labels:
    name: "ab-pipeline"
  name: "ab-pipeline"
spec:
  source:
    type: "Git"
    git:
      uri: "https://github.com/leandroberetta/openshift-cicd-demo"
  strategy:
    type: "JenkinsPipeline"
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.ab' | oc create -f -

echo 'apiVersion: v1
kind: BuildConfig
metadata:
  labels:
    name: "ci-pipeline"
  name: "ci-pipeline"
spec:
  source:
    type: "Git"
    git:
      ref: "develop"
      uri: "https://github.com/leandroberetta/openshift-cicd-demo"
  strategy:
    type: "JenkinsPipeline"
    jenkinsPipelineStrategy:
      jenkinsfilePath: Jenkinsfile.ci' | oc create -f -

      echo 'apiVersion: v1
      kind: BuildConfig
      metadata:
        labels:
          name: "ci-pipeline"
        name: "ci-pipeline"
      spec:
        source:
          type: "Git"
          git:
            ref: "develop"
            uri: "https://github.com/leandroberetta/openshift-cicd-demo"
        strategy:
          type: "JenkinsPipeline"
          jenkinsPipelineStrategy:
            jenkinsfilePath: Jenkinsfile.cd' | oc create -f -

#
# Test application
#

oc project test

oc new-app dev/app:TestCandidate-1.0.0 --name="app" --allow-missing-imagestream-tags=true

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
oc new-app dev/app:ProdReady-1.0.0 --name="app-green" --allow-missing-imagestream-tags=true
oc new-app dev/app:ProdReady-1.0.0 --name="app-blue" --allow-missing-imagestream-tags=true

# Removes the triggers
oc set triggers dc/app-green --remove-all
oc set triggers dc/app-blue --remove-all

oc expose dc/app-blue --port 8080
oc expose dc/app-green --port 8080

oc expose svc/app-green --name blue-green
