pipeline {
    agent { label 'node' }

    stages {
        stage('git_checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Anilbamnote/student-ui-app.git'
            }
        }

        stage('build') {
            steps {
                sh '/opt/maven/bin/mvn clean package'
            }
        }

        stage('test') {
            steps {
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar-cread') {
                    sh '/opt/maven/bin/mvn sonar:sonar'
                }
            }
        }

        // stage('Quality_Gate') {
        //     steps {
        //         timeout(time: 2, unit: 'MINUTES') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }

        stage('deploy') {
            steps {
                deploy adapters: [
                    tomcat9(
                        alternativeDeploymentContext: '', 
                        credentialsId: 'tomcat-cred', 
                        path: '', 
                        url: 'http://35.179.105.163:8080/'
                    )
                ], contextPath: '/', war: '**/*.war'
            }
        }
    }
}
