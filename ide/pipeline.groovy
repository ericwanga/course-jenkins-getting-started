pipeline {
    agent any
    triggers { pollSCM('* * * * *') }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/ericwanga/jgsu-spring-petclinic.git', 
                branch: 'main'
            }            
        }
        stage('Build') {
            steps {
                sh './mvnw clean package'
               // sh 'false' //'false'
            }
        
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
                changed {
                    emailext subject: "job \'${JOB_NAME}\' (${BUILD_NUMBER}) ${currentBuild.result}", 
                        body: 'Please go to ${BUILD_URL} and verify the build', 
                        attachLog: true, 
                        compressLog: true, 
                        recipientProviders: [upstreamDevelopers(), requestor()], 
                        to: 'eric.wang.au@outlook.com'
                }
            }
        }
    }
}
