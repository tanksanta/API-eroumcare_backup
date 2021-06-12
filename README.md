# Eroumcare System WEB - API
## 간략한 history
-  이로움케어 쇼핑몰(eroumcare.com) 이전에 '이로움시스템 웹' 이라는 어드민 웹이 먼저 있었습니다.   
이로움케어 쇼핑몰을 '쇼핑몰' 이라고 하고, 이로움시스템 웹을 '시스템'이라고 하겠습니다.   
쇼핑몰이 생겨나면서 기존에 시스템의 기능을 일부 옮겨오고자 했는데, 기존 시스템은 단일 서비스를 위해 개발되었으므로   
유지보수가 어렵고, 추후에는 운용을 중지할 예정이었기 때문에 새로운 모듈을 만들어 관리하고자 API를 새로 구성하였습니다.
   

## 개발환경
> - build : gradle 6.7
> - language: Java 1.8 (서버 환경:openjdk-1.8.0.252)
> - framework: Spring Boot 2.2.4 Relaese


## Build
1. `gradlew clean` 후 `gradlew bootJar` 
2. 빌드가 완료되면 프로젝트 루트 경로의 `/build` 에 .jar 파일이 생성된다.   
3. 구동서버에서 `java -jar 파일명.jar`
> dependencies 에서 로컬경로가 잡히지 않으면 `fileTree(dir:'lib', include: ['*.jar'])`   
> 이 안될수 있음. 이럴 경우에 `/lib` 내의 .jar 라이브러리들을 컴파일 경로로 포함시켜 주어야함


## Log
1. 로그 경로는 .jar 실행위치에서 `/eroumapi/eroumapi.log`
2. 날짜별로 생성되며 100m 단위로 .gz 압축되어 보관된다.
3. 로그레벨은 `log4j2.properties` 파일에서 수정
4. AOP 구성
> 서비스 단의 로그는 'LogAspect.java' 참고   
> 예외로그는 'ExceptionAdvice.java' 참고


## profile
> dev : 개발 및 테스트 profile `application-dev.properties` 파일 참고   
> real : 운용 profile `application-dev.properties` 파일 참고
