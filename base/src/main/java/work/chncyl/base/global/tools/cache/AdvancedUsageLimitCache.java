package work.chncyl.base.global.tools.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 支持过期时间和使用次数限制的缓存
 */
public class AdvancedUsageLimitCache implements CacheInterface {
    
    private static class CacheEntry {
        private final Object value;
        private final AtomicInteger remainingUsage;
        private final long expireTime;
        
        public CacheEntry(Object value, int maxUsage, long ttlMillis) {
            this.value = value;
            this.remainingUsage = new AtomicInteger(maxUsage);
            this.expireTime = System.currentTimeMillis() + ttlMillis;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
        
        public boolean hasRemainingUsage() {
            return remainingUsage.get() > 0;
        }
    }
    
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    public void put(String key, Object value, int maxUsage, long ttlMillis) {
        if (maxUsage <= 0) {
            throw new IllegalArgumentException("使用次数必须大于0");
        }
        cache.put(key, new CacheEntry(value, maxUsage, ttlMillis));
    }
    
    public Object getAndDecrement(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        while (true) {
            int current = entry.remainingUsage.get();
            if (current <= 0) {
                cache.remove(key);
                return null;
            }
            
            if (entry.remainingUsage.compareAndSet(current, current - 1)) {
                if (current - 1 == 0) {
                    cache.remove(key);
                }
                return entry.value;
            }
        }
    }
    
    @Override
    public void put(String key, Object value) {
        put(key, value, 1, Long.MAX_VALUE);
    }
    
    @Override
    public Integer putIfAbsent(String key, Object value) {
        CacheEntry newEntry = new CacheEntry(value, 1, Long.MAX_VALUE);
        CacheEntry existing = cache.putIfAbsent(key, newEntry);
        return existing == null ? 1 : null;
    }
    
    @Override
    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }
    
    @Override
    public Object get(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public Object remove(String key) {
        CacheEntry entry = cache.remove(key);
        return entry != null ? entry.value : null;
    }
    
    @Override
    public void clear() {
        cache.clear();
    }
    
    @Override
    public int size() {
        cleanupExpired();
        return cache.size();
    }
    
    @Override
    public boolean containsKey(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return false;
        }
        return entry.hasRemainingUsage();
    }
    
    private void cleanupExpired() {
        cache.entrySet().removeIf(entry -> 
            entry.getValue().isExpired() || !entry.getValue().hasRemainingUsage()
        );
    }
}
