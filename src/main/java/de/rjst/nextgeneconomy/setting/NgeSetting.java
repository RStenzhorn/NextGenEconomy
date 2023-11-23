package de.rjst.nextgeneconomy.setting;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NgeSetting {

    DB_DRIVER_CLASS("database.driver-class-name","org.mariadb.jdbc.Driver"),
    DB_URL("database.url","jdbc:mariadb://ip:port/dbname"),
    DB_USER("database.username","root"),
    DB_PASSWORD("database.password","passwort"),
    DB_SHOW_SQL("database.show-sql","false"),
    DB_HIKARI_AUTO_COMMIT("database.hikari.autoCommit","true"),
    DB_HIKARI_CONNECTION_TIMEOUT("database.hikari.connectionTimeout","30000"),
    DB_HIKARI_IDLE_TIMEOUT("database.hikari.idleTimeout","600000"),
    DB_HIKARI_KEEPALIVE_TIME("database.hikari.keepAliveTime","0"),
    DB_HIKARI_MAX_LIFETIME("database.hikari.maxLifetime","1800000"),
    DB_HIKARI_MINIMUM_IDLE("database.hikari.minimumIdle","1"),
    DB_HIKARI_MAXIMUM_POOL_SIZE("database.hikari.maximumPoolSize","10"),
    PREFIX("prefix", "[NextGenEconomy]"),
    BALANCE_TOP_PAGE_SIZE("balanceTop.pageSize", "10"),
    CURRENCY_SYMBOL("currency.symbol","€"),
    CURRENCY_SINGULAR("currency.singular", "Euro"),
    CURRENCY_PLURAL("currency.plural", "Euros"),
    CURRENCY_LOCALE_FORMAT("currency.localeFormat", "de_DE"),
    CURRENCY_FRACTION_DIGITS("currency.fractionDigits", "2"),
    CURRENCY_FORMAT("currency.format", "%LOCALE_FORMAT% %CURRENCY_NAME% %CURRENCY_SYMBOL%");

    private final String path;
    private final Object defaultValue;

}
