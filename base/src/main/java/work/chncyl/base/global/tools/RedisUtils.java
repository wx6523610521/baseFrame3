package work.chncyl.base.global.tools;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils implements CommandLineRunner {
  private static final Integer RELEASE_SUCCESS = Integer.valueOf(1);
  
  private static RedisTemplate<String, Object> redisTemplate;
  
  public static void set(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }
  
  public static void set(String key, Object value, Integer seconds) {
    set(key, value, seconds.intValue(), TimeUnit.SECONDS);
  }
  
  public static void set(String key, Object value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, value, timeout, unit);
  }
  
  public static Boolean expire(String key, long timeout, TimeUnit unit) {
    return redisTemplate.expire(key, timeout, unit);
  }
  
  public static void expire(String key, long time) {
    redisTemplate.expire(key, time, TimeUnit.SECONDS);
  }
  
  public static <T> T get(String key) {
    ValueOperations<String, Object> operations = redisTemplate.opsForValue();
    return (T)operations.get(key);
  }
  
  public Boolean delete(String key) {
    return redisTemplate.delete(key);
  }
  
  public long deleteObject(Collection collection) {
    return redisTemplate.delete(collection).longValue();
  }
  
  public static Boolean tryGetDistributedLock(String lockKey, String requestId, long expireTime) {
    return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, Duration.ofSeconds(expireTime));
  }
  
  public static boolean releaseDistributedLock(String lockKey, String requestId) {
    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    DefaultRedisScript<Integer> redisScript = new DefaultRedisScript(script);
    Integer execute = (Integer)redisTemplate.execute((RedisScript)redisScript, Collections.singletonList(lockKey), new Object[] { requestId });
    if (RELEASE_SUCCESS.equals(execute))
      return true; 
    return false;
  }
  
  public void run(String... args) {
    redisTemplate = (RedisTemplate<String, Object>)SpringUtils.getBean("ObjectRedisTemplate", RedisTemplate.class);
  }
}
