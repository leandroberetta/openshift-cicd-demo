#!/usr/bin/env groovy

def call() {
    env.IMAGE_NAME = env.APP_NAME
    
    env.DEV_PROJECT = "dev"
    env.TEST_PROJECT = "test"
    env.PROD_PROJECT = "prod"
                    
    env.APP_TEMPLATE = "./openshift/template.yaml"
    env.APP_TEMPLATE_PARAMETERS_DEV = "./openshift/environments/dev/templateParameters.txt"
    env.APP_TEMPLATE_PARAMETERS_TEST = "./openshift/environments/test/templateParameters.txt"
    env.APP_TEMPLATE_PARAMETERS_PROD = "./openshift/environments/prod/templateParameters.txt"
}