FROM eclipse-temurin:21
WORKDIR /opt/ipms
COPY ./target/spring-ai-redis-demo-0.0.1-SNAPSHOT.jar ipms.jar
ENTRYPOINT ["java","-jar","/opt/ipms/ipms.jar"]