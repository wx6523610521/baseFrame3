package work.chncyl.base.global.tools.cache;

public interface CacheInterface {

    void put(String key, Object value);

    default void put(String key, Object value, Long expired){
        put(key, value);
    }

    Integer putIfAbsent(String key, Object value);

    default Integer putIfAbsent(String key, Object value, Long expired){
       return putIfAbsent(key, value);
    }

    default boolean expired(String key, Long expired){
        return true;
    }

    Object get(String key);

    Object get(String key, Object defaultValue);

    Object remove(String key);

    void clear();

    int size();

    boolean containsKey(String key);

    default void destroy() {
        clear();
    }


}
