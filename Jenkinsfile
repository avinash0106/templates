pipeline {
  agent {
    docker {
      image "hashicorp/terraform:latest"
    }
  }

  stages {
    stage('Terraform init') {
      steps {
        echo 'terraform init'
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
