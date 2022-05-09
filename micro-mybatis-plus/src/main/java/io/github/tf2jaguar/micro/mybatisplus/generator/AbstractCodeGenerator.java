package io.github.tf2jaguar.micro.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author 张豪
 * @since 2021/7/10 11:57
 */
public abstract class AbstractCodeGenerator {

    /**
     * 读取控制台输入内容
     */
    private final Scanner scanner = new Scanner(System.in);
    /**
     * 生成路径
     */
    private final String outputDir = System.getProperty("user.dir") + "/generator";
    /**
     * 数据库链接
     */
    protected String dataSourceUrl;
    /**
     * 数据库用户名
     */
    protected String dataSourceUsername;
    /**
     * 数据库密码
     */
    protected String dataSourcePassword;
    /**
     * 公共包名
     */
    protected String packageName;
    /**
     * 表名前缀
     */
    protected String tablePrefix;
    /**
     * 包含表名(多个以空格隔开, 忽略则生成所有表)
     */
    protected String tableInclude;
    /**
     * 排除表名(多个以空格隔开, 忽略则生成所有表)
     */
    protected String tableExclude;

    private void clean(String outputDir) {
        File file = new File(outputDir);
        try {
            FileUtils.cleanDirectory(file);
            FileUtils.forceMkdir(file);
        } catch (IOException e) {
            throw new RuntimeException(outputDir + "清空失败");
        }

    }

    protected void generate() {
        System.out.println("======== 开始执行代码生成 ========");
        clean(outputDir);

        // 代码生成器
        AutoGenerator generator = new AutoGenerator(dataSourceConfig());

        // 全局配置
        generator.global(globalConfig());

        // 包配置
        generator.packageInfo(packageConfig());

        // 配置模板
        generator.template(templateConfig());

        // 策略配置
        generator.strategy(strategyConfig());

        // 自定义配置
        generator.injection(injectionConfig());

        // 生成
        generator.execute();

        System.out.println("======== 代码生成完成, 生成路径: " + outputDir + " ========");
    }

    protected DataSourceConfig dataSourceConfig() {
        return new DataSourceConfig
                .Builder(getRequired(dataSourceUrl, "数据库链接"),
                getRequired(dataSourceUsername, "数据库用户名"),
                getRequired(dataSourcePassword, "数据库密码"))
                .build();
    }

    protected GlobalConfig globalConfig() {
        return new GlobalConfig
                .Builder()
                .author("作者")
                .fileOverride()
                .outputDir(outputDir)
                .openDir(false)
                .build();
    }

    protected PackageConfig packageConfig() {
        Map<String, String> pathInfo = new HashMap<>();
        pathInfo.put(ConstVal.ENTITY_PATH, outputDir + "/model");
        pathInfo.put(ConstVal.MAPPER_PATH, outputDir + "/dao");
        pathInfo.put(ConstVal.XML_PATH, outputDir + "/mapper");
        pathInfo.put(ConstVal.SERVICE_PATH, outputDir + "/api");
        pathInfo.put(ConstVal.SERVICE_IMPL_PATH, outputDir + "/service");
        pathInfo.put(ConstVal.CONTROLLER_PATH, outputDir + "/controller");

        return new PackageConfig
                .Builder()
                .parent(getRequired(packageName, "公共包名"))
                // 因为pathInfo会覆盖.entity().mapper()等配置,所以这里不需要再指定
                .entity("model")
                .mapper("dao")
                .service("api")
                .serviceImpl("service")
                .pathInfo(pathInfo)
                .build();
    }

    protected TemplateConfig templateConfig() {
        return new TemplateConfig
                .Builder()
                .build();
    }

    protected StrategyConfig strategyConfig() {
        StrategyConfig.Builder builder = new StrategyConfig.Builder();
        String input = get(tablePrefix, "表名前缀(多个以空格隔开)");
        if (StringUtils.isNotBlank(input)) {
            builder.addTablePrefix(input.split(" "));
        }
        input = get(tableInclude, "包含表名(多个以空格隔开, 忽略则生成所有表)");
        if (StringUtils.isNotBlank(input)) {
            builder.addInclude(input.split(" "));
        } else {
            input = get(tableExclude, "排除表名(多个以空格隔开, 忽略则生成所有表)");
            if (StringUtils.isNotBlank(input)) {
                builder.addExclude(input.split(" "));
            }
        }

        builder.entityBuilder()
                .enableSerialVersionUID()
                .enableLombok()
                .enableTableFieldAnnotation()
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .addTableFills(new Column("create_time", FieldFill.INSERT), new Column("update_time", FieldFill.INSERT_UPDATE))
                .logicDeleteColumnName("is_deleted")
                .logicDeletePropertyName("isDeleted");

        builder.mapperBuilder()
                .convertMapperFileName(name -> name + "Dao");

        builder.serviceBuilder()
                .convertServiceFileName(name -> name + "Service");

        builder.controllerBuilder()
                .enableHyphenStyle()
                .enableRestStyle();

        return builder.build();
    }

    protected InjectionConfig injectionConfig() {
        return new InjectionConfig
                .Builder()
                .build();
    }

    private String get(String property, String message) {
        if (property == null) {
            return nextInput(message);
        }
        return property;
    }

    private String getRequired(String property, String message) {
        if (StringUtils.isBlank(property)) {
            return nextInputRequired(message);
        }
        return property;
    }

    /**
     * 控制台输入内容读取并打印提示信息
     *
     * @param message 提示信息
     * @return string 用户输入
     */
    protected String nextInputRequired(String message) {
        return nextInput(message, true);
    }

    /**
     * 控制台输入内容读取并打印提示信息
     *
     * @param message 提示信息
     * @return string 用户输入, 可为空
     */
    protected String nextInput(String message) {
        return nextInput(message, false);
    }

    /**
     * 控制台输入内容读取并打印提示信息
     *
     * @param message  提示信息
     * @param required 是否必须输入文字
     * @return string 用户输入
     */
    protected String nextInput(String message, boolean required) {
        System.out.println("请输入" + message + ":");
        String nextLine = scanner.nextLine();
        if (required) {
            if (StringUtils.isBlank(nextLine)) {
                return nextInput(message, required);
            }
            return nextLine;
        }
        return nextLine;
    }

}
