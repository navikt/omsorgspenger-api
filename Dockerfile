FROM navikt/java:11

RUN cat

COPY build/libs/*.jar ./app.jar