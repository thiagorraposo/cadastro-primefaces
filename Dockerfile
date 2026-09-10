FROM eclipse-temurin:25-jdk
RUN apt-get update && apt-get install -y --no-install-recommends curl ca-certificates
ARG WILDFLY_VERSION=40.0.0.Final
ARG MYSQL_CONNECTOR_VERSION=9.3.0
RUN mkdir -p /opt/wildfly && \
    curl -fSL "https://repo.maven.apache.org/maven2/org/wildfly/wildfly-dist/${WILDFLY_VERSION}/wildfly-dist-${WILDFLY_VERSION}.tar.gz" -o /tmp/wildfly.tar.gz && \
    tar -xzf /tmp/wildfly.tar.gz --strip-components=1 -C /opt/wildfly && \
    curl -fSL "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/${MYSQL_CONNECTOR_VERSION}/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar" -o /tmp/mysql-connector.jar
COPY docker/configure.cli /tmp/configure.cli
RUN /opt/wildfly/bin/jboss-cli.sh --file=/tmp/configure.cli && \
    useradd --system --uid 10001 --home-dir /opt/wildfly wildfly && \
    chown -R wildfly:wildfly /opt/wildfly
COPY --chown=wildfly:wildfly target/cadastro.war /opt/wildfly/standalone/deployments/cadastro.war
USER wildfly
EXPOSE 8080
CMD ["/opt/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
