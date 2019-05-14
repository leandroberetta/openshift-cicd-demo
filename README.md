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

    oc new-project dev
    oc new-project test
    oc new-project prod
    
### Create a Jenkins Instance

A Jenkins instance is created in the **dev** project:

    oc new-app --template=jenkins-ephemeral --name=jenkins -n dev

Then a set of permissions need to be granted:

    oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n test
    oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n prod

### Create the Pull Secret (Optional)

If the repository used is private a pull Secret is needed.

The Secret needs to be label with **credential.sync.jenkins.openshift.io=true** to be synchronized in Jenkins thanks to the [OpenShift Jenkins Sync Plugin](https://github.com/openshift/jenkins-sync-plugin). 

An annotation is used to automatically assign the Secret to any BuildConfig that matches the Git URI used.

The commands to create and label the Secret are:

    oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n dev
    oc label secret repository-credentials credential.sync.jenkins.openshift.io=true -n dev
    oc annotate secret repository-credentials 'build.openshift.io/source-secret-match-uri-1=ssh://github.com/*' -n dev

### Create the Pipeline

A pipeline is a special type of BuildConfig so to create it the new-build command is used:

    oc new-build ssh://git@github.com/leandroberetta/openshift-cicd-demo.git --name=hello-service-pipeline --strategy=pipeline -e APP_NAME=hello-service -n dev

After the execution of this command the pipeline is started.
