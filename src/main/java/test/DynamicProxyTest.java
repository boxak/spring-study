package test;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import proxy.UppercaseHandler;

import java.lang.reflect.Proxy;

public class DynamicProxyTest {

    @Test
    public void simpleProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] { Hello.class },
                new UppercaseHandler(new HelloTarget())
        );
    }

    @Test
    public void proxyFactoryBean() {
        // ProxyFactoryBean은 알려주지 않아도 타겟 오브젝트가
        // 구현하는 인터페이스 정보를 알아낸다.
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assertions.assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        Assertions.assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        Assertions.assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assertions.assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        Assertions.assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        Assertions.assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            // 리플렉션의 Method와 달리, MethodInvocation은 메소드 정보와
            // 타깃 오브젝트를 알고 있어서 타깃 오브젝트를 전달할 필요X
            String ret = (String) methodInvocation.proceed();
            return ret.toUpperCase();
        }
    }

    static interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {

        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }

}
