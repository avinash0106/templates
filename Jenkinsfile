pipeline {
  // agent {
  //   docker {
  //     image "hashicorp/terraform:latest"
  //   }
  // }
  agent any
  stages {
    stage('Terraform init') {
      steps {
        sh 'terraform -version'
      }
    }

    stage('Terraform validate') {
      steps {
        echo 'terraform validate'
      }
    }

    stage('Terraform plan') {
      steps {
        echo 'terraform plan'
      }
    }

    stage('Terraform apply') {
      steps {
        echo 'terraform apply'
      }
    }

    stage('Terraform destroy') {
      steps {
        echo 'terraform destroy'
      }
    }
  }
}
