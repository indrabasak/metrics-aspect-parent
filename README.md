**iovmetric-aspect** 

** Usage **

*** Timed Annotation ***

`@Timed` annotation is used to generate `timer` metrics. A timer aggregates timing durations and provides duration
and throughput statistics. A `@Timed` annotation can be used at `class` or `method` level.
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

You can customize the name of your metric in couple of different ways by providing values for `name` and `absolute`
parameters of `@Timed` annotation. It allows you to aggregate metrics from different methods of a class or classes.

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

*** Metered Annotation ***

`@Metered` annotation is used to generate `meter` metrics. A meter provides throughput statistics. Like `@Timed`
annotation, a `@Metered` annotation can be used at `class` or `method` level. Similarly you can use `name` and `absolute`
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

*** Exception Metered Annotation ***

`@ExceptionMetered` annotation is used to generate `meter` metrics when exception occurs. A `@ExceptionMetered` 
annotation can be used at `class` or `method` level. Similarly you can use `name` and `absolute`
parameters of `@Metered` annotation to customize metric names. You can also specify the exception type to report a 
specific exception.

```java
package com.example;

private class MyClass {
    @ExceptionMetered(cause =IllegalArgumentException.class)
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
``` 

`@ExceptionMetered` annotation will generate metrics having the following name when method `myMethod` is executed:
```
exception.com.example.MyClass.myMetric
```
Similary, the customize metrics will have the following format:
```
exception.com.example.MyClass.myMetric
exception.myMetric
```

*** Exception Metered Annotation ***

`@ExceptionMetered` annotation is used to generate `meter` metrics when exception occurs. A `@ExceptionMetered` 
annotation can be used at `class` or `method` level. Similarly you can use `name` and `absolute`
parameters of `@Metered` annotation to customize metric names. You can also specify the exception type to report a 
specific exception.

```java
package com.example;

private class MyClass {
    @ExceptionMetered(cause =IllegalArgumentException.class)
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
``` 

*** Core Metric Annotation ***

`@CoreMetric` annotation is an utility annotation which generates 4 different kinds of metrics:
* `Concurrent Request`: Number of requests per time period. Generates meter metrics.
* `Stall Count`: If the response time takes longer than a threshold. By default the, the threshold is set at 3 sec. You
can change it by modifying `metric.stall.threshold` property. Generates meter metrics.
* `Errors Per Interval`: Number of exceptions encountered per time period. Generates meter metrics.
* `Response Time`: The average response time per time period. Generates timer metrics.

```java
package com.example;

@CoreMetric
private class MyClass {
    public String myMethod(String name) {
        return "Hello " + name;
   }
}
```