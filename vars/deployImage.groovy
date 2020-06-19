#!/usr/bin/env groovy

def call(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            rolloutApplication(parameters.application, parameters.image, parameters.tag)
        }    
    }
}

def rolloutApplication(application, image, tag) {
    def dc = openshift.selector("dc/${application}").object()

    openshift.set("triggers", "dc/${application}", "--from-image=${image}:${tag}", "-c ${dc.spec.template.spec.containers[0].name}")    
    
    openshift.selector("dc", application).rollout().status()
}