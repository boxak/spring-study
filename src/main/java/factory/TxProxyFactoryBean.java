package factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import service.MailTransactionManager;
import service.TransactionHandler;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {

    Object target;
    PlatformTransactionManager transactionManager;
    MailTransactionManager mailTransactionManager;
    String pattern;
    // 다이나믹 프록시를 생성할 때 필요.
    Class<?> serviceInterface;

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

        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setMailTransactionManager(mailTransactionManager);
        txHandler.setPattern(pattern);

        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{ serviceInterface },
                txHandler
        );
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
