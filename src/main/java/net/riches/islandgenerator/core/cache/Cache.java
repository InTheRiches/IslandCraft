package net.riches.islandgenerator.core.cache;

public interface Cache<K, V> {
    V get(K key);
}
