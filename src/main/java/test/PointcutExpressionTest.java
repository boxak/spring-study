package test;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import pointcut.Bean;
import pointcut.Target;

public class PointcutExpressionTest {

    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int " +
                "pointcut.Target.minus(int,int) " +
                "throws java.lang.RuntimeException)");

        Assertions.assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class),null)).isTrue();

        Assertions.assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null))
                .isFalse();

        Assertions.assertThat(pointcut.getClassFilter().matches(Bean.class) &&
                pointcut.getMethodMatcher().matches(
                        Target.class.getMethod("method"),null
                )).isFalse();
    }

}
