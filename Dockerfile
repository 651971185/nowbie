FROM java:8

ADD target/gateway-function-0.0.1-SNAPSHOT.jar /

ENV PROFILE=dev


# CMD ["java", "-javaagent:/usr/local/skywalking/agent/skywalking-agent.jar","-jar", "-Dspring.profiles.active=${PROFILE}", "servless-api.jar"]
CMD ["java","-jar", "-Dspring.profiles.active=${PROFILE}", "gateway-function-0.0.1-SNAPSHOT.jar"]