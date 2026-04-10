pipeline {
    agent none

    environment {
        DB_URL = "jdbc:postgresql://postgres:5432/bankdb"
        DB_USER = "sonar"
        DB_PASS = "sonar123"
        DOCKER_NETWORK = "bindnamed_jenkins_network"
        SONAR_URL = "http://sonarqube:9000"
        SONAR_AUTH_TOKEN = "squ_d7ed6d0c10446be8287b01b2660dada678e6469a"
    }

    stages {

        stage('Checkout') {
            agent any
            steps {
                git branch: 'main',
                    url: 'https://github.com/zakimudzakirh/bank-microservices-maven.git'
                
                stash name: 'source', includes: '*/'
            }
        }

        stage('Build & Test - Account Service') {
            agent { label 'agent-1' }
            steps {
                unstash 'source'
                
                dir('account-service') {
                    sh 'mvn clean test'
                }
            }
        }

        stage('Build & Test - Transaction Service') {
            agent any
            steps {
                unstash 'source'
                
                dir('transaction-service') {
                    sh 'mvn clean test'
                }
            }
        }

        stage('Sonar Scan - Account') {
            agent { label 'agent-1' }
            steps {
                dir('account-service') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_AUTH_TOKEN')]) {
                        sh '''
                        docker run --rm \
                        --network $DOCKER_NETWORK \
                        -v $(pwd):/usr/src \
                        sonarsource/sonar-scanner-cli \
                        -Dsonar.projectKey=account-service \
                        -Dsonar.sources=. \
                        -Dsonar.host.url=$SONAR_URL \
                        -Dsonar.token=$SONAR_AUTH_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Sonar Scan - Transaction') {
            agent any
            steps {
                dir('transaction-service') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_AUTH_TOKEN')]) {
                        sh '''
                        docker run --rm \
                        --network $DOCKER_NETWORK \
                        -v $(pwd):/usr/src \
                        sonarsource/sonar-scanner-cli \
                        -Dsonar.projectKey=transaction-service \
                        -Dsonar.sources=. \
                        -Dsonar.host.url=$SONAR_URL \
                        -Dsonar.token=$SONAR_AUTH_TOKEN
                        '''
                    }
                }
            }
        }

        stage('DB Migration - Account') {
            agent any
            steps {
                dir('account-service') {
                    sh '''
                    echo "=== RUN FLYWAY ACCOUNT ==="

                    docker run --rm \
                    --network $DOCKER_NETWORK \
                    --volumes-from jenkins \
                    flyway/flyway \
                    -url=$DB_URL \
                    -user=$DB_USER \
                    -password=$DB_PASS \
                    -locations=filesystem:$WORKSPACE/account-service/db/migration \
                    migrate
                    '''
                }
            }
        }

        stage('DB Migration - Transaction') {
            agent any
            steps {
                dir('transaction-service') {
                    sh '''
                    echo "=== FIX CHECKSUM ==="

                    docker run --rm \
                    --network $DOCKER_NETWORK \
                    --volumes-from jenkins \
                    flyway/flyway \
                    -url=$DB_URL \
                    -user=$DB_USER \
                    -password=$DB_PASS \
                    -locations=filesystem:$WORKSPACE/transaction-service/db/migration \
                    repair
                    '''

                    sh '''
                    echo "=== RUN FLYWAY TRANSACTION ==="

                    docker run --rm \
                    --network $DOCKER_NETWORK \
                    --volumes-from jenkins \
                    flyway/flyway \
                    -url=$DB_URL \
                    -user=$DB_USER \
                    -password=$DB_PASS \
                    -locations=filesystem:$WORKSPACE/transaction-service/db/migration \
                    migrate
                    '''
                }
            }
        }

        stage('Deploy Simulation') {
            agent any
            steps {
                echo "Deploy success"
            }
        }
    }
}