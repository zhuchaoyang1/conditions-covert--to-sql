#TransformationSQL

###（一）框架支持：
* 针对两种前端请求方式： GET / POST
* 针对日期问题处理
* 支持分页
* 对FindBy语法条件的全部支持，说明如下：
![avator](./2018081915450258.bmp)  


###（二）前端发送伪SQL条件规则说明：  
对于上述图片的条件支持：  
以下为条件片段的连接词：
* And
* Or

以下需要有Key -- Value同时存在：
* LessThan
* GreaterThan
* Like
* NotLike
* In
* NotIn
* Not

以下需要有Key -对应- 两个Value存在：
* between


以下只需要Key
* IsNull
* IsNotNull,NotNull
* OrderBy

框架对上述扩展分页和相等条件 （FindBy注重的是条件 相等条件已经在其内部拼接好）
* Page  Eg:x,y x->pageNo,y->pageSize  pageNo从1开始
* eq 

###（三）暂不支持多表联合查询

###（四）业务术语
* 条件碎片：  
```java
   * eq_name='xxx'
   * isNotNull_name
   * like_name='xxx'
   条件碎片构成：Opt：操作符           （必须）
                Field：字段名称       （必须）
                Value：字段数据       （不是必须）
```

###（五）如何使用  
1、Between使用：
```java
    // 对比方式1、2说明：在时间上是否加’号都可以
    方式1：query_between_startTime=2019-09-19 08:48:37 and 2019-09-27 08:48:41
    方式2：query_between_startTime='2019-09-19 08:48:37' and '2019-09-27 08:48:41'
    
    // 如果在Date上没有写xx:xx:xx时间的话，默认包括开始时间和截至时间这两天，即下面的写法包括2019-09-19全天和2019-09-27全天
    方式3：query_between_startTime=2019-09-19 and 2019-09-27
    
    // 更多用法请看：./src/test/java/com/demo/IntegrateTest.class
```

###（六）注意问题
    同标准SQL一样，上面的OrderBy碎片和Page碎片只能拼接在最后否则会报错：“请正确拼接SQL碎片”，而OrderBy和Page这两个碎片无先后顺序。