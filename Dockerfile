FROM adoptopenjdk/openjdk11
ENV USE_PROFILE local
#ENV USE_PROFILE server
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} kkukkie_bookstore.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "kkukkie_bookstore.jar"]