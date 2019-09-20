package com.demo;

import com.demo.entity.User;
import com.demo.util.SqlExecutorWithJPQL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * 测试的类已启用
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransformationSqlApplicationTests {

    private Logger logger = LoggerFactory.getLogger(TransformationSqlApplicationTests.class);

    @Autowired
    private SqlExecutorWithJPQL sqlExecutorWithJPQL;

    @Autowired
    private EntityManager entityManager;

    /**
     * 无参数
     * 有Class Bean
     * 无分页
     */
    @Test
    public void contextLoadsTest1() {
        logger.info("----SqlExecutor：" + sqlExecutorWithJPQL.toString());
        String sql = "select * from trans_user p";
        Object result = sqlExecutorWithJPQL.executorSql(sql, null, "com.demo.entity.User");
        if (result instanceof String) {
            logger.error("--- TransformationSqlApplicationTests：没有该表");
        } else {
            print(result);
        }
    }


    /**
     * 无参数
     * 无Class Bean
     * 无分页
     */
    @Test
    public void contextLoadsTest2() {
        logger.info("----SqlExecutor：" + sqlExecutorWithJPQL.toString());
        String sql = "select * from trans_user p";
        Object result = sqlExecutorWithJPQL.executorSql(sql, null, null);
        if (result instanceof String) {
            logger.error("--- TransformationSqlApplicationTests：没有该表");
        } else {
            print(result);
        }
    }


    /**
     * 无参数
     * 有Class Bean
     * 有分页
     */
    @Test
    public void contextLoadsTest3() {
        logger.info("----SqlExecutor：" + sqlExecutorWithJPQL.toString());
        String sql = "select * from trans_user p";
        Pageable pageable = new PageRequest(0, 2, null);
        Object result = sqlExecutorWithJPQL.executorSqlWithPage(sql, null, "com.demo.entity.User", pageable);
        if (result instanceof String) {
            logger.error("--- TransformationSqlApplicationTests：没有该表");
        } else {
            print(result);
        }
    }


    @Test
    public void test() {
        Query query = entityManager.createNativeQuery("select * from trans_user where id = 1", User.class);
        List<User> list = query.getResultList();
        for (User user : list) {
            logger.info(user.toString());
        }
    }


    /**
     * 同一天不写具体时间会有问题
     * 解决方案在下面一种
     * 原生JPQL对日期的支持
     */
    @Test
    public void test2() {
        Query query = entityManager.createNativeQuery("select * from trans_user where start_time BETWEEN '2019-09-18' AND '2019-09-18'", User.class);
        List<User> list = query.getResultList();
        for (User user : list) {
            logger.info(user.toString());
        }
    }

    @Test
    public void test3() {
        Query query = entityManager.createNativeQuery("select * from trans_user where start_time BETWEEN '2019-09-18 00:00:00.0' AND '2019-09-19 00:00:00.0'", User.class);
        List<User> list = query.getResultList();
        for (User user : list) {
            logger.info(user.toString());
        }
    }


    private void print(Object result) {
        if (result instanceof PageImpl) {
            // 获取记录总数
            Long size = ((PageImpl) result).getTotalElements();
            logger.info("----SqlExecutor 数据长度为：" + size);
            // 获取数据
            List list = ((PageImpl) result).getContent();
            for (Object var : list) {
                logger.info("----SqlExecutor：" + var.toString());
            }
        }
    }

}
