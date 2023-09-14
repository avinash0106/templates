pipeline {
  agent {
    docker {
      image "hashicorp/terraform:latest"
    }
  }

  stages {
    stage('Terraform init') {
      steps {
        sh 'terraform init'
      }
    }

    stage('Terraform validate') {
      steps {
        sh 'terraform validate'
      }
    }

    stage('Terraform plan') {
      steps {
        sh 'terraform plan'
      }
    }

    stage('Terraform apply') {
      steps {
        sh 'terraform apply'
      }
    }

    stage('Terraform destroy') {
      steps {
        sh 'terraform destroy'
      }
    }
  }
}
