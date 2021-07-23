package com.example.mybatisplus;

import cn.hutool.core.util.StrUtil;
import com.github.davidfantasy.mybatisplus.generatorui.GeneratorConfig;
import com.github.davidfantasy.mybatisplus.generatorui.MybatisPlusToolsApplication;
import com.github.davidfantasy.mybatisplus.generatorui.mbp.NameConverter;
import com.google.common.base.Strings;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/7/14
 */
@SpringBootTest
public class GeberatorUIServer {
    private static final String POJO_NAME = "Pojo";

    public static void main(String[] args) {
        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:mysql://localhost:3306/zero-staff?userSSL=false&serverTimezone=Asia/Shanghai")
                .userName("root")
                .password("Gzzx@5500955")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                //数据库schema，POSTGRE_SQL,ORACLE,DB2类型的数据库需要指定
                .schemaName("zero-staff")
                //如果需要修改各类生成文件的默认命名规则，可自定义一个NameConverter实例，覆盖相应的名称转换方法：
                .nameConverter(new NameConverter() {
                    /**
                     * 自定义Service类文件的名称规则
                     */
                    @Override
                    public String serviceNameConvert(String tableName) {
                        return this.entityNameConvert(tableName).replace(POJO_NAME, "") + "Service";
                    }

                    /**
                     * 自定义Controller类文件的名称规则
                     */
                    @Override
                    public String controllerNameConvert(String tableName) {
                        return this.entityNameConvert(tableName).replace(POJO_NAME, "") + "Controller";
                    }

                    @Override
                    public String mapperNameConvert(String tableName) {
                        return this.entityNameConvert(tableName).replace(POJO_NAME, "") +"Mapper";
                    }

                    @Override
                    public String serviceImplNameConvert(String tableName) {
                        return this.serviceNameConvert(tableName)+"Impl";
                    }

                    @Override
                    public String mapperXmlNameConvert(String tableName) {
                        return mapperNameConvert(tableName);
                    }

                    @Override
                    public String entityNameConvert(String tableName) {
                        if (Strings.isNullOrEmpty(tableName)) {
                            return "";
                        } else {
                            tableName = tableName.substring(tableName.indexOf("_") + 1, tableName.length());
                            return StrUtil.upperFirst(StrUtil.toCamelCase(tableName.toLowerCase())) + POJO_NAME;
                        }
                    }
                })
                .basePackage("com.example.mybatisplus")
                .port(8068)
                .build();
        MybatisPlusToolsApplication.run(config);
    }

}
