package com.demo;

import com.demo.util.ReflexValidateField;
import com.demo.util.SqlExecutorNoJpql;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 小一点的单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilTests {

    private Logger logger = LoggerFactory.getLogger(UtilTests.class);

    @Autowired
    private SqlExecutorNoJpql sqlExecutorNoJpql;

    @Autowired
    private ReflexValidateField reflexValidateField;


    /**
     * queryConditions按照拼接条件来分割
     * Result:
     * ''
     * eq_name=a&a
     * eq_cc=15
     * eq_qd=1|5
     * eq_ss=15
     * eq_s=78
     */
    @Test
    public void contextLoadsTest1() {
        String symbol = "(query_|update_)";
        String regex = "\\&" + symbol + "|\\|" + symbol;

        String start = "|";
        String queryConditions = "query_eq_name=a&a&query_eq_cc=15|query_eq_qd=1|5|update_eq_ss=15&update_eq_s=78";
        // 去掉第一个Query 或 Update
        queryConditions = start + queryConditions;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(queryConditions);
        String[] split = pattern.split(queryConditions);
        while (matcher.find()) {
            logger.info("----UtilTests：" + matcher.group(0));
        }

        Arrays.stream(split).forEach(System.out::println);
    }

    @Test
    public void test() {
        String s = "|";
        Arrays.stream(s.split("\\|")).forEach(System.out::println);
    }

    @Test
    public void test3() {
        Object[] obj = sqlExecutorNoJpql.relationSplit("query_eq_name=a&a&query_eq_cc=15|query_eq_qd=1|5|update_eq_ss=15&update_eq_s=78");
        List<String> symbols = (List<String>) obj[0];
        List<String> querys = (List<String>) obj[1];
        logger.info("----UtilTests: 关联如下：");
        for (String symbol : symbols) {
            logger.info("----UtilTests:" + symbol);
        }

        for (String query : querys) {
            logger.info("----UtilTests:" + query);
        }
    }

    @Test
    public void test4() {
        String conRight = "isNotNull_name";
        String conError = "and";
        String[] strings = conError.split("=|_");
        for (String thisOne : strings) {
            logger.info("----UtilTests:" + thisOne);
        }
    }


    @Test
    public void test5() {
        try {
            Field[] fields = reflexValidateField.returnAllFiled(Class.forName("com.demo.entity.User"));
            for (Field field : fields) {
                logger.info("----ReflexValidateField： " + field.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void booleans() {
        Boolean flag = true, flag2 = true;
        s(flag,flag2);
        logger.info(""+flag);
        logger.info(""+flag2);
    }

    public void s(Boolean flag,Boolean flag2) {
        flag = false;
        flag2 = false;
    }


}
