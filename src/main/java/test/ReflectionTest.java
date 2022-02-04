package test;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import proxy.Hello;
import proxy.HelloTarget;

import java.lang.reflect.Method;

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
}
