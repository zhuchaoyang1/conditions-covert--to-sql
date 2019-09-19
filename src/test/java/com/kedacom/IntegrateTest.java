package com.kedacom;

import com.kedacom.entity.User;
import com.kedacom.util.SqlExecutorNoJpql;
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
    private SqlExecutorNoJpql sqlExecutorNoJpql;

    private Logger logger = LoggerFactory.getLogger(IntegrateTest.class);

    /**
     * 集成测试1
     */
    @Test
    public void test1() {
        Object result = sqlExecutorNoJpql.builderExecutorSql("query_eq_names='zcy'&query_eq_password='admin'", "com.kedacom.entity.User");

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
