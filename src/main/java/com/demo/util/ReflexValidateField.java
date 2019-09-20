package com.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 反射机制验证字段是否合法
 * @author 朱朝阳
 * @date 2019/9/19 17:31
 */
@Component
public class ReflexValidateField {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 返回className下所有Filed
     * @param entityClass
     * @exception ClassNotFoundException
     * @return
     */
    public Field[] returnAllFiled(Class entityClass) {

        Field[] fields = entityClass.getDeclaredFields();

        return fields;
    }


}
