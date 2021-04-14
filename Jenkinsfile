pipeline {
    agent any
    environment {
        COMMIT_HASH = "${sh(script:'git rev-parse --short HEAD', returnStdout: true).trim()}"
        /* groovylint-disable-next-line LineLength */
        AWS_LOGIN = 'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ID = '499898275313.dkr.ecr.us-east-2.amazonaws.com'
        AWS_ACCESS_KEY = "${sh(script:'echo $AWS_ACCESS_KEY', returnStdout: true)}"
        AWS_SECRET_MYSQL = credentials('mysql')
        AWS_SECRET_TARGET_GROUPS = credentials('target-groups')
        AWS_SECRET_VPC = credentials('vpc')
        //DB_USERNAME = "AWS_SECRET_MYSQL['DB_USERNAME']"
        //DB_PASSWORD = $AWS_SECRET_MYSQL['DB_PASSWORD']
        //DB_URL = $AWS_SECRET_MYSQL['DB_URL']
        //EXECUTION_ROLE_ARN = $AWS_SECRET_TARGET_GROUPS['EXECUTION_ROLE_ARN']
        /* groovylint-disable-next-line LineLength */
        //TARGETGROUP_UTOPIA_EUREKA_DEV_ARN = $AWS_SECRET_TARGET_GROUPS['TARGETGROUP_UTOPIA_EUREKA_DEV_ARN']
        /* groovylint-disable-next-line LineLength */
        //TARGETGROUP_UTOPIA_EUREKA_PROD_ARN = $AWS_SECRET_TARGET_GROUPS['TARGETGROUP_UTOPIA_EUREKA_PROD_ARN']
        //UTOPIA_PRIVATE_SUBNET_1 = $AWS_SECRET_VPC['UTOPIA_PRIVATE_SUBNET_1']
        //UTOPIA_PUBLIC_VPC_ID = $AWS_SECRET_VPC['UTOPIA_PUBLIC_VPC_ID']
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
                echo "$AWS_SECRET_MYSQL"
                echo "$AWS_SECRET_TARGET_GROUPS"
                echo "$AWS_SECRET_VPC"
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
