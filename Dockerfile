FROM openjdk:8
COPY . /tmp
WORKDIR /tmp
CMD ["java", "-jar", "out/artifacts/Chat_jar/Chat.jar"]

