#!/usr/bin/env groovy

def call() {
    env.GIT_COMMIT = checkout(scm).GIT_COMMIT
}