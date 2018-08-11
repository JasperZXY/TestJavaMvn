/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zxy.demo.mq.rocketmq.apache.transaction;

import com.zxy.demo.mq.rocketmq.apache.Config;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;


/**
 * 注意，发送事务消息，需要配置主从，不然不会调用LocalTransactionExecuter中的方法
 */
public class TransactionProducer {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        MyService myService = new MyService();

        TransactionCheckListener transactionCheckListener = new TransactionCheckListenerImpl(myService);
        TransactionMQProducer producer = new TransactionMQProducer("producer");
        producer.setNamesrvAddr(Config.ADDR);
        producer.setCheckThreadPoolMinSize(2);
        producer.setCheckThreadPoolMaxSize(2);
        producer.setCheckRequestHoldMax(2000);
        producer.setTransactionCheckListener(transactionCheckListener);
        producer.start();

        TransactionExecuterImpl tranExecuter = new TransactionExecuterImpl(myService);
        for (int i = 0; i < 10; i++) {
            try {
                Message msg =
                    new Message(Config.TOPIC, Config.TAG, "KEY" + i, ("Hello RocketMQ " + i).getBytes());
                SendResult sendResult = producer.sendMessageInTransaction(msg, tranExecuter, null);
                System.out.printf("%s%n", sendResult);

                Thread.sleep(10);
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(2 * 60 * 1000);

        producer.shutdown();
    }
}
