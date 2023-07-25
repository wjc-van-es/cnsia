## 2-stage build

## first stage acquire and extract the uberjar build with maven on the container host
# Set root image
FROM eclipse-temurin:17 AS builder

# Set working directory
WORKDIR workspace

# The path to the jar file, which can be overridden with a --build-arg argument of the docker build command
ARG JAR_FILE=target/*.jar

# Copy the uberjar into an image layer
COPY ${JAR_FILE} catalog-service.jar

# extract the uber jar into another image layer
RUN java -Djarmode=layertools -jar catalog-service.jar extract


## second stage copy all extracted directories into a separate layer discarding the rest of the first stage
FROM    eclipse-temurin:17

# create and assign spring as user instead of the default root
# before copying everything into the working dir (so it will be owned by the spring user
# and the spring user will start the process defined by the ENTRYPOINT
RUN useradd spring
USER spring
WORKDIR workspace

# Copy each directory in a separate layer in the right order:
# from changing least often to most often and necessasitating a new image build
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./

# running the Spring Boot JarLauncher class
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]