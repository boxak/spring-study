package service;

import domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService {

    private UserServiceImpl userServiceImpl;
    private PlatformTransactionManager transactionManager;
    private MailTransactionManager mailTransactionManager;

    public void setUserServiceImpl(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailTransactionManager(MailTransactionManager mailTransactionManager) {
        this.mailTransactionManager = mailTransactionManager;
    }


    @Override
    public void add(User user) {
        userServiceImpl.add(user);
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        mailTransactionManager.start();
        try {
            // 타깃 오브젝트에 위임하는 부분.
            userServiceImpl.upgradeLevels();

            this.transactionManager.commit(status);
            mailTransactionManager.commit();
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            mailTransactionManager.rollback();
            throw e;
        }
    }
}
