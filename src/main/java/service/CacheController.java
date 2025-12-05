package service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import service.cache.CaffeineCacheProvider;
import service.cache.RedisCacheProvider;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class CacheController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CacheController.class.getName());

    @Inject
    @PlainService
    private AreaCheckServiceInterface baseService; // plain implementation без кеша (инжектим интерфейс с qualifier)

    @Inject
    private CachingAreaCheckService cachingService;

    @Inject
    private CaffeineCacheProvider caffeineProvider;

    @Inject
    private RedisCacheProvider redisProvider;

    // По умолчанию используем CAFFEINE (NONE убран)
    private volatile CacheType current = CacheType.CAFFEINE;

    public AreaCheckServiceInterface getService() {
        try {
            // Защита: если кэш-объекты ещё не инжектированы, возвращаем базовую реализацию
            if (cachingService == null || caffeineProvider == null || redisProvider == null) {
                LOGGER.warning("[CACHECTRL] cachingService or providers not available yet -> fallback to baseService");
                return baseService;
            }
            if (current == CacheType.CAFFEINE) {
                LOGGER.fine("[CACHECTRL] getService() -> CAFFEINE selected");
                cachingService.setActiveProvider(caffeineProvider);
                return cachingService;
            } else {
                LOGGER.fine("[CACHECTRL] getService() -> REDIS selected");
                cachingService.setActiveProvider(redisProvider);
                return cachingService;
            }
        } catch (Exception e) {
            LOGGER.severe("[CACHECTRL] getService() failed, falling back to baseService: " + e);
            return baseService;
        }
    }

    public void setCacheType(CacheType type) {
        CacheType old = this.current;
        this.current = (type == null) ? CacheType.CAFFEINE : type;
        LOGGER.info(() -> String.format("[CACHECTRL] setCacheType() changed from %s to %s", old, this.current));
        // при смене режима инвалидация соответствующего провайдера — чтобы при следующем запросе загрузились свежие данные
        try {
            if (this.current == CacheType.CAFFEINE) {
                if (caffeineProvider != null) {
                    caffeineProvider.invalidate();
                    LOGGER.fine("[CACHECTRL] caffeineProvider.invalidate()");
                }
            } else if (this.current == CacheType.REDIS) {
                if (redisProvider != null) {
                    redisProvider.invalidate();
                    LOGGER.fine("[CACHECTRL] redisProvider.invalidate()");
                }
            }
        } catch (Exception e) {
            LOGGER.warning("[CACHECTRL] Error while invalidating provider on setCacheType: " + e);
        }
    }

    // Добавленный метод: инвалидация всех провайдеров кэша (не затрагивает БД)
    public void invalidateAllCaches() {
        LOGGER.info("[CACHECTRL] invalidateAllCaches() called");
        if (caffeineProvider != null) caffeineProvider.invalidate();
        if (redisProvider != null) redisProvider.invalidate();
    }

    // Удобный метод: удалить все данные в БД и очистить все кэши
    public void deleteAllEverything() {
        LOGGER.info("[CACHECTRL] deleteAllEverything() called — will delete DB and invalidate all caches");
        // удаляем в БД (через базовую реализацию)
        if (baseService != null) {
            try {
                baseService.deleteAllResults();
            } catch (Exception e) {
                LOGGER.warning("[CACHECTRL] error deleting all results via baseService: " + e);
            }
        } else {
            LOGGER.warning("[CACHECTRL] baseService is null when trying to deleteAllEverything()");
        }
        // затем инвалидация всех кэшей
        invalidateAllCaches();
        LOGGER.fine("[CACHECTRL] deleteAllEverything() completed");
    }

    public CacheType getCacheType() {
        return current;
    }
}
