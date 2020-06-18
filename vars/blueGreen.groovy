#!/usr/bin/env groovy

def getApplication1Name(application) {
    return "${application}-1"
}

def getApplication2Name(application) {
    return "${application}-2"
}

def existsBlueGreenRoute(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            if (openshift.selector("route/${parameters.application}-blue-green").exists()) {
                return true                      
            }
        }
    }
}

def createBlueGreenRoute(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            openshift.selector("svc", "${parameters.application}-2").expose("--name=${parameters.application}-blue-green") 
        }
    }
}

def getBlueApplication(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            def route = openshift.selector("route/${parameters.application}-blue-green").object()
            def blueApplication = "${parameters.application}-1"  
                            
            if (route.spec.to.name.compareTo("${parameters.application}-1") == 0) {
                blueApplication = "${parameters.application}-2"
            }

            return blueApplication
        }
    }
}

def switchToGreenApplication(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            def route = openshift.selector("route/${parameters.application}-blue-green").object()
            
            route.spec.to.name = getBlueApplication(project: parameters.project, application: parameters.application)
            
            openshift.apply(route)        
        }
    }
}