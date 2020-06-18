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

def getBlueGreenRoute(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            return openshift.selector("route/${parameters.application}-blue-green").object()
        }
    }
}

def getBlueApplication(parameters) {
    def route = getBlueGreenRoute(project: parameters.project, application: parameters.application)

    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {            
            def blueApplication = "${parameters.application}-1"  
                            
            if (route.spec.to.name.compareTo("${parameters.application}-1") == 0) {
                blueApplication = "${parameters.application}-2"
            }

            return blueApplication
        }
    }
}

def switchToGreenApplication(parameters) {
    def route = getBlueGreenRoute(project: parameters.project, application: parameters.application)

    route.spec.to.name = getBlueApplication(project: parameters.project, application: parameters.application)
    
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            openshift.apply(route)        
        }
    }
}