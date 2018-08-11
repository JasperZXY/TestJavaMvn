package com.zxy.demo.mock;

import com.zxy.demo.protostuff.User;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceMock {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceMock.class);

    @Test
    public void getById() {
        MockingProgress mockingProgress = ThreadSafeMockingProgress.mockingProgress();
        logger.info("mockingProgress:{}", mockingProgress);

        UserService userService = Mockito.mock(UserService.class);
        User user1 = new User();
        user1.setId(1);
        user1.setName("user-1");
        User user2 = new User();
        user2.setId(2);
        user2.setName("user-2");
        Mockito.when(userService.getById(1)).thenReturn(user1);
        Mockito.when(userService.getById(2)).thenReturn(user2);

        logger.info("1:" + userService.getById(1));
        logger.info("2:" + userService.getById(2));
    }
}
