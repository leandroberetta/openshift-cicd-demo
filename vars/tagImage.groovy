#!/usr/bin/env groovy

def call(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.dstProject) {
            openshift.tag("${parameters.srcProject}/${parameters.srcImage}:${parameters.srcTag}", "${parameters.dstProject}/${parameters.dstImage}:${parameters.dstTag}");
        }
    }
}