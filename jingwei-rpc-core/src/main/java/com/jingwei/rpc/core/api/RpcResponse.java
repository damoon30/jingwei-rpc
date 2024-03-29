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
    Exception ex;

}
