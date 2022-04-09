package factory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import service.MailTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {

    Object target;
    PlatformTransactionManager transactionManager;
    MailTransactionManager mailTransactionManager;
    String pattern;
    // 다이나믹 프록시를 생성할 때 필요.
    Class<?> serviceInterface;

    class TxAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {

            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            mailTransactionManager.start();

            try {
                Object ret = invocation.proceed();
                transactionManager.commit(status);
                mailTransactionManager.commit();
                return ret;
            } catch (Exception e) {
                transactionManager.rollback(status);
                mailTransactionManager.rollback();
                throw e;
            }
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailTransactionManager(MailTransactionManager mailTransactionManager) {
        this.mailTransactionManager = mailTransactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgradeLevels");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new TxAdvice()));

        return pfBean.getObject();
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        // 싱글톤 빈이 아니라는 뜻이 아니라 getObject()가
        // 매번 같은 오브젝트를 리턴하지 않는다는 의미.
        return false;
    }
}
