pipeline {
    agent any
    environment {
        COMMIT_HASH = "${sh(script:'git rev-parse --short HEAD', returnStdout: true).trim()}"
        /* groovylint-disable-next-line LineLength */
        AWS_LOGIN = 'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ID = '499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ACCESS_KEY = "${sh(script:'echo $AWS_ACCESS_KEY', returnStdout: true)}"
        DB_USERNAME = "${sh(script:'echo $DB_USERNAME', returnStdout: true)}"
        DB_PASSWORD = "${sh(script:'echo $DB_PASSWORD', returnStdout: true)}"
        DB_URL = "${sh(script:'echo $DB_URL', returnStdout: true)}"
        EXECUTION_ROLE_ARN = "${sh(script:'echo $EXECUTION_ROLE_ARN', returnStdout: true)}"
        /* groovylint-disable-next-line LineLength */
        TARGETGROUP_UTOPIA_EUREKA_DEV_ARN = "${sh(script:'echo $TARGETGROUP_UTOPIA_EUREKA_DEV_ARN', returnStdout: true)}"
        UTOPIA_PRIVATE_SUBNET_1 = "${sh(script:'echo $UTOPIA_PRIVATE_SUBNET_1', returnStdout: true)}"
        UTOPIA_PUBLIC_VPC_ID = "${sh(script:'echo $UTOPIA_PUBLIC_VPC_ID', returnStdout: true)}"
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
                sh "docker build -t utopia-eureka:$COMMIT_HASH ."
                sh 'docker images'
                sh "docker tag utopia-eureka:$COMMIT_HASH $AWS_ID/utopia-eureka:$COMMIT_HASH"
                sh "docker push $AWS_ID/utopia-eureka:$COMMIT_HASH"
            }
        }

        stage('Deploy') {
            steps {
                //    sh "touch ECSService.yml"
                //    sh "rm ECSService.yml"
                //    sh "wget https://raw.githubusercontent.com/Utopia/CloudFormationTemplates/main/ECSService.yml"
                sh """aws cloudformation deploy --stack-name UtopiaEurekaMS --template-file ./test-utopia-cftemplate.yml
                --parameter-overrides ApplicationName=UtopiaEurekaMS ECRepositoryUri=$AWS_ID/utopia-eureka:$COMMIT_HASH
                DBUrl=$DB_URL` DBUsername=$DB_USERNAME DBPassword=$DB_PASSWORD ExecutionRoleArn=$EXECUTION_ROLE_ARN
                SubnetID=$UTOPIA_PRIVATE_SUBNET_1 TargetGroupArnDev=$TARGETGROUP_UTOPIA_EUREKA_DEV_ARN
                VpcId=$UTOPIA_PUBLIC_VPC_ID  --capabilities \"CAPABILITY_IAM\" \"CAPABILITY_NAMED_IAM\"" """
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
