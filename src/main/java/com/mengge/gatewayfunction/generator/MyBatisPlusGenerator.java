package com.mengge.gatewayfunction.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.sql.SQLException;


public class MyBatisPlusGenerator {

    public static void main(String[] args) throws SQLException {

        // 1. 全局配置
        GlobalConfig config = new GlobalConfig();
        // 是否支持AR模式
        config.setActiveRecord(true)
                // 作者
                .setAuthor("zxw")
                // 生成路径
                .setOutputDir(System.getProperty("user.dir") + "\\src\\main\\java")
                // 文件覆盖
                .setFileOverride(false)
                // 主键策略
                .setIdType(IdType.AUTO)
                // 设置生成的service接口的名字的首字母是否为IEmployeeService
                .setServiceName("%sService")
                //生成时间Date
                .setDateType(DateType.ONLY_DATE);
        // 生成基本的resultMap
        //   .setBaseResultMap(true)
        // 生成基本的SQL片段
        //    .setBaseColumnList(true);

        // 2. 数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig();
        // 设置数据库类型
        dsConfig.setDbType(DbType.MYSQL)

                .setDriverName("com.mysql.cj.jdbc.Driver")

                .setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8&characterEncoding=utf-8&useSSL=false&useUnicode=true")

                .setUsername("root")

                .setPassword("123456");

        // 3. 策略配置globalConfiguration中
        StrategyConfig stConfig = new StrategyConfig();
        // 全局大写命名
        stConfig.setCapitalMode(true)
                // 数据库表映射到实体的命名策略
                .setNaming(NamingStrategy.underline_to_camel)
                // 生成lombok
                .setEntityLombokModel(true)
                //生成RestController
                .setRestControllerStyle(true)
                // 生成的表
                .setInclude("query_data");

        // 4. 包名策略配置
        PackageConfig pkConfig = new PackageConfig();

        pkConfig.setParent("com.mengge.getwayfunction")
                // dao
                .setMapper("mapper")
                // service
                .setService("service")
                // controller
                .setController("controller")
                // pojo
                .setEntity("entity");

        // 5. 整合配置
        AutoGenerator ag = new AutoGenerator();

        ag.setGlobalConfig(config)

                .setDataSource(dsConfig)

                .setStrategy(stConfig)

                .setPackageInfo(pkConfig);

        // 6. 执行
        ag.execute();

    }

}
