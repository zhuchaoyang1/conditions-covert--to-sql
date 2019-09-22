package com.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private DateUtil dateUtil;

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

        // 去掉上面添加的|
        if (matcher.find()) ;
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

        String[] querySmall;

        boolean hasRelation = symbolSegments.size() == 0 ? false : true;

        StringBuffer standardSQLWhithPage = new StringBuffer();


        standardSQLWhithPage.append("select var from " + entityClass.getName() + " var where 1=1");

        String currentRelation = null;

        if (querySegments.size() > 0) {
            standardSQLWhithPage.append(" and ");
        }

        // eq_name=aa --> eq name aa  isNotNull_name=aa    like_name=aa       条件_字段=数据
        for (String querySeg : querySegments) {

            // 拼接连接符
            if (hasRelation && index < symbolSegments.size()) {
                currentRelation = symbolSegments.get(index++);
            }

            querySmall = querySeg.split(queryRegex);

            if (querySmall.length < 2) {
                // 说明前端发送的条件碎片有不合格存在
                return "条件碎片拼接不合理";
            }

            Object tinyQuerySegment = handleTiny(currentRelation, querySmall);
            currentRelation = null;
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
                if (!pageStart) {
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


            // 分页信息不拼接条件碎片
            if (!(tinyQuerySegment instanceof int[])) {
                standardSQLWhithPage.append((String) tinyQuerySegment);
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
    public Object handleTiny(String currentRelation, String[] querySmall) {

        hasField = hasOption = true;

        String condition, field;
        StringBuffer stringBuffer = new StringBuffer();

        condition = querySmall[0].replaceAll(" ", "");
        field = querySmall[1].replaceAll(" ", "");

        // 分页中没有字段名称 为了统一请在前端条件的page后面同样拼接上_ 如：query_page_=1,10
        if (!StringUtils.isEmpty(field) && !fieldsName.contains(field)) {
            // 字段不存在
            hasField = false;
            return false;
        }

        currentRelation = StringUtils.isEmpty(currentRelation) ? "" : (currentRelation.equals("&") ? " and " : " or ");

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
                // '2019-0 9-19 08:4 8:37' and '2019- 09-20 08:4  8:41'
                String date = querySmall[2];

                String[] timeSplit = date.split("and");
                // timeSplit-->['2019-0 9-19 08:48:37' , '2019-09-20 08:48:41']
                Object time1Content = dateUtil.handleDateStr(timeSplit[0], true);
                Object time2Content = dateUtil.handleDateStr(timeSplit[1], false);

                if (time1Content instanceof String && time2Content instanceof String) {
                    date = "'" + time1Content + "' and '" + time2Content + "'";
                }

                stringBuffer.append("var." + field + " between " + date);
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
                if (Integer.parseInt(pageInfoStr[0]) <= 0) {
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

        if (!condition.equals("page")) {
            stringBuffer.append(currentRelation);
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
    public Object builderExecutorSql(String queryConditions, String entityName, boolean... isNeedCount) {

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

        // 如果是String[] 则返回的是标准SQL  否则只能说明字段或操作符不存在
        if (standardSQL instanceof String[]) {
            String sqls = ((String[]) standardSQL)[0].trim();
            sqls = sqls.endsWith("and") ? sqls.substring(0, sqls.length() - 3) : sqls;
            try {
                // 查询出结果集 不分页
                dataFromDatabase = entityManager.createQuery(sqls, entityClass);
                if (pageRequest != null) {
                    // 需要分页
                    dataFromDatabase.setFirstResult((pageRequest.getPageNumber() - 1) * pageRequest.getPageSize());
                    dataFromDatabase.setMaxResults(pageRequest.getPageSize());
                    // 释放
                    pageRequest = null;
                }

                long count = 0L;
                if (isNeedCount.length > 0 && isNeedCount[0]) {
                    countQuery = entityManager.createQuery(sqls, entityClass);
                    count = countQuery.getResultList().size();
                    return new PageImpl(dataFromDatabase.getResultList(), Pageable.unpaged(), count);
                } else {
                    return dataFromDatabase.getResultList();
                }
            }catch (Exception e) {
                logger.error("---SqlExecutor："+e.toString());
                standardSQL = "请正确拼接SQL碎片";
            }
        }

        // 到这一步返回的只有错误信息  如 字段不存在
        return standardSQL;
    }


}

