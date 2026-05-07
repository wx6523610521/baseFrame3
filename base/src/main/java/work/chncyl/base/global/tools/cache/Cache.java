package work.chncyl.base.global.tools.cache;


import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Cache extends ConcurrentHashMap<String, AbstractMap.SimpleEntry<Object, Long>> {

    public void put(String key, Object value, Long expired, TimeUnit timeUnit) {
        AbstractMap.SimpleEntry<Object, Long> cache = this.computeIfAbsent(key,
                k -> new AbstractMap.SimpleEntry<>(value, expired == null ? null : calculateExpired(expired, timeUnit)));
        put(key, cache);
    }

    public boolean expired(String key, Long expired, TimeUnit timeUnit) {
        if (containsKey(key)) {
            get(key).setValue(calculateExpired(expired, timeUnit));
            return true;
        }
        return false;
    }

    public Long calculateExpired(Long expired, TimeUnit timeUnit) {
        return System.currentTimeMillis() + (timeUnit.toMillis(expired));
    }
}
