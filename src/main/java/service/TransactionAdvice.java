package service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {

    private PlatformTransactionManager transactionManager;
    private MailTransactionManager mailTransactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailTransactionManager(MailTransactionManager mailTransactionManager) {
        this.mailTransactionManager = mailTransactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        mailTransactionManager.start();
        try {
            Object ret = invocation.proceed();
            this.transactionManager.commit(status);
            this.mailTransactionManager.commit();
            return ret;
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            this.mailTransactionManager.rollback();
            throw e;
        }
    }
}
