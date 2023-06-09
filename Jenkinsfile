pipeline {
    agent any
    tools {
        maven 'maven-3.8.1'
        jdk 'java17'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package'
                sh '''
                    cd target
                    zip -r PowerNukkitX-Libs.zip libs
                '''
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar, target/PowerNukkitX-Libs.zip', excludes: 'target/*-sources.jar, target/*-shaded.jar', followSymlinks: false
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}
