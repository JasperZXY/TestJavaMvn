package com.zxy.demo.mq.rocketmq.apache.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MyService {
    private ConcurrentHashMap<String, LocalTransactionState> stateMap = new ConcurrentHashMap<>();

    private String getMessageKey(Message message) {
        return message.getKeys();
    }

    public LocalTransactionState getState(Message message) {
        return stateMap.get(getMessageKey(message));
    }

    public LocalTransactionState execute(Message message) {
        System.out.println(new String(message.getBody()));
        LocalTransactionState state;
        switch (new Random().nextInt(4)) {
            case 0:
                state = LocalTransactionState.COMMIT_MESSAGE;
                stateMap.put(getMessageKey(message), state);
                return state;
            case 1:
                state = LocalTransactionState.ROLLBACK_MESSAGE;
                stateMap.put(getMessageKey(message), state);
                return state;
            case 2:
                state = LocalTransactionState.UNKNOW;
                stateMap.put(getMessageKey(message), state);
                return state;
        }

        System.out.println("throw new RuntimeException");
        throw new RuntimeException("unknow runtime exception");
    }
}
