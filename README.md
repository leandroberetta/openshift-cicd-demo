# openshift-cicd-demo

![demo](demo/img/demo.png)


This repository contains a demo of the following OpenShift capabilities:

* **Blue-Green** deployments with zero downtime
* **A/B** deployments for experimental applications on production
* Integration with **Jenkins Pipelines**
* Continuous integration in Test environment (**CI**)
* Continuous deployment (Blue-Green and A/B) in Production environment (**CD**)

## Requisites

* An **OpenShift Container Platform 3.5** cluster
* The OpenShift CLI client (**oc**) for executing remotes commands to the cluster
* Python 3.5 or higher (for testing the service) with requests library installed

## Usage

This demo can be be used in every cluster of OpenShift. The easiest way is to use the all-in-one local cluster:

    oc cluster up

Then this repository contains a **demo.sh** script to generate all the needed objects.

    sh demo/demo.sh

After the script execution the demo is ready to be used.

## Description

### Environments

The environments created are:

* Jenkins
* Test
* Prod

## Steps

### Blue-Green Pipeline

![bluegreen](demo/img/bluegreen.png)

### A/B Pipeline

![ab](demo/img/ab.png)
