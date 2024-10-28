#=========================================================================
## BUILD STAGE 1 - JRE 추출
# Step 1: Amazon Corretto JDK 기반으로 빌더 이미지 생성
FROM amazoncorretto:17-alpine3.18 as builder-jre

# Step 2: binutils 패키지 설치 (오브젝트 파일을 복사하거나 변환시 필요)
RUN apk add --no-cache binutils

# Step 3: jlink를 사용하여 최소한의 JRE 생성
RUN $JAVA_HOME/bin/jlink \
         --module-path "$JAVA_HOME/jmods" \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jre

#=========================================================================
## BUILD STAGE 2 - APP 실행

FROM alpine:3.18.4

ENV JAVA_HOME=/jre
ENV PATH="$JAVA_HOME/bin:$PATH"

COPY --from=builder-jre /jre $JAVA_HOME

COPY ./build/libs/*.jar ./shop-server.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=docker", "-jar", "shop-server.jar"]