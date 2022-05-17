pipeline {
    agent any
    stages {
        stage('Fetch') {
            steps {
                sh 'git clone https://github.com/linuxacademy/content-pipelines-cje-labs.git'
            }
        }
        stage('Build'){
            steps{
                sh 'gcc --std=c99 -o mario content-pipelines-cje-labs/lab1_lab2/mario.c'
            }
        }
    }

    post{
        failure {
            timeout(time: 5, unit: 'MINUTES') {
                retry(3) {
                    ${env.JOB_NAME}.run
                }
            }
        }
    }
}
