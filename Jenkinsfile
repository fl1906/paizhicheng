pipeline {
    agent {
        node {
            label 'maven'
        }

    }

    stages {
        stage('拉取代码') {
            agent none
            steps {
                container('maven') {
                    git(url: 'https://gitee.com/fl1906249647/paizhi-city.git', credentialsId: 'fl1906', branch: 'master', changelog: true, poll: false)
                    sh 'echo my-tag-name:  latest '
                    sh 'ls -al'
                }

            }
        }

        stage('项目编译') {
            agent none
            steps {
                container('maven') {
                    sh 'ls'
                    sh 'mvn clean package -Dmaven.test.skip=true'
                    sh 'ls ./ruoyi-admin/target'
                }

            }
        }

        stage('构建镜像') {
            parallel {
                stage('构建后端应用镜像') {
                    agent none
                    steps {
                        container('maven') {
                            sh 'ls -l'
                            sh 'docker build  --no-cache --network=host -t paizhicheng-backend:latest -f ./ruoyi-admin/src/main/resources/Dockerfile  ./'
                            sh 'docker build  --no-cache --network=host -t paizhicheng-management:latest -f ./ruoyi-ui/Dockerfile  ./'
                        }

                    }
                }

            }
        }
        stage('推送镜像') {
            parallel {
                stage('推送后端应用镜像') {
                    agent none
                    steps {
                        container('maven') {
                            withCredentials([usernamePassword(credentialsId : 'aliyun' ,usernameVariable : 'DOCKER_USER_VAR' ,passwordVariable : 'DOCKER_PWD_VAR' ,)]) {
                                sh 'echo "$DOCKER_PWD_VAR" | docker login $REGISTRY -u "$DOCKER_USER_VAR" --password-stdin'
                                sh 'docker tag paizhicheng-backend:latest $REGISTRY/$DOCKERHUB_NAMESPACE/paizhicheng-backend:latest '
                                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/paizhicheng-backend:latest '

                                sh 'docker tag paizhicheng-management:latest $REGISTRY/$DOCKERHUB_NAMESPACE/paizhicheng-management:latest '
                                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/paizhicheng-management:latest '

                            }

                        }

                    }
                }

}}

        stage('deploy to dev') {
            steps {
                container('maven') {
                    withCredentials([
                        kubeconfigFile(
                        credentialsId: env.KUBECONFIG_CREDENTIAL_ID,
                        variable: 'KUBECONFIG')
                    ]) {
                        script {
                            def serviceExists = sh (
                                script: 'kubectl get service paizhicheng -n paizhicheng',
                                returnStatus: true
                            )

                            if (serviceExists == 0) {
                                sh 'kubectl delete service paizhicheng -n paizhicheng --cascade'
                            }


                            sh 'envsubst < deploy.yaml | kubectl apply -f - --force --grace-period=0'

                        }
                    }
                }
            }
        }





    }
    environment {
        KUBECONFIG_CREDENTIAL_ID = 'demo'
        REGISTRY = 'registry.cn-hangzhou.aliyuncs.com'
        DOCKERHUB_NAMESPACE = 'choose-course-system'
    }

}
