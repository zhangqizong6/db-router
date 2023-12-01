package com.zqz.dbrouter;

import com.zqz.dbrouter.annotation.DBRouter;

public interface IUserDao {

    @DBRouter(key = "userId")
    void insertUser(String req);
}
