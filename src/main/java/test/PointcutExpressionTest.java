package test;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {

    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("");
    }

}
