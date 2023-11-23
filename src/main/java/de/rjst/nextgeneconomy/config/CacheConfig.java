package de.rjst.nextgeneconomy.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import de.rjst.nextgeneconomy.logic.config.PropertySupplier;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig {

    private final PropertySupplier propertySupplier;

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        final Integer balanceTopRefresh = propertySupplier.apply(NgeSetting.BALANCE_TOP_REFRESH, Integer.class);
        return Caffeine.newBuilder()
                .expireAfterAccess(balanceTopRefresh, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(final Caffeine<Object, Object> caffeineConfig) {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeineConfig);
        return caffeineCacheManager;
    }

}
