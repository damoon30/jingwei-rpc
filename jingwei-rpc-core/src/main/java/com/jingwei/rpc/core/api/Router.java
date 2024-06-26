package com.jingwei.rpc.core.api;

import java.util.List;

/**
 *
 */
public interface Router<T> {

    List<T> route(List<T> providers);

    Router Default = p -> p;
}
