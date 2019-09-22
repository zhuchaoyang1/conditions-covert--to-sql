package com.demo.controller;

import com.demo.entity.User;
import com.demo.util.SqlExecutor;
import com.demo.util.vo.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 集成测试
 *
 * @Author: ${朱朝阳}
 * @Date: 2019/9/18 23:48
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private SqlExecutor sqlExecutor;

    /**
     * Get请求
     *
     * @param query      条件拼接
     * @param entityName 实体包名
     * @param is         分页之后是否需要获取总条数
     * @return
     */
    @GetMapping("/test1/{query}/{entityName}/{is}")
    public Object test1(@PathVariable String query, @PathVariable String entityName, @PathVariable boolean is) {
        Object result = sqlExecutor.builderExecutorSql(query, entityName, is);

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
        return result;
    }

    /**
     * Form表单提交方式
     * contentType：application/x-www-form-urlencoded
     *
     * @param query
     * @param entityName
     * @param is
     * @return
     */
    @PostMapping("/test2")
    public Object test2(@RequestParam String query, @RequestParam String entityName, @RequestParam boolean is) {
        Object result = sqlExecutor.builderExecutorSql(query, entityName, is);

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
        return result;
    }


    @PostMapping("/test3")
    public Object test3(@RequestBody Receiver receiver) {
        Object result = sqlExecutor.builderExecutorSql(receiver.getQuery(), receiver.getEntityName(), receiver.getIsNeedCount() == null ? false : true);

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
        return result;
    }


}
