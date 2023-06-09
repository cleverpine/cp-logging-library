# CleverPine Logging Library

### Description
<p>CleverPine logging library is a lightweight and efficient solution that can easily be integrated into various Java applications. 
It utilizes Log4j2 as its underlying logger, providing robust and customizable logging capabilities. 
With this library, developers can quickly and easily add logging functionality to their applications, allowing them to track events, troubleshoot issues, 
and gather valuable insights into their software's performance.</p>

#### Features
1. Log4J2 Configuration using the agreed pattern for CleverPine BackEnd applications:  [DATE-TIME-ISO][TRACE-ID][SERVICE-NAME][CLASS][LOGLEVEL] - Log message
2. Spring Configuration to automatically set the SERVICE-NAME property
3. Annotation to configure an automatic interceptor in spring configuration which propagates the TRACE-ID in the logging context from the request headers or creates it if missing.

### Usage

After everything is configured, the logger is used simply by requesting it from the log manager:
```java
private Logger logger = LogManager.getLogger(TestController.class);
```
or by adding the @Log4j2 annotation from lombok on the class:
```java
@RestController
@Log4j2
public class TestController {
   @GetMapping("/test")
   public ResponseEntity<String> testController(){
      log.info("Test controller");
      TestService.test();
      return ResponseEntity.ok("Test");
   }
}
```

### SetUp

#### Spring Project

1. To enable the logging functionality, first you need to replace the default Spring logger dependency with Log4J2.
   ```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>3.0.1</version>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
            <version>3.0.1</version>
        </dependency>        
    ```

2. Add the springboot-starter-aop dependency to the dependency management configuration.
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
            <version>3.0.1</version>
            <scope>provided</scope>
        </dependency>
```
3. Add the cp-logging-library dependency to your pom.xml
```xml
        <dependency>
            <groupId>com.cleverpine</groupId>
            <artifactId>cp-logging-library</artifactId>
            <version>LOGGING_LIBRARY_VERSION</version>
        </dependency>
```

4. Then to enable the log4j2 programatic configuration provided by the library, you need to tell Log4J where to find the config plugins. 
In a spring application this needs to happen before the Spring context is initialized. When the log4J Plugin class is in the classpath of the project, 
the configuration happen automatically. However, when the plugin is in a library, it needs to be explicitly added. Just specify the packages which have to be scanned with a
Component Scan annotation.
```java
@ComponentScan(basePackages = {"com.cleverpine.springlogginglibrary.*", "<your application main package>"})
```

5. Finally you need to enable all additional Spring features of the logging library. Just add the @EnableCPLogging annotation above the main application class. 
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.cleverpine.springlogginglibrary.*", "<your application main package>"})
@EnableCPLogging
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

6. Ensure you have set the "spring.application.name" property in the application.properties or application.yaml. This name will be used as a serviceId in the log messages.
```properties
spring.application.name=library-test
```
7. Test the configuration. When you run the application, you should see the starting logs in the following format: 
```
2023-01-23 09:03:35.022[][library-test][org.springframework.boot.StartupInfoLogger][INFO] - Started LibraryTestApplication in 0.612 seconds (process running for 0.989)
```

8. Test the TraceId interceptor. When you trigger a http request to the app with a "traceId" header, it should automatically add it to the logs of the current request. 
When you make a request without the header, the traceId will be created by the interceptor.
```
// traceId header = 123456
2023-01-23 09:03:46.345[123456][library-test][com.cleverpine.librarytest.TestController][INFO] - This is a test message in the controller
// No traceId header
2023-01-23 09:03:55.761[13f34c37-a375-4403-88b1-7e79e00cddf1][library-test][com.cleverpine.librarytest.TestController][INFO] - This is a test message in the controller
```
### Logstash Configuration
The Logstash appender sends the logs via TCP to a Logstash instance in **JSON Format**.
<p> To enable log sending via **TCP** to **Logstash**, you need to set up the following properties in the application.properties or application.yaml file:</p>

```properties
logging.logstash.enabled=true
logging.logstash.host=<logstash host>
logging.logstash.port=<logstash port>
```
To add additional properties which you want to send in the JSON message, you can add them in the application.properties or application.yaml file. They will be automatically mapped
in both the main Thread Context and its child threads, as well as all the request threads:
```properties
logging.logstash.properties.<property name>=<property value>

logging.logstash.properties.stage=dev
logging.logstash.properties.appId=<some app id>
```

**Example JSON received by logstash:**
```json
{
   "message": "Method TestController.testController() executed in 4 ms",
   "threadId": 43,
   "loggerName": "com.cleverpine.springlogginglibrary.aop.PerformanceMeasureAspect",
   "instant": {
      "epochSecond": 1686737560,
      "nanoOfSecond": 9782000
   },
   "loggerFqcn": "org.apache.logging.log4j.spi.AbstractLogger",
   "@timestamp": "2023-06-14T10:12:40.033038845Z",
   "contextMap": {
      "stage": "dev",
      "appName": "example-app-name",
      "traceId": "e1b97a94-9532-4dd8-aac1-b2d3927835c8",
      "serviceId": "library-test",
      "appId": "some-example-app-id"
   },
   "@version": "1",
   "thread": "http-nio-8080-exec-2",
   "source": {
      "class": "com.cleverpine.springlogginglibrary.aop.PerformanceMeasureAspect",
      "classLoaderName": "app",
      "line": 23,
      "method": "measurePerformance",
      "file": "PerformanceMeasureAspect.java"
   },
   "threadPriority": 5,
   "endOfBatch": false,
   "level": "INFO"
}
```

### Filters 
To propagate the Logging parameters such as Trace ID to other services in the same Request Chain, currently a Spring WebClient filter is implemented.
It adds a "traceId" header in the request headers.
In order to use it in an application, you should simply add it into the web client configuration:
```java
import static com.cleverpine.springlogginglibrary.client.SpringWebClientLoggingExchangeFilter.cpLoggingFilter;

WebClient client = WebClient.builder()
                .baseUrl("<URL>")
                .filter(cpLoggingFilter())
                .build();
```

### Method Execution Time Monitoring
If you want to monitor the execution time of a Method call, you need to add the @PerformanceMeasure annotation ontop of the method declaration.
```java
    @PerformanceMeasure
    @GetMapping("/test")
    public ResponseEntity<String> testController(){
        log.info("This is a test message in the controller");
        return ResponseEntity.ok("OK");
    }
```

This will automatically log the execution time in the standard logging format.
```
2023-04-08 09:45:03.055[981f65d6-4b7a-4faf-96f5-c494530a3664][library-test][com.cleverpine.springlogginglibrary.aop.PerformanceMeasureAspect][INFO] - Method TestController.testController() executed in 3 ms
```