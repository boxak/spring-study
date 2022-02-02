package service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.LinkedList;
import java.util.Queue;

public class DummyMailSender implements MailSender {

    private Queue<SimpleMailMessage> msgQue;
    private boolean success;

    public boolean IsCommit() {
        return this.success;
    }

    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        msgQue.add(simpleMailMessage);
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

    }

    public void start() {
        this.msgQue = new LinkedList<>();
        System.out.println("mailSender start");
    }

    public void commit() {
        this.success = true;
        for (SimpleMailMessage msg : msgQue) {
            System.out.println(msg.getTo()[0]);
        }
    }

    public void rollback() {
        this.msgQue.clear();
        this.success = false;
        System.out.println("mailSender rollback");
    }
}
