package service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailTransactionManager {

    private boolean result;
    
    private MailSender mailSender;
    
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean isCommit() {
        return this.result;
    }

    public void start() {
        MailUtils.init();
        System.out.println("MailSender start");
    }
    
    public void commit() {
        result = true;
        System.out.println("MailSender commit");
        for (SimpleMailMessage msg : MailUtils.getMsgs()) {
            System.out.println(msg.getTo()[0]);
        }
        SimpleMailMessage[] mailMessages = (SimpleMailMessage[]) MailUtils.getMsgs();
        this.mailSender.send(mailMessages);
    }
    
    public void rollback() {
        result = false;
        System.out.println("MailSender rollback");
    }
}
