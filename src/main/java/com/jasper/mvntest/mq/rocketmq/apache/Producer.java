package com.jasper.mvntest.mq.rocketmq.apache;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

public class Producer {

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("Producer");
        producer.setNamesrvAddr(Config.ADDR);
        try {
            producer.start();

            Message msg;
            SendResult result;
            for (int i=0; i<16; i++) {
                msg = new Message(Config.TOPIC, Config.TAG, Integer.toHexString(i), ("Just for push. i=" + i).getBytes());

                // 无序消息
                result = producer.send(msg);

                // 有序消息，通过MessageQueueSelector来确定消息发送到哪一个队列上
//                result = producer.send(msg, new MessageQueueSelector() {
//                    @Override
//                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
//                        return mqs.get((Integer)arg);
//                    }
//                }, 0); // 0对应select()方法中的arg
                System.out.println("i:" + i + " id:" + result.getMsgId() + " result:" + result.getSendStatus());
            }

            msg = new Message("PushTopic", "pull", "1", "Just for pull.".getBytes());
            result = producer.send(msg);
            System.out.println("id:" + result.getMsgId() + " result:" + result.getSendStatus());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdown();
        }
    }
}
