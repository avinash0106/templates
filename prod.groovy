
pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    tools {
        terraform 'terraform'
    }
    environment {
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '702024837763'
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        sh 'cp -r /apps/terraform/* .'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Error Copying files ${e.getMessage()}")
                    }
                }
            }
        }

        stage('Terraform Initialise and Plan') {
            steps {
                script {
                    try {
                        sh 'terraform init'
                        sh 'terraform validate'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Terraform planning failed: ${e.getMessage()}")
                    }
                }
            }
        }

        stage('Terraform Apply') {
            steps {
                script {
                    try {
                        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS']]) {
                            // Set AWS CLI credentials using Jenkins credentials
                            sh 'aws configure set aws_access_key_id \$AWS_ACCESS_KEY_ID'
                            sh 'aws configure set aws_secret_access_key \$AWS_SECRET_ACCESS_KEY'
                            sh 'aws configure set region ${AWS_REGION}'
                            
                            // Print AWS CLI configuration for verification (optional)
                            sh 'aws configure list'
                            sh 'terraform plan -out=tfplan'
                            // sh 'terraform apply "tfplan" -auto-approve'
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Terraform apply failed: ${e.getMessage()}")
                    }
                }
            }
        }

        stage('ECR Config') {
            steps {
                script {
                    try {
                        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS']]) {
                            sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("ECR login failed: ${e.getMessage()}")
                    }
                }
            }
        }

        stage('Docker Build/Publish') {
            parallel {
                stage('ems-api') {
                    steps {
                        script {
                            try {
                                sh 'cp -r /apps/ems-api/* .'
                                def IMAGE_NAME = 'osiems'
                                def ECR_REPOSITORY = 'osiems'
                                // Build the Docker image
                                sh "docker build -t ${IMAGE_NAME} ."
                                // Tag the image with ECR repository URI
                                sh "docker tag ${IMAGE_NAME}:latest ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}"
                                // Push the image to ECR
                                sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}"
                            } catch (Exception e) {
                                currentBuild.result = 'FAILURE'
                                error("Docker image build/push failed: ${e.getMessage()}")
                            }
                        }
                    }
                }
                stage('tsm-api') {
                    steps {
                        script {
                            try {
                                sh 'cp -r /apps/tsm-api/* .'
                                def IMAGE_NAME = 'ositsm'
                                def ECR_REPOSITORY = 'ositsm'
                                // Build the Docker image
                                sh "docker build -t ${IMAGE_NAME} ."
                                // Tag the image with ECR repository URI
                                sh "docker tag ${IMAGE_NAME}:latest ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}"
                                // Push the image to ECR
                                sh "docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}"
                            } catch (Exception e) {
                                currentBuild.result = 'FAILURE'
                                error("Docker image build/push failed: ${e.getMessage()}")
                            }
                        }
                    }
                }
            }
        }
    }
}
