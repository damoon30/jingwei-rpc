package com.jingwei.rpc.provider.serviceImpl;

import com.jingwei.rpc.api.Order;
import com.jingwei.rpc.api.OrderService;
import com.jingwei.rpc.core.annotation.JwProvider;
import org.springframework.stereotype.Component;

@Component
@JwProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        return new Order(id.longValue(), 1000.1f);
    }
}
