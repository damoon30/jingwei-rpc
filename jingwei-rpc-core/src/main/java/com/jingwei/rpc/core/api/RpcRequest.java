package com.jingwei.rpc.core.api;

import lombok.Data;

/**
 * Description for this class.
 *
 * @Author : fangwen
 * @create 2024/3/6 20:48
 */

@Data
public class RpcRequest {

    private String service; // 接口的全限定名，带目录的哪种
    private String method;  // 方法：findById
    private Object[] args;  // 参数： 100

}
