pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        MYSQL_ROOT_PASSWORD = credentials('mysql-root-password')
        IMAGE_NAME = 'abdellatif18722/tricol-supplierchain'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Test') {
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean test'
            }
            post { always { junit testResults: 'target/surefire-reports/*.xml' } }
        }

        stage('Build') {
            steps { sh './mvnw clean package -DskipTests' }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                    sh '''
                        docker-compose down --remove-orphans || true
                        docker-compose pull || true
                        docker-compose up -d || echo "Some containers are already running. Skipping start."
                        docker ps --format "table {{.Names}}\t{{.Status}}"
                    '''
                }
        }
    }

    post {
        always { cleanWs() }
        success { echo 'Pipeline succeeded!' }
        failure { echo 'Pipeline failed!' }
    }
}
