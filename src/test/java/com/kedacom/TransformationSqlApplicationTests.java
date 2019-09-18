package com.kedacom;

import com.kedacom.util.SqlExecutorWithJPQL;
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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransformationSqlApplicationTests {

    private Logger logger = LoggerFactory.getLogger(TransformationSqlApplicationTests.class);

    @Autowired
    private SqlExecutorWithJPQL sqlExecutorWithJPQL;

    /**
     * 无参数
     * 有Class Bean
     * 无分页
     */
    @Test
    public void contextLoadsTest1() {
        logger.info("----SqlExecutor：" + sqlExecutorWithJPQL.toString());
        String sql = "select * from trans_user p";
        Object result = sqlExecutorWithJPQL.executorSql(sql, null, "com.kedacom.entity.User");
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
        Object result = sqlExecutorWithJPQL.executorSqlWithPage(sql, null, "com.kedacom.entity.User", pageable);
        if (result instanceof String) {
            logger.error("--- TransformationSqlApplicationTests：没有该表");
        } else {
            print(result);
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
