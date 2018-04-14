FROM alpine:latest

ENV CASSANDRA_VERSION=3.11.2 \
    CASSANDRA_HOME=/opt/cassandra

RUN mkdir -p ${CASSANDRA_HOME}

RUN apk --update --no-cache add wget ca-certificates tar python bash curl && \
    wget http://artfiles.org/apache.org/cassandra/${CASSANDRA_VERSION}/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz -P /tmp && \
    tar -xvzf /tmp/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz -C /tmp/ && \
    mv /tmp/apache-cassandra-${CASSANDRA_VERSION} ${CASSANDRA_HOME} && \
    apk --purge del wget ca-certificates tar && \
    rm -r /tmp/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz \
          /var/cache/apk/*

ENV PATH=$PATH:${CASSANDRA_HOME}/apache-cassandra-${CASSANDRA_VERSION}/bin \
    CASSANDRA_CONF=${CASSANDRA_CONFIG}
