provider "aws" {
  region = "us-west-2"  # Replace with your desired region
}

resource "aws_lambda_function" "my_lambda_function" {
  filename      = "my_lambda_function.zip"  # Replace with the path to your function code
  function_name = "my_lambda_function"  # Replace with your desired function name
  role          = aws_iam_role.lambda_exec.arn  # Replace with the ARN of your IAM role
  handler       = "lambda_function.lambda_handler"  # Replace with the name of your function handler
  runtime       = "python3.9"  # Use Python 3.9 runtime
  
  environment {
    variables = {
      ENV_VAR = "some value"  # Replace with your desired environment variables
    }
  }
}

resource "aws_iam_role" "lambda_exec" {
  name = "lambda_exec_role"  # Replace with your desired role name

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  inline_policy {
    name = "lambda_exec_policy"

    policy = jsonencode({
      Version = "2012-10-17"
      Statement = [
        {
          Effect = "Allow"
          Action = [
            "logs:CreateLogGroup",
            "logs:CreateLogStream",
            "logs:PutLogEvents"
          ]
          Resource = "*"
        }
      ]
    })
  }
}

data "archive_file" "my_lambda_function_zip" {
  type        = "zip"
  source_dir  = "lambda_function"
  output_path = "my_lambda_function.zip"
}

resource "aws_s3_bucket" "lambda_code_bucket" {
  bucket_prefix = "my-lambda-function-code-"
}

resource "aws_s3_bucket_object" "my_lambda_function_object" {
  bucket       = aws_s3_bucket.lambda_code_bucket.id
  key          = "my_lambda_function.zip"
  source       = data.archive_file.my_lambda_function_zip.output_path
  content_type = "application/zip"
}

output "lambda_function_arn" {
  value = aws_lambda_function.my_lambda_function.arn
}
