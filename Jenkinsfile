pipeline {
  agent any
  tools {
    // Define a Terraform tool installation named 'terraform'
    terraform 'terraform'
  }
  stages {
    stage('Terraform Init') {
      steps {
        script {
          try {
            // Initialize Terraform
            sh 'terraform init'
          } catch (Exception e) {
            currentBuild.result = 'FAILURE'
            error("Terraform initialization failed: ${e.getMessage()}")
          }
        }
      }
    }

    stage('Terraform Validate') {
      steps {
        script {
          try {
            // Validate Terraform configuration
            sh 'terraform validate'
          } catch (Exception e) {
            currentBuild.result = 'FAILURE'
            error("Terraform validation failed: ${e.getMessage()}")
          }
        }
      }
    }

    stage('Terraform Plan') {
      steps {
        script {
          try {
            // Generate Terraform execution plan
            sh 'terraform plan -out=tfplan'
          } catch (Exception e) {
            currentBuild.result = 'FAILURE'
            error("Terraform planning failed: ${e.getMessage()}")
          }
        }
      }
    }

    stage('Terraform Apply') {
      when {
        // Only run if previous stages were successful
        expression {
          currentBuild.resultIsBetterOrEqualTo('SUCCESS')
        }
      }
      steps {
        script {
          try {
            // Apply Terraform changes
            sh 'terraform apply -auto-approve tfplan'
          } catch (Exception e) {
            currentBuild.result = 'FAILURE'
            error("Terraform apply failed: ${e.getMessage()}")
          }
        }
      }
    }

    stage('Terraform Destroy') {
      when {
        // Only run if previous stages were successful
        expression {
          currentBuild.resultIsBetterOrEqualTo('SUCCESS')
        }
      }
      steps {
        script {
          try {
            // Destroy Terraform resources (use with caution)
            sh 'terraform destroy -auto-approve'
          } catch (Exception e) {
            currentBuild.result = 'FAILURE'
            error("Terraform destroy failed: ${e.getMessage()}")
          }
        }
      }
    }
  }
  post {
    always {
      // Clean up Terraform temporary files and directories
      deleteDir()
    }
  }
}
