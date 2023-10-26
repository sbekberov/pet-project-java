FROM sbekberov/jdk as builder

ENV app_dir_app=/app
ENV config_dir=$app_dir_app/config
ENV application_dir=$app_dir_app/application

WORKDIR /pet-project
COPY . /pet-project

RUN ./gradlew build \
    && mkdir -p $application_dir $config_dir \
    && cp /pet-project/build/libs/*.jar $application_dir \
    && cp /pet-project/src/main/resources/*.yaml $config_dir

FROM sbekberov/jre

ENV app_dir_app=/app
ENV config_dir=$app_dir_app/config
ENV application_dir=$app_dir_app/application

WORKDIR $app_dir_app

COPY --from=builder $application_dir $application_dir
COPY --from=builder $config_dir $config_dir

VOLUME ["/mnt/app/config", "/app/config"]
VOLUME ["/mnt/app/application", "/app/application"]

EXPOSE 8888/tcp

CMD java -jar $application_dir/trello-app-0.0.1-SNAPSHOT.jar --spring.config.location=file://$config_dir/application.yaml
