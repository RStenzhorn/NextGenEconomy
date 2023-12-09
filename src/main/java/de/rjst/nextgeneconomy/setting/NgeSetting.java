package de.rjst.nextgeneconomy.setting;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NgeSetting {

    DB_DRIVER_CLASS("spring.datasource.driver-class-name","org.mariadb.jdbc.Driver"),
    DB_URL("spring.datasource.url","jdbc:mariadb://ip:port/databaseName"),
    DB_USER("spring.datasource.username","root"),
    DB_PASSWORD("spring.datasource.password","password"),
    DB_DDL_AUTO("spring.jpa.hibernate.ddl-auto","update"),
    DB_SHOW_SQL("spring.jpa.show-sql","false"),
    DB_HIKARI_AUTO_COMMIT("spring.datasource.hikari.autoCommit","true"),
    DB_HIKARI_CONNECTION_TIMEOUT("spring.datasource.hikari.connectionTimeout","30000"),
    DB_HIKARI_IDLE_TIMEOUT("spring.datasource.hikari.idleTimeout","600000"),
    DB_HIKARI_KEEPALIVE_TIME("spring.datasource.hikari.keepAliveTime","0"),
    DB_HIKARI_MAX_LIFETIME("spring.datasource.hikari.maxLifetime","1800000"),
    DB_HIKARI_MINIMUM_IDLE("spring.datasource.hikari.minimumIdle","10"),
    DB_HIKARI_MAXIMUM_POOL_SIZE("spring.datasource.hikari.maximumPoolSize","10"),
    PREFIX("prefix", "[NextGenEconomy]"),
    START_BALANCE("start.balance", "0"),
    BALANCE_TOP_PAGE_SIZE("balanceTop.pageSize", "10"),
    BALANCE_TOP_REFRESH("balanceTop.refresh", "15"),
    BALANCE_REFRESH("balance.refresh", "60"),
    CURRENCY_SYMBOL("currency.symbol","€"),
    CURRENCY_SINGULAR("currency.singular", "Euro"),
    CURRENCY_PLURAL("currency.plural", "Euros"),
    CURRENCY_LOCALE_FORMAT("currency.localeFormat", "de_DE"),
    CURRENCY_FRACTION_DIGITS("currency.fractionDigits", "2"),
    CURRENCY_FORMAT("currency.format", "%LOCALE_FORMAT% %CURRENCY_NAME% %CURRENCY_SYMBOL%");

    private final String path;
    private final Object defaultValue;

}
