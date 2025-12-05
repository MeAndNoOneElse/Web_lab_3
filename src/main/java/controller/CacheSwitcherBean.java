package controller;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import service.CacheType;
import service.CacheController;

@Named("cacheSwitcher")
@SessionScoped
public class CacheSwitcherBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private CacheController cacheController;

    public CacheType getCurrent() {
        return cacheController.getCacheType();
    }

    public CacheType getCurrentCache() {
        return cacheController.getCacheType();
    }

    public CacheType[] getAvailableCaches() {
        return CacheType.values();
    }

    public void setCache(CacheType type) {
        if (type == null) return;
        cacheController.setCacheType(type);
    }

    public void setCaffeine() {
        setCache(CacheType.CAFFEINE);
    }

    public void setRedis() {
        setCache(CacheType.REDIS);
    }
    public String getCurrentCacheLabel() {
        return getCurrentCache() != null ? getCurrentCache().name() : "UNKNOWN";
    }
}
