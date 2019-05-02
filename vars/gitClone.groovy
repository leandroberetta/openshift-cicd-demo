#!/usr/bin/env groovy

def call(parameters) {
    def gitRemoteConfig = [:]

    gitRemoteConfig['url'] = parameters.repository

    if (parameters.credentialsId)
        gitRemoteConfig['credentialsId'] = parameters.credentialsId

    env.GIT_COMMIT = checkout([$class: 'GitSCM',
                               branches: [[name: parameters.branch]], 
                               userRemoteConfigs: [gitRemoteConfig]]).GIT_COMMIT

}