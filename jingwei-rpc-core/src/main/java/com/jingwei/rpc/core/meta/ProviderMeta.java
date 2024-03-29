package com.jingwei.rpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ProviderMeta {

   private Method method;

   String methodSign;

   Object serviceImpl;
}
