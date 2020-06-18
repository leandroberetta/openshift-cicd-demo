# OpenShift CI/CD Demo

Basic demonstration of an OpenShift CI/CD pipeline for deploying applications across environments using advanced deployment strategies like Blue/Green.

The pipeline is designed with the declarative approach and the [OpenShift Jenkins Pipeline Plugin](https://github.com/openshift/jenkins-client-plugin).

## Pipeline

![Pipeline](demo/images/pipeline.png)

## Pipeline Library

The pipeline uses a shared library for common functionality, the library is embedded in this repository but can be externalized in other Git repository as well.

## Demo

### Create the Projects

These are the projects (environments) used to deploy the application:

    oc new-project hello-dev
    oc new-project hello-test
    oc new-project hello-prod
    
### Create a Jenkins Instance

A Jenkins instance is created in the **dev** project:

    oc new-app --template=jenkins-ephemeral --name=jenkins -n hello-dev

Then a set of permissions need to be granted:

    oc adm policy add-role-to-user edit system:serviceaccount:hello-dev:jenkins -n hello-test
    oc adm policy add-role-to-user edit system:serviceaccount:hello-dev:jenkins -n hello-prod

### Create the Pipeline

A pipeline is a special type of BuildConfig so to create it the new-build command is used:

    oc new-build https://github.com/leandroberetta/openshift-cicd-demo --name=hello-pipeline --strategy=pipeline -e APP_NAME=hello -n hello-dev

After the execution of this command the pipeline is started.
