pipeline {
    agent any
    environment {
        COMMIT_HASH = "${sh(script:'git rev-parse --short HEAD', returnStdout: true).trim()}"
        /* groovylint-disable-next-line LineLength */
        AWS_LOGIN = 'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ID = '499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ACCESS_KEY = "${sh(script:'echo $AWS_ACCESS_KEY', returnStdout: true)}"
        DB_USERNAME = credentials('DB_USERNAME')
        DB_PASSWORD = credentials('DB_PASSWORD')
        DB_URL = credentials('DB_URL')
        EXECUTION_ROLE_ARN = credentials('EXECUTION_ROLE_ARN')
        /* groovylint-disable-next-line LineLength */
        TARGETGROUP_UTOPIA_EUREKA_DEV_ARN = credentials('TARGETGROUP_UTOPIA_EUREKA_DEV_ARN')
        /* groovylint-disable-next-line LineLength */
        TARGETGROUP_UTOPIA_EUREKA_PROD_ARN = credentials('TARGETGROUP_UTOPIA_EUREKA_PROD_ARN')
        UTOPIA_PRIVATE_SUBNET_1 = credentials('UTOPIA_PRIVATE_SUBNET_1')
        UTOPIA_PUBLIC_VPC_ID = credentials('UTOPIA_PUBLIC_VPC_ID')
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
                sh 'export AWS_DEFAULT_REGION=us-east-2'
                /* groovylint-disable-next-line LineLength */
                echo "$TARGETGROUP_UTOPIA_EUREKA_PROD_ARN $UTOPIA_PRIVATE_SUBNET_1 $EXECUTION_ROLE_ARN $DB_PASSWORD $UTOPIA_PUBLIC_VPC_ID $DB_USERNAME $TARGETGROUP_UTOPIA_EUREKA_DEV_ARN"
                /* groovylint-disable-next-line LineLength */
                sh "aws cloudformation deploy --region us-east-2 --stack-name UtopiaEurekaMS --template-file test-utopia-cftemplate --parameter-overrides ApplicationName=UtopiaEurekaMS ECRepositoryUri=$AWS_ID/utopia-eureka:$COMMIT_HASH DBUrl=$DB_URL` DBUsername=$DB_USERNAME DBPassword=$DB_PASSWORD ExecutionRoleArn=$EXECUTION_ROLE_ARN SubnetID=$UTOPIA_PRIVATE_SUBNET_1 TargetGroupArnDev=$TARGETGROUP_UTOPIA_EUREKA_DEV_ARN TargetGroupArnProd=$TARGETGROUP_UTOPIA_EUREKA_PROD_ARN VpcId=$UTOPIA_PUBLIC_VPC_ID --capabilities \"CAPABILITY_IAM\" \"CAPABILITY_NAMED_IAM\""
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
