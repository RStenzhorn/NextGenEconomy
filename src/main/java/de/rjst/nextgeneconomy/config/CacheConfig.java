package de.rjst.nextgeneconomy.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import de.rjst.nextgeneconomy.logic.config.PropertySupplier;
import de.rjst.nextgeneconomy.setting.NgeSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig {

    private final PropertySupplier propertySupplier;

    @Bean
    public Cache balanceTopCache() {
        final Integer balanceTopRefresh = propertySupplier.apply(NgeSetting.BALANCE_TOP_REFRESH, Integer.class);
        return new CaffeineCache("balanceTop",
                Caffeine.newBuilder()
                        .expireAfterAccess(balanceTopRefresh, TimeUnit.MINUTES)
                        .build());
    }


    @Bean
    public Cache balanceCache() {
        final Integer balanceTopRefresh = propertySupplier.apply(NgeSetting.BALANCE_REFRESH, Integer.class);
        return new CaffeineCache("balance",
                Caffeine.newBuilder()
                        .expireAfterAccess(balanceTopRefresh, TimeUnit.MINUTES)
                        .build());
    }

    @Bean
    public CacheManager cacheManager(final Cache balanceTopCache, final Cache balanceCache) {
        final SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(List.of(balanceTopCache, balanceCache));
        return simpleCacheManager;
    }

}
