package com.jasper.mvntest.mq.rocketmq.apache;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class Consumer {
    public static void main(String[] args) {
        DefaultMQPushConsumer consumer =
                new DefaultMQPushConsumer("PushConsumer");
        consumer.setNamesrvAddr(Config.ADDR);
        try {
            //订阅PushTopic下Tag为push的消息
            consumer.subscribe(Config.TOPIC, Config.TAG);
            /**
             * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
             * 如果非第一次启动，那么按照上次消费的位置继续消费
             */
            consumer.setConsumeFromWhere(
                    ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener(
                    new MessageListenerConcurrently() {
                        public ConsumeConcurrentlyStatus consumeMessage(
                                List<MessageExt> msgs,
                                ConsumeConcurrentlyContext Context) {
                            for (Message msg : msgs) {
                                System.out.println(new String(msg.getBody()) + ":" + msg.toString());
                            }
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
