pipeline {
    agent any

    parameters {
        string(name: 'projectKey', defaultValue: '')
        string(name: 'projectToken', defaultValue: '')
    }
   
    stages{
        stage('Static Analysis with SonarQube') {
   	        steps {
                script {
                    sh """/var/lib/jenkins/sonar-scanner/bin/sonar-scanner \
                    -Dsonar.projectKey=${params.projectKey} \
                    -Dsonar.sources=. \
                    -Dsonar.css.node=. \
                    -Dsonar.host.url=http://192.168.1.112:9000\
                    -Dsonar.login=${params.projectToken}"""
                }
       		}
	    }
	}
}