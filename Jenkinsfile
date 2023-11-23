pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }
    parameters {
        booleanParam(name: 'RELEASE', defaultValue: false, description: '')
        string(name: 'VERSION', defaultValue: '', description: '')
    }
    environment {
        GROUP_ID = ''
        ARTIFACT_ID = ''
        NAME = ''

        REPO_PATH = 'RStenzhorn'

        ARTIFACTORY_URL = 'https://artifactory.rjst.de/artifactory'
        ARTIFACTORY_REPO_NAME = 'maven-local-rjst'

        DEPLOY_USER = 'minecraft'
        DEPLOY_URL = 'vpn.rjst.de'
        DEPLOY_SCREEN = 'dev'
        DEPLOY_PATH = """/home/minecraft/${DEPLOY_SCREEN}/plugins"""
    }
    agent {
        kubernetes {
            yaml """
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: maven
            image: maven:3.9.5-eclipse-temurin-17
            command: ["cat"]
            tty: true
            volumeMounts:
              - name: maven-secret
                mountPath: /usr/maven
          volumes:
            - name: maven-secret
              secret:
                secretName: maven-settings-secret
                items:
                  - key: settings.xml
                    path: settings.xml
        """
        }
    }
    stages {
        stage('Setup: VERSION') {
            steps {
                container('maven') {
                    script {
                        def pomFile = readFile('pom.xml')
                        def matcher
                        matcher = pomFile =~ /<groupId>(.*?)<\/groupId>/
                        GROUP_ID = matcher[0][1]
                        matcher = pomFile =~ /<artifactId>(.*?)<\/artifactId>/
                        ARTIFACT_ID = matcher[0][1]
                        matcher = pomFile =~ /<version>(.*?)<\/version>/
                        VERSION = matcher[0][1]
                        matcher = pomFile =~ /<name>(.*?)<\/name>/
                        NAME = matcher[0][1]
                    }
                }
            }
        }

        stage('Setup: RELEASE') {
            when {
                expression {
                    params.RELEASE
                }
            }
            steps {
                container('maven') {
                    script {
                        withCredentials([sshUserPrivateKey(credentialsId: 'github', keyFileVariable: 'keyfile')]) {
                            sh """echo ${params.VERSION} | mvn gitflow:release"""
                            VERSION = params.VERSION
                        }
                    }
                }
            }
        }

        stage('Git: PUSH') {
            when {
                expression {
                    params.RELEASE
                }
            }
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'github', keyFileVariable: 'keyfile')]) {
                    sh """git -c core.sshCommand="ssh -i \${keyfile} -o StrictHostKeyChecking=no" push --tags"""
                    sh """git -c core.sshCommand="ssh -i \${keyfile} -o StrictHostKeyChecking=no" push origin develop"""
                    sh """git -c core.sshCommand="ssh -i \${keyfile} -o StrictHostKeyChecking=no" push origin master"""
                }
            }
        }

        stage('Maven: BUILD') {
            when {
                expression {
                    !params.RELEASE
                }
            }
            steps {
                container('maven') {
                    sh 'mvn -DskipTests=true package -s /usr/maven/settings.xml -Dmaven.wagon.http.ssl.insecure=true'
                }
            }
        }

        stage('Maven: TEST') {
            when {
                expression {
                    !params.RELEASE
                }
            }
            steps {
                container('maven') {
                    sh 'mvn test'
                }
            }
        }

        stage('Badge: SET') {
            steps {
                script {
                    addShortText(text: "Version: ${VERSION}")
                }
            }
        }

        stage('Artifactory: UPLOAD') {
            when {
                expression {
                    !params.RELEASE && !VERSION.contains("SNAPSHOT")
                }
            }
            steps {
                container('maven') {
                    sh """
                    mvn deploy:deploy-file \
                        -s /usr/maven/settings.xml \
                        -DgroupId=${GROUP_ID} \
                        -DartifactId=${ARTIFACT_ID} \
                        -Dversion=${VERSION} \
                        -Durl=${ARTIFACTORY_URL}/${ARTIFACTORY_REPO_NAME} \
                        -Dpackaging=jar \
                        -Dfile=target/${NAME}.jar \
                        -DrepositoryId=${ARTIFACTORY_REPO_NAME} \
                        -Dmaven.wagon.http.ssl.insecure=true \
                        -DskipTests=true
                """
                }
            }
        }
        stage('GitHub: DEPLOY') {
            when {
                expression {
                    !params.RELEASE && !VERSION.contains("SNAPSHOT")
                }
            }
            steps {
                script {
                    if (!VERSION.contains("SNAPSHOT")) {
                        def mdContent = readFile('release-notes.md')
                        mdContent = mdContent.replaceAll("\n", "\\\\n")
                        withCredentials([string(credentialsId: 'gitRelease', variable: 'TOKEN')]) {
                            def curlCommand = """
                                curl -L \
                                    -X POST \
                                    -H 'Accept: application/vnd.github+json' \
                                    -H 'Authorization: Bearer $TOKEN' \
                                    -H 'X-GitHub-Api-Version: 2022-11-28' \
                                    https://api.github.com/repos/${REPO_PATH}/${NAME}/releases \
                                    -d '{
                                        "tag_name": "${VERSION}",
                                        "target_commitish": "master",
                                        "name": "Release ${VERSION}",
                                        "body": "${mdContent}",
                                        "draft": false,
                                        "prerelease": false,
                                        "generate_release_notes": true
                                    }'
                            """

                            def result = sh(script: curlCommand, returnStdout: true)
                            echo "${result}"
                            def json = readJSON(text: result)
                            def releaseId = json.id

                            def uploadUrl = "https://uploads.github.com/repos/${REPO_PATH}/${NAME}/releases/${releaseId}/assets?name=${NAME}.jar"
                            def uploadCommand = "curl -L -H 'Authorization: Bearer $TOKEN' -H 'Content-Type: application/zip' --data-binary @target/${NAME}.jar ${uploadUrl}"
                            def resultUpload = sh(script: uploadCommand, returnStdout: true)
                            echo "${resultUpload}"
                        }
                    }
                }
            }
        }

        stage('Server: DEPLOY') {
            when {
                expression {
                    !params.RELEASE
                }
            }
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'github', keyFileVariable: 'keyfile')]) {
                    sh """scp -i \${keyfile} -o StrictHostKeyChecking=no target/${NAME}.jar ${DEPLOY_USER}@${DEPLOY_URL}:${DEPLOY_PATH}"""
                    sh """ssh -i \${keyfile} -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_URL} screen -S ${DEPLOY_SCREEN} -p 0 -X stuff 'stop^M'"""
                }
            }
        }
    }
}