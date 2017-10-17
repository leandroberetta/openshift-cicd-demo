# openshift-cicd-demo

![demo](demo/img/demo.png)

This repository contains a demo of the following OpenShift capabilities:

* **Blue-Green** deployments with zero downtime
* **A/B** deployments for experimental applications on production
* Integration with **Jenkins Pipelines**
* Continuous integration in Dev environment (**CI**)
* Continuous deployment (Blue-Green and A/B) in Test and Production environment (**CD**)

## Requisites

* An **OpenShift Container Platform 3.6** cluster
* The OpenShift CLI client (**oc**) for executing remotes commands to the cluster
* Python 3.5 or higher (for testing the service) with requests library installed

## Usage

This demo can be be used in every cluster of OpenShift. The easiest way is to use **minishift**:

    minishift start

Then this repository contains a **demo.sh** script to generate all the needed objects.

    sh demo/demo.sh

After the script execution the demo is ready to be used.

## Description

### Environments

The environments created are:

* Jenkins
* Dev
* Test
* Prod

## Steps

### CI/CD Pipeline (Blue-Green)

![bluegreen](demo/img/bluegreen.png)

### A/B Pipeline

![ab](demo/img/ab.png)

### CI Pipeline (just for Dev deployments)

![ci](demo/img/ab.png)

### CD Pipeline (deploys an specific image without building)

![cd](demo/img/ab.png)
