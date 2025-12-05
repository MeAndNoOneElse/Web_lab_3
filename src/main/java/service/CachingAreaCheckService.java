package service;

import dto.PointDTO;
import dto.ResultDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import service.cache.CacheProvider;
import service.cache.CaffeineCacheProvider;
import service.cache.RedisCacheProvider;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CachingAreaCheckService implements AreaCheckServiceInterface, Serializable {

    private static final Logger LOGGER = Logger.getLogger(CachingAreaCheckService.class.getName());

    @Inject
    @PlainService
    private AreaCheckServiceInterface baseService; // изменено на интерфейс

    @Inject
    private CaffeineCacheProvider caffeineProvider;

    @Inject
    private RedisCacheProvider redisProvider;

    // активный провайдер (устанавливается извне)
    private volatile CacheProvider activeProvider;

    public void setActiveProvider(CacheProvider provider) {
        this.activeProvider = provider;
        LOGGER.fine(() -> "[CACHE] active provider set to " + (provider != null ? provider.getClass().getSimpleName() : "null"));
    }

    @Override
    public ResultDTO checkAndSave(PointDTO point) {
        LOGGER.fine("[CACHE] checkAndSave() — delegating to baseService and will invalidate cache");
        if (baseService == null) {
            LOGGER.severe("[CACHE] baseService is not available in checkAndSave()");
            throw new IllegalStateException("baseService not available");
        }
        ResultDTO res = baseService.checkAndSave(point);
        // при изменении данных инвалидируем кэш (можно оптимизировать обновлением)
        CacheProvider p = this.activeProvider;
        if (p != null) {
            String name = p.getClass().getSimpleName();
            LOGGER.info(() -> "[CACHE] invalidating active cache provider: " + name + " (due to data change)");
            p.invalidate();
        } else {
            LOGGER.fine("[CACHE] no active provider to invalidate after save");
        }
        return res;
    }

    @Override
    public List<ResultDTO> getAllResults() {
        if (baseService == null) {
            LOGGER.warning("[CACHE] baseService is null — returning empty list to avoid NPE during init");
            return Collections.emptyList();
        }
        CacheProvider p = this.activeProvider;
        if (p == null) {
            // нет провайдера кэша — сразу в базу
            LOGGER.fine("[CACHE] activeProvider == null -> delegating to baseService.getAllResults()");
            try {
                List<ResultDTO> fromDb = baseService.getAllResults();
                LOGGER.info(() -> String.format("[SRC] Served %d rows from DB (no cache active)", fromDb.size()));
                return fromDb;
            } catch (Exception e) {
                LOGGER.warning("[CACHE] baseService.getAllResults() failed: " + e);
                return Collections.emptyList();
            }
        }
        List<ResultDTO> cached = p.get();
        if (cached != null && p.isFresh()) {
            LOGGER.info(() -> String.format("[CACHE] HIT (%s) — serving %d rows from cache", p.getClass().getSimpleName(), cached.size()));
            return cached;
        }
        // промах кэша — логируем и загружаем из базы, затем обновляем кэш
        String providerName = (p != null) ? p.getClass().getSimpleName() : "null";
        LOGGER.info(() -> "[CACHE] MISS (" + providerName + ") — loading from DB via baseService");
        List<ResultDTO> loaded;
        try {
            loaded = baseService.getAllResults();
        } catch (Exception e) {
            LOGGER.warning("[CACHE] baseService.getAllResults() failed during cache MISS: " + e);
            loaded = Collections.emptyList();
        }
        final int rows = (loaded != null) ? loaded.size() : 0;
        LOGGER.info(() -> String.format("[CACHE] updating cache (%s) with %d rows (source=DB)", providerName, rows));
        p.put(loaded);
        return loaded;
    }

    @Override
    public void deleteAllResults() {
        LOGGER.info("[CACHE] deleteAllResults() — delegating to baseService and invalidating all caches");
        // удаляем в БД
        if (baseService != null) {
            try {
                baseService.deleteAllResults();
            } catch (Exception e) {
                LOGGER.warning("[CACHE] baseService.deleteAllResults() failed: " + e);
            }
        } else {
            LOGGER.warning("[CACHE] baseService is null in deleteAllResults()");
        }
        // инвалидация всех доступных провайдеров кэша (чтобы история ушла везде)
        if (caffeineProvider != null) {
            LOGGER.fine("[CACHE] invalidating caffeineProvider");
            caffeineProvider.invalidate();
        }
        if (redisProvider != null) {
            LOGGER.fine("[CACHE] invalidating redisProvider");
            redisProvider.invalidate();
        }
        // дополнительно инвалидация активного провайдера (на случай, если он другой)
        CacheProvider p = this.activeProvider;
        if (p != null) {
            LOGGER.fine(() -> "[CACHE] invalidating active provider: " + p.getClass().getSimpleName());
            p.invalidate();
        }
    }
}
