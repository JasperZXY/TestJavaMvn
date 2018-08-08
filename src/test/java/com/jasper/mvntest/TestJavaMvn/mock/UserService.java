package com.jasper.mvntest.TestJavaMvn.mock;

import com.jasper.mvntest.protostuff.User;

public interface UserService {
    User getById(int id);
}
