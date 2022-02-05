package test;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import proxy.Hello;
import proxy.HelloTarget;
import proxy.HelloUppercase;
import proxy.UppercaseHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ReflectionTest {
    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        Assertions.assertThat(name).hasSize(6);

        // length()
        Method lengthMethod = String.class.getMethod("length");
        Assertions.assertThat(lengthMethod.invoke(name)).isEqualTo(6);

        //charAt()
        Assertions.assertThat(name.charAt(0)).isEqualTo('S');

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        Assertions.assertThat(charAtMethod.invoke(name, 0)).isEqualTo('S');
    }

    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();
        Assertions.assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
        Assertions.assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
        Assertions.assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    }

    @Test
    public void HelloUppercase() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        Assertions.assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        Assertions.assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        Assertions.assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }
}
