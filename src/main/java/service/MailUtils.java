package service;

import org.springframework.mail.SimpleMailMessage;

import java.util.LinkedList;
import java.util.Queue;

public class MailUtils {

    static Queue<SimpleMailMessage> msgQue;

    static public void init() {
        msgQue = new LinkedList<>();
    }

    static public void add(SimpleMailMessage simpleMailMessage) {
        msgQue.add(simpleMailMessage);
    }

    static public SimpleMailMessage[] getMsgs() {
        return (SimpleMailMessage[]) msgQue.toArray();
    }
}
