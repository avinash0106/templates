pipeline {
    agent any
    options {
        ansiColor('xterm')
    }
    tools {
        terraform 'terraform'

    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        sh 'cp -r /app/osione-infra/* .'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Error Copying files ${e.getMessage()}")
                    }
                }
            }
        }

        stage('Terraform Initialise') {
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

        stage('Terraform Plan/Apply') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS']]) {
                    script {
                        try {
                        // Set AWS CLI credentials using Jenkins credentials
                        sh 'aws configure set aws_access_key_id \$AWS_ACCESS_KEY_ID'
                        sh 'aws configure set aws_secret_access_key \$AWS_SECRET_ACCESS_KEY'
                        sh 'aws configure set region ap-south-1'

                        // Print AWS CLI configuration for verification (optional)
                        sh 'aws configure list'

                        sh 'terraform plan -out=tfplan'
                        sh 'terraform apply -auto-approve'
                        } finally {
                            // Clear AWS CLI credentials after use (optional)
                            archiveArtifacts allowEmptyArchive: true, artifacts: '**/tfplan', followSymlinks: false
                            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.tfstate', followSymlinks: false
                            echo "Infrastructure is Provisioned!"
                        }
                    }
                }
            }
        }
    }
}
