package de.rjst.nextgeneconomy.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.rjst.nextgeneconomy.logic.config.PropertySupplier;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableJpaRepositories(basePackages = "de.rjst.nextgeneconomy.database.repository")
@EnableTransactionManagement
public class DatabaseConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource, final @NotNull PropertySupplier propertySupplier){
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("de.rjst.nextgeneconomy.database.*");
        factoryBean.setPersistenceUnitName("Eco-DB");

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", propertySupplier.apply(NgeSetting.DB_SHOW_SQL, String.class));
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }

    @Bean
    public HikariConfig hikariConfig(@NotNull final PropertySupplier propertySupplier) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(propertySupplier.apply(NgeSetting.DB_URL, String.class));
        config.setDriverClassName(propertySupplier.apply(NgeSetting.DB_DRIVER_CLASS, String.class));
        config.setUsername(propertySupplier.apply(NgeSetting.DB_USER, String.class));
        config.setPassword(propertySupplier.apply(NgeSetting.DB_PASSWORD, String.class));
        return config;
    }

    @Bean
    public DataSource dataSource(final HikariConfig hikariConfig){
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory){
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
