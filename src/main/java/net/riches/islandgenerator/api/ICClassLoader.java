package net.riches.islandgenerator.api;

import net.riches.islandgenerator.core.distribution.ConstantBiomeDistribution;
import net.riches.islandgenerator.core.distribution.EmptyIslandDistribution;
import net.riches.islandgenerator.core.EmptyIslandGenerator;
import net.riches.islandgenerator.core.cache.Cache;
import net.riches.islandgenerator.core.cache.CacheLoader;
import net.riches.islandgenerator.core.cache.EternalLoadingCache;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ICClassLoader {
    private final Cache<String, IslandDistribution> islandDistributionCache;
    private final Cache<String, IslandGenerator> islandGeneratorCache;
    private final Cache<String, BiomeDistribution> biomeDistributionCache;

    public ICClassLoader() {
        islandDistributionCache = new EternalLoadingCache<>(new StringConstructorCacheLoader<>());
        islandGeneratorCache = new EternalLoadingCache<>(new StringConstructorCacheLoader<>());
        biomeDistributionCache = new EternalLoadingCache<>(new StringConstructorCacheLoader<>());
    }

    public IslandDistribution getIslandDistribution(final String string) {
        try {
            return islandDistributionCache.get(string);
        } catch (final Exception e) {
            return new EmptyIslandDistribution(new String[0]);
        }
    }

    public IslandGenerator getIslandGenerator(final String string) {
        try {
            return islandGeneratorCache.get(string);
        } catch (final Exception e) {
            return new EmptyIslandGenerator(new String[0]);
        }
    }

    public BiomeDistribution getBiomeDistribution(final String string) {
        try {
            return biomeDistributionCache.get(string);
        } catch (final Exception e) {
            return new ConstantBiomeDistribution(new String[] { "DEEP_OCEAN" });
        }
    }

    private static class StringConstructorCacheLoader<T> implements CacheLoader<String, T> {
        @Override
        @SuppressWarnings("unchecked")
        public T load(final String string) {
            try {
                final String[] split = string.split(" ");
                final String className = split[0];
                final String[] args = Arrays.copyOfRange(split, 1, split.length);
                final Class<?> subClass = Class.forName(className);
                final Constructor<?> constructor = subClass.getConstructor(String[].class);
                return (T) constructor.newInstance(new Object[] { args });
            } catch (final Exception e) {
                throw new RuntimeException("Failed to create instance of " + string, e);
            }
        }
    }
}
