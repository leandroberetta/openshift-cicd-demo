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
                library(identifier: "openshift-pipeline-library@v1.0", 
                        retriever: modernSCM([$class: "GitSCMSource",
                                              credentialsId: env.GIT_CREDENTIALS,
                                              traits: [[$class: "jenkins.plugins.git.traits.TagDiscoveryTrait"]],
                                              remote: "https://github.com/leandroberetta/openshift-pipeline-library.git"]))     

                script {
                    env.IMAGE_NAME = env.APP_NAME
    
                    env.DEV_PROJECT = "dev"
                    env.TEST_PROJECT = "test"
                    env.PROD_PROJECT = "prod"
                                    
                    env.APPLICATION_TEMPLATE = "src/main/openshift/template.yaml"
                    env.APPLICATION_TEMPLATE_PARAMETERS_DEV = "src/main/openshift/environments/dev/templateParameters.txt"
                    env.APPLICATION_TEMPLATE_PARAMETERS_TEST = "src/main/openshift/environments/test/templateParameters.txt"
                    env.APPLICATION_TEMPLATE_PARAMETERS_PROD = "src/main/openshift/environments/prod/templateParameters.txt"
                    env.APPLICATION_INT_TEST_AGENT = "src/main/openshift/environments/test/integration-test/integration-test-agent.yaml"
                    env.APPLICATION_INT_TEST_SCRIPT = "src/main/openshift/environments/test/integration-test/integration-test.py"
                }
            }
        }
        stage("Checkout") {
            steps {                
                gitClone(repository: env.GIT_REPO, 
                         branch: env.GIT_BRANCH, 
                         credentialsId: env.GIT_CREDENTIALS)

                stash "repo"
            }
        }
        stage("Compile") {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }
        stage("Test") {
            steps {
                sh "mvn test"
            }
        }
        stage("Build Image") {
            steps {
                applyTemplate(project: env.DEV_PROJECT, 
                              application: env.APP_NAME, 
                              template: env.APPLICATION_TEMPLATE, 
                              parameters: env.APPLICATION_TEMPLATE_PARAMETERS_DEV,
                              createBuildObjects: true)

                buildImage(project: env.DEV_PROJECT, 
                           application: env.APP_NAME, 
                           artifactsDir: "./target")
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
                    yaml readFile(env.APPLICATION_INT_TEST_AGENT)
                }
            }
            steps {
                unstash "repo"

                container("python") {
                    sh "pip install requests"
                    sh "python ${env.APPLICATION_INT_TEST_SCRIPT}"
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