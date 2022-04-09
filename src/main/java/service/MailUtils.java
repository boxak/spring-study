package service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.LinkedList;
import java.util.Queue;

public class MailUtils {

    static Queue<SimpleMailMessage> msgQue;

    static public void init() {
        msgQue = new LinkedList<>();
    }

    static public void add(SimpleMailMessage simpleMailMessage) {
        if (CollectionUtils.isEmpty(msgQue)) return;
        msgQue.add(simpleMailMessage);
    }

    static public SimpleMailMessage[] getMsgs() {
        return (SimpleMailMessage[]) msgQue.toArray();
    }

    static public boolean isEmpty() {
        return CollectionUtils.isEmpty(msgQue);
    }
}
