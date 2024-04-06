package com.jingwei.rpc.provider.serviceImpl;

import com.jingwei.rpc.api.User;
import com.jingwei.rpc.api.UserService;
import com.jingwei.rpc.core.annotation.JwProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@JwProvider
public class UserServiceImpl implements UserService {

    @Autowired
    private Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, environment.getProperty("server.port")
                +"_" + "JW-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, name);
    }

    @Override
    public long getId(int id) {
        return 0;
    }

    @Override
    public long getId(long id) {
        return 1;
    }

    @Override
    public long getId(float id) {
        return 2;
    }

    @Override
    public int getId(User user) {
        return user.getId();
    }

    @Override
    public List<Integer> getIds(List<Integer> ids) {
        return ids;
    }

    @Override
    public String getName() {
        return null;
    }
}
