package com.kedacom.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * SQL执行器工具  选用JPA ORM
 *
 * @author 朱朝阳
 * @date 2019/9/18 18:37
 */
@Component
public class SqlExecutorWithJPQL {

    @Autowired
    private EntityManager entityManager;

    private Logger logger = LoggerFactory.getLogger(SqlExecutorWithJPQL.class);

    private Class eneityClass = null;

    private int allCount = 0;

    private Query query = null;

    private List content;

    /**
     * 不带分页查询
     */
    public Object executorSql(String sql, Map<String, Object> keyValues, String className) {
        return this.handleExecutorSql(sql, keyValues, className, null);
    }

    /**
     * 带有分页查询
     *
     * @param sql       拼接好的SQL         Eg: select * from trans_user p where p.id=:Id
     * @param keyValues 数据               Eg: [<Key,value>] --> <"id",1>
     * @param className 映射到的实体名称
     * @param pageable  包含排序 & 分页
     * @return
     */
    public Object executorSqlWithPage(String sql, Map<String, Object> keyValues, String className, Pageable pageable) {
        return this.handleExecutorSql(sql, keyValues, className, pageable);
    }


    private Object handleExecutorSql(String sql, Map<String, Object> keyValues, String className, Pageable pageable) {
        if (sql == null) {
            return null;
        }

        // 若Class全路径名称为null Or ""则不使用实体构造函数
        if (!StringUtils.isEmpty(className)) {
            try {
                eneityClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("---SqlExecutor Error: " + e.toString());
                return "ClassNotFound";
            }
            query = entityManager.createNativeQuery(sql, eneityClass);
        } else {
            query = entityManager.createNativeQuery(sql);
        }


        // 对SQL插值处理
        if (keyValues != null) {
            keyValues.entrySet().forEach(item -> {
                query.setParameter(item.getKey(), item.getValue());
            });
        }

        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        content = query.getResultList();

        // 缺憾 content.size并不是整个的数据
        return new PageImpl(content, pageable == null ? Pageable.unpaged() : pageable, content.size());
    }


    // TODO: 利用反射机制检查字段是否存在于数据库中

}
