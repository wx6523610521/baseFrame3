package work.chncyl.base.global.tools.cache;

import java.util.AbstractMap;
import java.util.concurrent.TimeUnit;

/**
 * 含过期时间的缓存
 */
public class TimeLimitCache implements CacheInterface {
    private static final Cache cache = new Cache();

    @Override
    public void put(String key, Object value) {
        put(key, value, null);
    }

    @Override
    public void put(String key, Object value, Long expiredSeconds) {
        cache.put(key, value, expiredSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean expired(String key, Long expiredSeconds) {
        return cache.expired(key, expiredSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        if (cache.containsKey(key)) {
            AbstractMap.SimpleEntry<Object, Long> entry = cache.get(key);
            if (entry.getValue() == null || entry.getValue() > System.currentTimeMillis()) {
                return entry.getKey();
            }
            return null;
        }
        return null;
    }

    @Override
    public Object get(String key, Object defaultValue) {
        if (cache.containsKey(key)) {
            AbstractMap.SimpleEntry<Object, Long> entry = cache.get(key);
            if (entry.getValue() == null || entry.getValue() > System.currentTimeMillis()) {
                return entry.getKey();
            }
            return defaultValue;
        }
        return defaultValue;
    }

    @Override
    public Integer putIfAbsent(String key, Object value) {
        if (!cache.containsKey(key)) {
            put(key, value);
            return 1;
        }
        return 0;
    }

    @Override
    public Integer putIfAbsent(String key, Object value, Long expiredSeconds) {
        if (!cache.containsKey(key)) {
            put(key, value, expiredSeconds);
            return 1;
        }
        return 0;
    }

    @Override
    public Object remove(String key) {
        if (cache.containsKey(key)) {
            return cache.remove(key);
        }
        return null;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
