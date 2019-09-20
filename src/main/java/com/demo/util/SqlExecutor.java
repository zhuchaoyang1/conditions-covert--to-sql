package com.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 拼接成JPQL
 *
 * @Author: ${朱朝阳}
 * @Date: 2019/9/18 23:30
 */
@Component
public class SqlExecutor {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReflexValidateField reflexValidateField;

    private Class entityClass;

    private final String relationSymbol = "(query_|update_)";

    /**
     * 分割关联字
     */
    private final String relationRegex = "\\&" + relationSymbol + "|\\|" + relationSymbol;

    /**
     * 分割小条件
     */
    private final String queryRegex = "=|_";

    /**
     * Entity中声明的变量名称
     */
    private List<String> fieldsName;

    /**
     * 标志是否有该字段或操作符
     * 不能再builderSql方法中采用字符串，万一拼接好的SQL中也有那样的字段，则会报异常
     */
    private boolean hasField = true, hasOption = true, pageStart = true;

    /**
     * 全局分页信息
     */
    private PageRequest pageRequest;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 对关联分割
     *
     * @param queryConditions
     * @return 关联字链表 、 分割出条件字符串
     */
    public Object[] relationSplit(String queryConditions) {
        // _query _update
        List<String> symbolSegment = new ArrayList<>();
        // eq_name=z isNotNull_pwd
        List<String> querySegment;

        queryConditions = "|" + queryConditions;

        Pattern pattern = Pattern.compile(relationRegex);
        Matcher matcher = pattern.matcher(queryConditions);


        String[] split = pattern.split(queryConditions);

        if (matcher.find()) ;     // 去掉上面添加的|
        while (matcher.find()) {
            symbolSegment.add(String.valueOf(matcher.group(0).charAt(0)));
        }

        // 舍弃分割中出现的空数据 且 转List
        querySegment = Arrays.stream(split).skip(1L).collect(Collectors.toList());

        return new Object[]{symbolSegment, querySegment};
    }


    /**
     * 构建标准JPQL
     * 带分页
     * 不带分页
     *
     * @param symbolSegments 链接符号链表  And | Or
     * @param querySegments  条件链表
     * @param entityClass    Jpa Entity
     * @return
     */
    public Object builderSql(List<String> symbolSegments, List<String> querySegments, Class entityClass) {
        int index = 0;

        String currentCondition;

        String[] querySmall;

        boolean hasRelation = symbolSegments.size() == 0 ? false : true;

        // 如果有分页信息则拼接分页信息 如果没有分页信息则不拼分页
        StringBuffer standardSQLWhithPage = new StringBuffer();


        standardSQLWhithPage.append("select var from " + entityClass.getName() + " var where 1=1");
        // eq_name=aa --> eq name aa  isNotNull_name=aa    like_name=aa       条件_字段=数据
        for (String querySeg : querySegments) {
            // 去空格
            currentCondition = querySeg.replaceAll(" ", "");
            querySmall = currentCondition.split(queryRegex);

            if (querySmall.length < 2) {
                // 说明前端发送的条件碎片有不合格存在
                return "条件碎片拼接不合理";
            }

            Object tinyQuerySegment = handleTiny(querySmall);
            // 字段不匹配直接跳转走
            if (tinyQuerySegment instanceof Boolean) {
                if (!hasField) {
                    logger.error("---SqlExecutorNoJPQL:" + "字段：" + querySmall[1] + "在实体" + entityClass.getName() + "中未找到");
                    return "字段" + querySmall[1] + "在实体" + entityClass.getName() + "中未找到";
                }
                if (!hasOption) {
                    logger.error("---SqlExecutorNoJPQL:" + "操作符：" + querySmall[0] + "不合法");
                    return "操作符：" + querySmall[0] + "不合法";
                }
                if(!pageStart) {
                    logger.error("---SqlExecutorNoJPQL:分页信息不合法");
                    return "分页信息不合法";
                }
            } else if (tinyQuerySegment instanceof int[]) {
                // JPQL不支持分页 只能通过Query对象设置分页
                if (pageRequest == null) {
                    // 初始化
                    int[] pageInfoInt = (int[]) tinyQuerySegment;
                    pageRequest = new PageRequest(pageInfoInt[0], pageInfoInt[1]);
                }
            }

            // 以下三步拼接顺序不能乱
//            if (index == 0) {
//                standardSQLWhithPage.append(" and ");
//            }

            // 只有不是分页 且不是第一个index时候才需要拼接And
            if (!(tinyQuerySegment instanceof int[]) && index == 0) {
                standardSQLWhithPage.append(" and ");
                standardSQLWhithPage.append((String) tinyQuerySegment);
            }

            // 拼接连接符
            if (hasRelation && index < symbolSegments.size()) {
                String currentRelation = symbolSegments.get(index++);
                // OrderBy之前不需要添加连接符
                standardSQLWhithPage.append(" " + (currentRelation.equals("&") ? " and " : " or "));
            }
        }

        return new String[]{standardSQLWhithPage.toString()};
    }

