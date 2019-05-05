pipeline {
    agent {
        label "maven"
    }
    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }
    stages {
        stage("Initialize") {
            steps {
                library(identifier: "openshift-pipeline-library@master", 
                        retriever: modernSCM([$class: "GitSCMSource",
                                              credentialsId: "dev-repository-credentials",
                                              remote: "ssh://git@github.com/leandroberetta/openshift-cicd-demo.git"]))     

                script {
                    env.IMAGE_NAME = env.APP_NAME
    
                    env.DEV_PROJECT = "dev"
                    env.TEST_PROJECT = "test"
                    env.PROD_PROJECT = "prod"
                                    
                    env.APPLICATION_TEMPLATE = "./openshift/template.yaml"
                    env.APPLICATION_TEMPLATE_PARAMETERS_DEV = "./openshift/environments/dev/templateParameters.txt"
                    env.APPLICATION_TEMPLATE_PARAMETERS_TEST = "./openshift/environments/test/templateParameters.txt"
                    env.APPLICATION_TEMPLATE_PARAMETERS_PROD = "./openshift/environments/prod/templateParameters.txt"
                }
            }
        }
        stage("Checkout") {
            steps {      
                script {
                    env.GIT_COMMIT = checkout(scm).GIT_COMMIT
                }
            }
        }
        stage("Compile") {
            steps {
                // Quarkus requires Maven 3.5.3+ but the Jenkins agent's Maven version is 3.5.0, using the agent just for the oc command
                sh "./mvnw clean package -DskipTests"
            }
        }
        stage("Test") {
            steps {
                sh "./mvnw test"
            }
        }
        stage("Build Image") {
            steps {
                applyTemplate(project: env.DEV_PROJECT, 
                              application: env.APP_NAME, 
                              template: env.APPLICATION_TEMPLATE, 
                              parameters: env.APPLICATION_TEMPLATE_PARAMETERS_DEV,
                              createBuildObjects: true)

                // Quarkus specific tasks
                sh "mkdir deploy"
                sh "cp -R ./target/lib ./deploy"
                sh "cp ./target/${env.APP_NAME}-runner.jar ./deploy"
                sh "cp -R ./.s2i ./deploy"

                buildImage(project: env.DEV_PROJECT, 
                           application: env.APP_NAME, 
                           artifactsDir: "./deploy")
            }
        }
        stage("Deploy DEV") {
            steps {
                script {
                    env.TAG_NAME = readMavenPom().getVersion()
                }   
                
                tagImage(srcProject: env.DEV_PROJECT, 
                         srcImage: env.IMAGE_NAME, 
                         srcTag: "latest", 
                         dstProject: env.DEV_PROJECT, 
                         dstImage: env.IMAGE_NAME,
                         dstTag: env.TAG_NAME)
                
                deployImage(project: env.DEV_PROJECT, 
                            application: env.APP_NAME, 
                            image: env.IMAGE_NAME, 
                            tag: env.TAG_NAME)
            }
        }
        stage("Deploy TEST") {
            steps {
                input("Promote to TEST?")

                applyTemplate(project: env.TEST_PROJECT, 
                              application: env.APP_NAME, 
                              template: env.APPLICATION_TEMPLATE, 
                              parameters: env.APPLICATION_TEMPLATE_PARAMETERS_TEST)

                tagImage(srcProject: env.DEV_PROJECT, 
                         srcImage: env.IMAGE_NAME, 
                         srcTag: env.TAG_NAME, 
                         dstProject: env.TEST_PROJECT, 
                         dstImage: env.IMAGE_NAME,
                         dstTag: env.TAG_NAME)
                
                deployImage(project: env.TEST_PROJECT, 
                            application: env.APP_NAME, 
                            image: env.IMAGE_NAME, 
                            tag: env.TAG_NAME)
            }
        }
        stage("Integration Test") {
            agent {
                kubernetes {
                    cloud "openshift"
                    defaultContainer "jnlp"
                    label "${env.APP_NAME}-int-test"
                    yaml """
                        apiVersion: v1
                        kind: Pod
                        spec:
                          containers:
                          - name: python
                            image: python:3
                            command:
                            - cat
                            tty: true
                    """                
                }
            }
            steps {
                checkout(scm)

                container("python") {
                    sh "pip install requests"
                    sh "python ./src/test/python/it.py"
                }
            }
        }
        stage("Deploy PROD (Blue)") {
            steps {
                script {
                    if (!blueGreen.existsBlueGreenRoute(project: env.PROD_PROJECT, application: env.APP_NAME)) {
                        applyTemplate(project: env.PROD_PROJECT, 
                                      application: blueGreen.getApplication1Name(env.APP_NAME), 
                                      template: env.APPLICATION_TEMPLATE, 
                                      parameters: env.APPLICATION_TEMPLATE_PARAMETERS_PROD)
                                      
                        applyTemplate(project: env.PROD_PROJECT, 
                                      application: blueGreen.getApplication2Name(env.APP_NAME), 
                                      template: env.APPLICATION_TEMPLATE, 
                                      parameters: env.APPLICATION_TEMPLATE_PARAMETERS_PROD) 

                        blueGreen.createBlueGreenRoute(project: env.PROD_PROJECT, application: env.APP_NAME)
                    } else {
                        applyTemplate(project: env.PROD_PROJECT, 
                                      application: blueGreen.getBlueApplication(project: env.PROD_PROJECT, application: env.APP_NAME), 
                                      template: env.APPLICATION_TEMPLATE, 
                                      parameters: env.APPLICATION_TEMPLATE_PARAMETERS_PROD)
                    }
                    
                    tagImage(srcProject: env.TEST_PROJECT, 
                             srcImage: env.IMAGE_NAME, 
                             srcTag: env.TAG_NAME, 
                             dstProject: env.PROD_PROJECT, 
                             dstImage: env.IMAGE_NAME,
                             dstTag: env.TAG_NAME)

                    deployImage(project: env.PROD_PROJECT, 
                                application: blueGreen.getBlueApplication(project: env.PROD_PROJECT, application: env.APP_NAME), 
                                image: env.IMAGE_NAME, 
                                tag: env.TAG_NAME)
                } 
            }
        }
        stage("Deploy PROD (Green)") {
            steps {
                input("Switch to new version?")

                script{
                    blueGreen.switchToGreenApplication(project: env.PROD_PROJECT, application: env.APP_NAME)   
                }              
            }
        }
    }
}