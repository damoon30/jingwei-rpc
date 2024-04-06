1、 @Import注解作用  

@Import 是 Java 中的一个注解（Annotation），它的作用是在 Spring 框架中用于导入其他配置类或配置项。具体来说，**@Import 注解允许你在一个配置类中引入其他配置类，使得被引入的配置类中的 Bean 或者配置项可以被当前配置类所管理。**  

在 Spring 中，通常通过注解的方式配置应用程序的依赖关系和配置信息。@Import 注解就是其中的一种用于管理配置的注解。它可以用在标有 @Configuration 注解的配置类上，或者用在普通的组件类上。

@Import 注解的作用是在 Spring 中管理配置信息和依赖关系时，实现模块化和组件化的设计，使得应用程序的配置更加灵活和可维护。通过 @Import 注解，可以将多个配置类或组件类纳入到一个配置类的管理范围内，从而实现统一管理和配置的目的。
```java
@Configuration
@Import({DatabaseConfig.class, SecurityConfig.class})
public class AppConfig {
    // AppConfig 类的配置信息
}

@Configuration
public class DatabaseConfig {
    
    @Bean
    public A createA(){
        return new A();
    }
}
```

2、ApplicationContestAware 作用

ApplicationContextAware 是 Spring 框架中的一个接口，用于在 Spring 容器初始化时，将容器的上下文（ApplicationContext）注入到实现了该接口的类中。它的作用主要是让 Spring 容器的上下文对象可以在应用程序中被访问和使用，以便获取容器中的 Bean 和其他资源。

具体来说，当一个类实现了 ApplicationContextAware 接口并实现了其中的方法 setApplicationContext()，Spring 在初始化该类时会自动调用这个方法，并将当前的 ApplicationContext 对象传递给这个方法，从而使得这个类可以持有 Spring 容器的上下文引用。

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyBean implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void doSomething() {
        // 使用 ApplicationContext 对象获取其他 Bean
        AnotherBean anotherBean = applicationContext.getBean(AnotherBean.class);
        anotherBean.doAnotherThing();
    }
}

public class AnotherBean {
    public void doAnotherThing() {
        System.out.println("Doing another thing...");
    }
}

```
在这个示例中，MyBean 类实现了 ApplicationContextAware 接口，并在 setApplicationContext() 方法中将传入的 ApplicationContext 对象保存起来。然后，在 doSomething() 方法中，可以通过这个 ApplicationContext 对象获取其他的 Bean，例如 AnotherBean。

通过 ApplicationContextAware 接口，Spring 容器可以将上下文对象注入到需要访问容器中的 Bean 或其他资源的类中，从而实现了依赖注入和资源获取的功能。这种机制可以使得代码更加灵活和可扩展，同时也方便了对 Spring 容器中资源的管理和使用。


3、@Target注解  

@Target 是 Java 中的一个元注解（Meta Annotation），用于标注其他注解的适用范围，即指定注解可以应用于哪些程序元素上。在 Java 中，注解可以应用于类、方法、字段等不同的程序元素上，而 @Target 注解就是用来规定这种适用范围的。

具体来说，@Target 注解的作用是告诉编译器，被标注的注解可以应用于哪些类型的程序元素，从而限定了注解的使用范围，避免了在不恰当的地方使用注解。@Target 注解使用 ElementType 枚举类型作为参数，指定注解可以应用的目标类型。

ElementType 枚举类型定义了以下几种目标类型：

TYPE：可以应用于类、接口（包括注解类型）、枚举等。  
FIELD：可以应用于字段（包括枚举常量）。  
METHOD：可以应用于方法。  
PARAMETER：可以应用于方法的参数。  
CONSTRUCTOR：可以应用于构造方法。  
LOCAL_VARIABLE：可以应用于局部变量。  
ANNOTATION_TYPE：可以应用于注解类型。  
PACKAGE：可以应用于包。  

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value() default "default value";
}

```


