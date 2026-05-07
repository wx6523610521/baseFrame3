package work.chncyl.base.global.tools.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用次数限制缓存
 * 线程安全的缓存实现，每个key可以设置最大使用次数
 */
public class UsageLimitCache implements CacheInterface {
    
    private final ConcurrentHashMap<String, AtomicInteger> usageCountMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> cacheData = new ConcurrentHashMap<>();
    
    /**
     * 放入缓存，并设置可使用次数
     * @param key 缓存键
     * @param value 缓存值
     * @param maxUsage 最大使用次数
     */
    public void put(String key, Object value, int maxUsage) {
        if (maxUsage <= 0) {
            throw new IllegalArgumentException("使用次数必须大于0");
        }
        cacheData.put(key, value);
        usageCountMap.put(key, new AtomicInteger(maxUsage));
    }
    
    /**
     * 放入缓存，默认使用次数为1
     */
    @Override
    public void put(String key, Object value) {
        put(key, value, 1);
    }
    
    /**
     * 如果key不存在则放入缓存
     * @return 剩余使用次数，如果key已存在返回null
     */
    @Override
    public Integer putIfAbsent(String key, Object value) {
        return putIfAbsent(key, value, 1);
    }
    
    /**
     * 如果key不存在则放入缓存，并设置使用次数
     * @return 剩余使用次数，如果key已存在返回null
     */
    public Integer putIfAbsent(String key, Object value, int maxUsage) {
        if (maxUsage <= 0) {
            throw new IllegalArgumentException("使用次数必须大于0");
        }
        
        AtomicInteger existingCount = usageCountMap.putIfAbsent(key, new AtomicInteger(maxUsage));
        if (existingCount != null) {
            return null; // key已存在
        }
        
        Object existingValue = cacheData.putIfAbsent(key, value);
        if (existingValue != null) {
            usageCountMap.remove(key);
            return null;
        }
        
        return maxUsage;
    }
    
    /**
     * 获取缓存值并消耗一次使用次数
     * @return 缓存值，如果key不存在或使用次数已耗尽则返回null
     */
    public Object getAndDecrement(String key) {
        AtomicInteger count = usageCountMap.get(key);
        if (count == null) {
            return null;
        }
        
        // 原子性递减，确保线程安全
        while (true) {
            int current = count.get();
            if (current <= 0) {
                // 使用次数已耗尽，移除缓存
                remove(key);
                return null;
            }
            
            // CAS操作保证线程安全
            if (count.compareAndSet(current, current - 1)) {
                Object value = cacheData.get(key);
                
                // 如果减到0，清理缓存
                if (current - 1 == 0) {
                    remove(key);
                }
                
                return value;
            }
            // 如果CAS失败，重试
        }
    }
    
    /**
     * 获取缓存值（不消耗使用次数）
     */
    @Override
    public Object get(String key) {
        AtomicInteger count = usageCountMap.get(key);
        if (count == null || count.get() <= 0) {
            return null;
        }
        return cacheData.get(key);
    }
    
    /**
     * 获取缓存值，如果不存在返回默认值（不消耗使用次数）
     */
    @Override
    public Object get(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 获取剩余使用次数
     */
    public int getRemainingUsage(String key) {
        AtomicInteger count = usageCountMap.get(key);
        return count == null ? 0 : count.get();
    }
    
    /**
     * 检查key是否存在且还有可用次数
     */
    @Override
    public boolean containsKey(String key) {
        AtomicInteger count = usageCountMap.get(key);
        return count != null && count.get() > 0;
    }
    
    /**
     * 移除缓存
     */
    @Override
    public Object remove(String key) {
        usageCountMap.remove(key);
        return cacheData.remove(key);
    }
    
    /**
     * 清空所有缓存
     */
    @Override
    public void clear() {
        usageCountMap.clear();
        cacheData.clear();
    }
    
    /**
     * 获取缓存大小
     */
    @Override
    public int size() {
        return cacheData.size();
    }
    
    /**
     * 销毁缓存
     */
    @Override
    public void destroy() {
        clear();
    }
}
