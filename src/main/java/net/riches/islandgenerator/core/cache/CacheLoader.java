package net.riches.islandgenerator.core.cache;

public interface CacheLoader<K, V> {
    V load(K key);
}