    /**
     * 传入小条件
     *
     * @param querySmall Eg: [eq,name,aa] 或者 [isNotNull name] 拼接成SQL条件片段
     * @return
     */
    public Object handleTiny(String[] querySmall) {

        hasField = hasOption = true;

        String condition, field;
        StringBuffer stringBuffer = new StringBuffer();

        condition = querySmall[0];
        field = querySmall[1];

        // 分页中没有字段名称 为了统一请在前端条件的page后面同样拼接上_ 如：query_page_=1,10
        if (!StringUtils.isEmpty(field) && !fieldsName.contains(field)) {
            // 字段不存在
            hasField = false;
            return false;
        }

        switch (condition) {
            case "eq": {
                stringBuffer.append("var." + field + " = " + querySmall[2]);
                break;
            }
            case "lessThan": {
                stringBuffer.append("var." + field + " < " + querySmall[2]);
                break;
            }
            case "greaterThan": {
                stringBuffer.append("var." + field + " > " + querySmall[2]);
                break;
            }
            case "like": {
                stringBuffer.append("var." + field + " like " + querySmall[2]);
                break;
            }
            case "notLike": {
                stringBuffer.append("var." + field + " not like " + querySmall[2]);
                break;
            }
            case "in": {
                stringBuffer.append("var." + field + " in " + querySmall[2]);
                break;
            }
            case "notIn": {
                stringBuffer.append("var." + field + " not in " + querySmall[2]);
                break;
            }
            case "not": {
                stringBuffer.append("var." + field + " <> " + querySmall[2]);
                break;
            }
            case "between": {
                // TODO：  需要对日期做额外的处理  如判断是否为日期字符串 是否为同一天 是否为同一时刻
                stringBuffer.append("var." + field + " between " + querySmall[2]);
                break;
            }
            case "isNull": {
                stringBuffer.append("var." + field + " is null ");
                break;
            }
            case "isNotNull": {
                stringBuffer.append("var." + field + " is not null ");
                break;
            }
            case "notNull": {
                stringBuffer.append("var." + field + " is not null ");
                break;
            }
            case "orderBy": {
                stringBuffer.append("1=1 order by " + "var." + field + " " + querySmall[2]);
                break;
            }
            case "page": {
                String[] pageInfoStr = querySmall[2].split(",");
                // PageNo PageSize
                if(Integer.parseInt(pageInfoStr[0]) <= 0) {
                    pageStart = false;
                    return false;
                }
                return new int[]{Integer.parseInt(pageInfoStr[0]), Integer.parseInt(pageInfoStr[1])};
            }
            default: {
                // Condition操作符不存在
                hasOption = false;
                return false;
            }
        }

        return stringBuffer.toString();
    }

    /**
     * 调度方法拼接JPQL  并  执行
     * 目前只支持单表 查询
     *
     * @param queryConditions 前端拼接的字符串
     * @param entityName      实体Bean名称
     * @return
     */
    public Object builderExecutorSql(String queryConditions, String entityName) {

        try {
            entityClass = Class.forName(entityName);
        } catch (ClassNotFoundException e) {
            logger.error("---- SqlExecutorNoJPQL:" + e.getMessage());
            return e;
        }

        Field[] fields;

        // 条件不空将实体字段名塞入链表：fieldsName
        if (!StringUtils.isEmpty(queryConditions)) {
            // 条件不空 获取 反射类字段
            fieldsName = new ArrayList<>();
            fields = reflexValidateField.returnAllFiled(entityClass);
            Arrays.stream(fields).forEach(item -> {
                fieldsName.add(item.getName());
            });
        }

        Object[] condition = relationSplit(queryConditions);

        List<String> symbolSegment = (List<String>) condition[0];
        List<String> querySegment = (List<String>) condition[1];

        // 连接词应该比条件碎片少一个
        if (querySegment.size() != 0 && (querySegment.size() - symbolSegment.size()) != 1) {
            return "前端条件字符串拼接不合理";
        } else if (querySegment.size() == 0 && symbolSegment.size() != 0) {
            return "前端条件字符串拼接不合理";
        }

        Query dataFromDatabase, countQuery = null;

        Object standardSQL = builderSql(symbolSegment, querySegment, entityClass);

        // 如果是String[] 则返回的是标准SQL  否则只能说明字段不存在
        if (standardSQL instanceof String[]) {
            String[] sqls = (String[]) standardSQL;
            // 查询出结果集 不分页
            dataFromDatabase = entityManager.createQuery(sqls[0], entityClass);
            if (pageRequest != null) {
                // 需要分页
                dataFromDatabase.setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize());
                dataFromDatabase.setMaxResults(pageRequest.getPageSize());
                // 释放
                pageRequest = null;
            }
            return dataFromDatabase.getResultList();
        }


        // TODO:查询总数

        // 到这一步返回的只有错误信息  如 字段不存在
        return standardSQL;
    }


}

