#!/usr/bin/env groovy

def call() {
    env.IMAGE_NAME = env.APP_NAME
    
    env.DEV_PROJECT = "${env.APP_NAME}-dev"
    env.TEST_PROJECT = "${env.APP_NAME}-test"
    env.PROD_PROJECT = "${env.APP_NAME}-prod"
                    
    env.APP_TEMPLATE = "./openshift/template.yaml"
    env.APP_TEMPLATE_PARAMETERS_DEV = "./openshift/environments/dev/templateParameters.txt"
    env.APP_TEMPLATE_PARAMETERS_TEST = "./openshift/environments/test/templateParameters.txt"
    env.APP_TEMPLATE_PARAMETERS_PROD = "./openshift/environments/prod/templateParameters.txt"
}