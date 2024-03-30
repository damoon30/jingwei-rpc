package com.jingwei.rpc.provider.serviceImpl;

import com.jingwei.rpc.api.User;
import com.jingwei.rpc.api.UserService;
import com.jingwei.rpc.core.annotation.JwProvider;
import org.springframework.stereotype.Component;

@Component
@JwProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "JW-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return null;
    }

    @Override
    public long getId(int id) {
        return 0;
    }

    @Override
    public int getId(User user) {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }
}
