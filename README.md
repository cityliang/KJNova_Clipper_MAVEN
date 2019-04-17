# KJNova_Clipper_MAVEN
图片裁剪 从mysql或Oracle数据库中把图片读取到本地，然后裁剪图片并保存到数据库

该项目是一个MAVEN项目，  没有其他依赖  
使用jar包为 C3P0的JDBC连接池 还有常用的mysql和Oracle的jar包

### 使用jar包
```
c3p0-0.9.1.2.jar  
mchange-commons-java-0.2.15.jar  
mysql-connector-java-5.1.32.jar  
ojdbc14-10.2.0.4.0.jar  
thumbnailator-0.4.8.jar  
```

### 使用配置  
在 c3p0-config.xml 中配置数据库信息之后进行 jdbc 操作数据库即可，该项目可以导出 10 万以上的图片，不会因为数据量太大导致内存溢出
