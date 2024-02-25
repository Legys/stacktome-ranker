FROM sbtscala/scala-sbt:eclipse-temurin-11.0.17_8_1.8.2_2.12.17

WORKDIR /usr/src/app

COPY . .

EXPOSE 9000

RUN sbt compile

ENV FETCH_INTERVAL_MINUTES="1"
ENV COOKIE_SESSION_HEADER="vstat_session=your_session_id"

CMD ["sbt", "run"]