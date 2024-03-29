package com.jingwei.rpc.api;

public interface UserService {

    User findById(int id);

    User findById(int id, String name);

    long getId(int id);

    int getId(User user);

    String getName();
}
