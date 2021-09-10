pipeline {
    agent any
   
    stages{
        stage('Checkout'){
            steps {
		        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/ardionoroma/catatcovid.git']]])
            }
        }
        
        stage('Static Analysis with SonarQube') {
   	        steps {
                script {
                    sh '''/var/lib/jenkins/sonar-scanner/bin/sonar-scanner \
                    -Dsonar.projectKey=catatcovid \
                    -Dsonar.sources=. \
                    -Dsonar.css.node=. \
                    -Dsonar.host.url=http://192.168.1.112:9000\
                    -Dsonar.login=a807c2c555ce1645d57b19239d1543a860fcc4d0'''
                }
       		}
	    }
	}
}