package com.jingwei.rpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class.
 *
 * @Author : fangwen
 * @create 2024/3/6 20:49
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {

    boolean status;  // 状态: true
    T data;   // new User
    String message;   // new User
    Exception ex;

    public static<T> RpcResponse<T> success(T data){
        RpcResponse<T> tRpcResponse = new RpcResponse<>();
        tRpcResponse.setStatus(true);
        tRpcResponse.setData(data);
        return tRpcResponse;
    }


    public static<T> RpcResponse<T> failed(String message){
        RpcResponse<T> tRpcResponse = new RpcResponse<>();
        tRpcResponse.setStatus(false);
        tRpcResponse.setData(null);
        tRpcResponse.setMessage(message);
        return tRpcResponse;
    }
}
