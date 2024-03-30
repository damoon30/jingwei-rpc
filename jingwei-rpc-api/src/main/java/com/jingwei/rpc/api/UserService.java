package com.jingwei.rpc.api;

import java.util.List;

public interface UserService {

    User findById(int id);

    User findById(int id, String name);

    long getId(int id);
    long getId(long id);
    long getId(float id);

    int getId(User user);
    List<Integer> getIds(List<Integer> ids);

    String getName();
}
