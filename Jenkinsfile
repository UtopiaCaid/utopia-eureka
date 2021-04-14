pipeline {
    agent any
    environment {
        COMMIT_HASH = "${sh(script:'git rev-parse --short HEAD', returnStdout: true).trim()}"
        /* groovylint-disable-next-line LineLength */
        AWS_LOGIN = 'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ID = '499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ACCESS_KEY = "${sh(script:'echo $AWS_ACCESS_KEY', returnStdout: true)}"
        AWS_SECRET_MYSQL = credentials('dev/utopia/mysql')
        AWS_SECRET_TARGET_GROUPS = credentials('dev/utopia/target-groups')
        AWS_SECRET_VPC = credentials('dev/utopia/vpc')
        /* groovylint-disable-next-line LineLength */
        DB_USERNAME = "${sh(script:'echo $AWS_SECRET_MYSQL | jq -r \'. | .DB_USERNAME \'', returnStdout: true)}"
        DB_PASSWORD = "${sh(script:'echo $AWS_SECRET_MYSQL | jq -r \'. | .DB_PASSWORD \'', returnStdout: true)}"
        DB_URL = "${sh(script:'echo $AWS_SECRET_MYSQL | jq -r \'. | .DB_URL \'', returnStdout: true)}"
        /* groovylint-disable-next-line LineLength */
        EXECUTION_ROLE_ARN = "${sh(script:'echo $AWS_SECRET_TARGET_GROUPS | jq -r \'. | .EXECUTION_ROLE_ARN \'', returnStdout: true)}"
        /* groovylint-disable-next-line LineLength */
        TARGETGROUP_UTOPIA_EUREKA_DEV_ARN = "${sh(script:'echo $AWS_SECRET_TARGET_GROUPS | jq -r \'. | .TARGETGROUP_UTOPIA_EUREKA_DEV_ARN \'', returnStdout: true)}"
        /* groovylint-disable-next-line LineLength */
        TARGETGROUP_UTOPIA_EUREKA_PROD_ARN = "${sh(script:'echo $AWS_SECRET_TARGET_GROUPS | jq -r \'. | .TARGETGROUP_UTOPIA_EUREKA_PROD_ARN \'', returnStdout: true)}"
        /* groovylint-disable-next-line LineLength */
        UTOPIA_PRIVATE_SUBNET_1 = "${sh(script:'echo $AWS_SECRET_VPC | jq -r \'. | .UTOPIA_PRIVATE_SUBNET_1 \'', returnStdout: true)}"
        /* groovylint-disable-next-line LineLength */
        UTOPIA_PUBLIC_VPC_ID = "${sh(script:'echo $AWS_SECRET_VPC | jq -r \'. | .UTOPIA_PUBLIC_VPC_ID \'', returnStdout: true)}"
    }
    tools {
        maven 'Maven 3.6.3'
    }
    stages {
        stage('Package') {
            steps {
                echo 'Building..'

                script {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        stage('Build') {
            steps {
                echo 'Deploying....'
                sh "$AWS_LOGIN"
                sh "if test -f \"target/utopia-0.0.1-SNAPSHOT.jar\"; then echo \"target/utopia-0.0.1-SNAPSHOT.jar exists.\" fi"
                sh "docker build -t utopia-eureka:$COMMIT_HASH ."
                sh 'docker images'
                sh "docker tag utopia-eureka:$COMMIT_HASH $AWS_ID/utopia-eureka:$COMMIT_HASH"
                sh "docker push $AWS_ID/utopia-eureka:$COMMIT_HASH"
            }
        }

        stage('Deploy') {
            steps {
                sh 'export AWS_DEFAULT_REGION=us-east-2'
                /* groovylint-disable-next-line LineLength */
                echo "$DB_USERNAME"
                echo "$DB_PASSWORD"
                echo "$DB_URL"
                echo "$EXECUTION_ROLE_ARN"
                echo "$TARGETGROUP_UTOPIA_EUREKA_DEV_ARN"
                echo "$TARGETGROUP_UTOPIA_EUREKA_PROD_ARN"
                echo "$UTOPIA_PRIVATE_SUBNET_1"
                echo "$UTOPIA_PUBLIC_VPC_ID"
                /* groovylint-disable-next-line LineLength */
                sh 'chmod +x ./aws-cf-deploy.sh'
                sh './aws-cf-deploy.sh'
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker system prune -f'
            // sh "docker image prune -a"
            }
        }
    }
}
