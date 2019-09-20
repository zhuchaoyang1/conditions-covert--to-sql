package com.demo;

import com.demo.entity.User;
import com.demo.util.SqlExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 用作集成测试
 * @author 朱朝阳
 * @date 2019/9/19 14:41
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrateTest {

    @Autowired
    private SqlExecutor sqlExecutor;

    private Logger logger = LoggerFactory.getLogger(IntegrateTest.class);

    /**
     * 集成测试Eq 操作符
     * Result：测试成功
     */
    @Test
    public void testEq() {
        Object result = sqlExecutor.builderExecutorSql("query_eq_names='zcy'&query_eq_password='admin'", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试LessThan 操作符
     * Result：测试成功
     */
    @Test
    public void testLessThan() {
        Object result = sqlExecutor.builderExecutorSql("query_lessThan_age=18", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }


    /**
     * 集成测试GreaterThan 操作符
     * Result：测试成功
     */
    @Test
    public void testGreaterThan() {
        Object result = sqlExecutor.builderExecutorSql("query_greaterThan_age=18&query_eq_name='ceshi'", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }


    /**
     * 集成测试Like 操作符
     * Result：测试成功
     */
    @Test
    public void testLike() {
        Object result = sqlExecutor.builderExecutorSql("query_like_name='%c%'", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试NotLike 操作符
     * Result：测试成功
     */
    @Test
    public void testNotLike() {
        Object result = sqlExecutor.builderExecutorSql("query_notLike_name='%c%'", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试In 操作符
     * Result：测试成功
     */
    @Test
    public void testIn() {
        Object result = sqlExecutor.builderExecutorSql("query_in_age=(0,400)", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }


    /**
     * 集成测试NotIn 操作符
     * Result：测试成功
     */
    @Test
    public void testNotIn() {
        Object result = sqlExecutor.builderExecutorSql("query_notIn_age=(0,400)", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试Not 操作符
     * Result：测试成功
     *
     * Describe:
     * Tinyint 1: true  0: false
     */
    @Test
    public void testNot() {
        Object result = sqlExecutor.builderExecutorSql("query_not_gender=1", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试Between 操作符
     * Result: 测试失败 // TODO 日期问题
     */
    @Test
    public void testBetween() {
        Object result = sqlExecutor.builderExecutorSql("query_between_startTime=('2019-09-19 08:48:37' and '2019-09-20 08:48:41')", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }


    /**
     * 集成测试IsNull 操作符
     * Result: 测试成功
     */
    @Test
    public void testIsNull() {
        Object result = sqlExecutor.builderExecutorSql("query_isNull_gender", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }


    /**
     * 集成测试IsNotNull 操作符
     * Result: 测试成功
     */
    @Test
    public void testIsNotNull() {
        Object result = sqlExecutor.builderExecutorSql("query_isNotNull_gender", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试NotNull 操作符
     * Result: 测试成功
     */
    @Test
    public void testNotNull() {
        Object result = sqlExecutor.builderExecutorSql("query_notNull_gender", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }

    /**
     * 集成测试OrderBy 操作符
     * Result: 测试成功
     */
    @Test
    public void testOrderBy() {
        Object result = sqlExecutor.builderExecutorSql("query_eq_name='admin'|query_eq_name='zcy'&query_orderBy_id=desc", "com.demo.entity.User");

        if (result instanceof String) {
            logger.error("---IntegrateTest:" + result);
        }

        if (result instanceof List) {
            for (User user : (List<User>) result) {
                logger.info(user.toString());
            }
        }
    }








}
