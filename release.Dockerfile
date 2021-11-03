ARG SONARQUBE_VERSION

FROM sonarqube:${SONARQUBE_VERSION}

ARG PLUGIN_VERSION
ENV PLUGIN_VERSION=${PLUGIN_VERSION}

ADD --chown=sonarqube:sonarqube https://github.com/felixoi/sonarqube-community-branch-plugin/releases/download/${PLUGIN_VERSION}/sonarqube-community-branch-plugin-${PLUGIN_VERSION}.jar /opt/sonarqube/extensions/plugins/
ADD --chown=sonarqube:sonarqube https://github.com/checkstyle/sonar-checkstyle/releases/download/9.0.1/checkstyle-sonar-plugin-9.0.1.jar /opt/sonarqube/extensions/plugins/

ENV SONAR_WEB_JAVAADDITIONALOPTS="-javaagent:./extensions/plugins/sonarqube-community-branch-plugin-${PLUGIN_VERSION}.jar=web"
ENV SONAR_CE_JAVAADDITIONALOPTS="-javaagent:./extensions/plugins/sonarqube-community-branch-plugin-${PLUGIN_VERSION}.jar=ce"
