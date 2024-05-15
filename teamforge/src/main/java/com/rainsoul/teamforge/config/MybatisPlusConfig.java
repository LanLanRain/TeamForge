package com.rainsoul.teamforge.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus 配置类，用于配置MyBatis Plus的相关设置。
 */
@Configuration
@MapperScan("com.rainsoul.teamforge.mapper") // 扫描指定包下的Mapper接口
public class MybatisPlusConfig {

    /**
     * 配置Mybatis Plus的分页插件。
     * <p>
     * 新的分页插件配置，针对MySQL数据库。为了避免分页插件与MyBatis的缓存问题，
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor = false。
     * 这个设置会防止使用已被弃用的执行器，避免潜在的缓存问题。
     * </p>
     *
     * @return 返回配置好的MybatisPlusInterceptor分页拦截器实例。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 添加分页插件
        return interceptor;
    }
}
