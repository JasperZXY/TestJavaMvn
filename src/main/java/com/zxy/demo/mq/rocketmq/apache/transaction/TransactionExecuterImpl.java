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

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;


/**
 * 这里是业务逻辑的处理
 */
public class TransactionExecuterImpl implements LocalTransactionExecuter {
    private MyService myService;

    public TransactionExecuterImpl(MyService myService) {
        this.myService = myService;
    }

    @Override
    public LocalTransactionState executeLocalTransactionBranch(final Message msg, final Object arg) {
        LocalTransactionState localTransactionState = myService.execute(msg);
        System.out.println("message:" + msg.toString() + " state:" + localTransactionState);
        return localTransactionState;
    }
}
