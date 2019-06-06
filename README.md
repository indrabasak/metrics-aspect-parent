[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![Build Status][travis-badge]][travis-badge-url]
[![Quality Gate][sonarqube-badge]][sonarqube-badge-url] 
[![Technical debt ratio][technical-debt-ratio-badge]][technical-debt-ratio-badge-url] 
[![Coverage][coverage-badge]][coverage-badge-url]

Metrics Aspect
==========================
This project provides a way of generating Dropwizard metrics based on Spring
AOP. The below usage shows on how to use different types of metric annotations.
Th example module shows you how to use the `metric-aspect` library with
Spring Boot. 

# Usage 

## Timed Annotation

`@Timed` annotation is used to generate `timer` metrics. A timer aggregates timing 
durations and provides duration and throughput statistics. A `@Timed` annotation 
can be used at `class` or `method` level.
```java
package com.example;

@Timed
private class MyClass {
    public String myMethod(String name) {
        return "Hello " + name;
    }
    
}
```  

```java
package com.example;

private class MyClass {
    @Timed
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
``` 
It will generate metrics having the following name when method `myMethod` is executed:
```
timer.com.example.MyClass.myMethod
```

You can customize the name of your metric in couple of different ways by 
providing values for `name` and `absolute` parameters of `@Timed` annotation. 
It allows you to aggregate metrics from different methods of a class or classes.

```java
package com.example;

private class MyClass {
    @Timed(name="myMetric")
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
``` 
It will generate metrics having the following name when method `myMethod` is executed:
```
timer.com.example.MyClass.myMetric
```
You can also make the `absolute` attribute to be `true` (default is `false`).
```java
package com.example;

private class MyClass {
    @Timed(name="myMetric", absolute=true)
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
``` 
It will generate metrics having the following name when method `myMethod` is executed:
```
timer.myMetric
```

## Metered Annotation

`@Metered` annotation is used to generate `meter` metrics. A meter provides 
throughput statistics. Like `@Timed` annotation, a `@Metered` annotation can 
be used at `class` or `method` level. Similarly you can use `name` and `absolute`
parameters of `@Metered` annotation to customize metric names. 

`@Metered` annotation will generate metrics having the following name when method `myMethod` is executed:
```
meter.com.example.MyClass.myMetric
```
Similary, the customize metrics will have the following format:
```
meter.com.example.MyClass.myMetric
meter.myMetric
```

## Exception Metered Annotation

`@ExceptionMetered` annotation is used to generate `meter` metrics when 
exception occurs. A `@ExceptionMetered` annotation can be used at `class` or 
`method` level. Similarly you can use `name` and `absolute` parameters of 
`@Metered` annotation to customize metric names. You can also specify the 
exception type to report a specific exception.

```java
package com.example;

private class MyClass {
    @ExceptionMetered(cause =IllegalArgumentException.class)
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
``` 

`@ExceptionMetered` annotation will generate metrics having the following 
name when method `myMethod` is executed:
```
exception.com.example.MyClass.myMetric
```
Similary, the customize metrics will have the following format:
```
exception.com.example.MyClass.myMetric
exception.myMetric
```

## Custom JMX Metrics
There are cases where `Dropwizard` do not provide metrics you're interested. 
Some of this metrics are published as JMX MBeans such as Jetty or Tomcat 
thread pools. This project includes a custom `JMXGaugeSet` class which provides
the ability to publish a JMX MBean as Dropwizard metrics.

Here's an example of publishing the Tomcat threadpool JMX MBean having `ObjectName`
of `Tomcat:type=ThreadPool,name="http-nio-8080"`, as Dropwizard metrics:

```java
@Configuration
public class MyConfiguration {

    @Autowired
    private MetricRegistry registry;

    @PostConstruct
    public void init() {
        registry.register(
                MetricRegistry.name("tomcat", "threadpool", "http-nio-8080"),
                new JmxGaugeSet("Tomcat:type=ThreadPool,name=\"http-nio-8080\""));
    }
}
```

## Metric Fiter
There may be cases when you want to filter metrics based on name. In order to 
filter metrics add a YAML configuration file either in classpath or file 
system and specify the name of the file in property `metrics.filter`.

```yaml
metrics:
  filter: metric-filter.yml
```

Metrics you would like to show up as JMX MBeans are specified in a YAML file, 
named `metric-filter.yml` (same names as specified in the `metrics.filter` property.
```yaml
filter:
  properties:
    "[jvm.memory.pools]":
      type: memory
    "[jvm.memory.heap]":
      type: memory
    "[timer.org.basaki.example.book.controller.BookController.read]":
      type: gauge
```

[travis-badge]: https://travis-ci.org/indrabasak/metrics-aspect-parent.svg?branch=master
[travis-badge-url]: https://travis-ci.org/indrabasak/metrics-aspect-parent

[sonarqube-badge]: https://sonarcloud.io/api/project_badges/measure?project=org.basaki.metrics:metrics-aspect-parent&metric=alert_status
[sonarqube-badge-url]: https://sonarcloud.io/dashboard/index/org.basaki.metrics:metrics-aspect-parent

[technical-debt-ratio-badge]: https://sonarcloud.io/api/project_badges/measure?project=org.basaki.metrics:metrics-aspect-parent&metric=sqale_index
[technical-debt-ratio-badge-url]: https://sonarcloud.io/dashboard/index/org.basaki.metrics:metrics-aspect-parent

[coverage-badge]: https://sonarcloud.io/api/project_badges/measure?project=org.basaki.metrics:metrics-aspect-parent&metric=coverage
[coverage-badge-url]: https://sonarcloud.io/dashboard/index/org.basaki.metrics:metrics-aspect-parent
