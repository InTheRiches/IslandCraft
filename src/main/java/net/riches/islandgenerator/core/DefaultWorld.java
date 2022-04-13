package net.riches.islandgenerator.core;

import net.riches.islandgenerator.IslandCraft;
import net.riches.islandgenerator.api.*;
import net.riches.islandgenerator.core.cache.Cache;
import net.riches.islandgenerator.core.cache.CacheLoader;
import net.riches.islandgenerator.core.cache.ExpiringLoadingCache;
import net.riches.islandgenerator.core.cache.IslandCache;
import net.riches.islandgenerator.core.distribution.ConstantBiomeDistribution;
import net.riches.islandgenerator.core.distribution.HexagonalIslandDistribution;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

public class DefaultWorld implements ICWorld {
    private final String worldName;
    private final long worldSeed;
    private final IslandDatabase database;
    private final BiomeDistribution ocean;
    private final IslandDistribution islandDistribution;
    private final List<IslandGenerator> islandGenerators;
    private final IslandCache cache;
    private final ICClassLoader classLoader;
    private final Cache<ICLocation, ICIsland> databaseCache;

    public DefaultWorld(final String name, final long seed, final IslandDatabase database, final ICWorldConfig config, final IslandCache cache, final ICClassLoader classLoader) {
        this.worldName = name;
        this.worldSeed = seed;
        this.database = database;
        this.cache = cache;
        this.classLoader = classLoader;

        ocean = new ConstantBiomeDistribution(Biome.DEEP_OCEAN.toString());
        //islandDistribution = classLoader.getIslandDistribution(config.getIslandDistribution());
        islandDistribution = new HexagonalIslandDistribution("288", "32");
        islandGenerators = new ArrayList<>();
        List<String> gens = new ArrayList<>(Arrays.asList("net.riches.islandgenerator.core.DefaultIslandGenerator"));
//        islandGenerators = new ArrayList<>(Arrays.asList(config.getIslandGenerstors()));
        // Load islandGenerators just to make sure there are no errors
        for (final String islandGenerator : gens) {
            islandGenerators.add(classLoader.getIslandGenerator(islandGenerator));
        }

        databaseCache = new ExpiringLoadingCache<>(30, new DatabaseCacheLoader());
    }

    @Override
    public long getSeed() {
        return worldSeed;
    }

    @Override
    public String getName() {
        return worldName;
    }

    @Override
    public Biome getBiomeAt(final ICLocation location) {
        return getBiomeAt(location.getX(), location.getZ());
    }

    @Override
    public Biome getBiomeAt(final int x, final int z) {
        final ICIsland island = getIslandAt(x, z);
        if (island == null) {
            return Biome.DEEP_OCEAN;
        }
        final ICLocation origin = island.getInnerRegion().getMin();
        final Biome biome = island.getBiomeAt(x - origin.getX(), z - origin.getZ());
        if (biome == null) {
//            IslandCraft.getInstance().getLogger().log(Level.SEVERE, "Biome is null");
            return Biome.DEEP_OCEAN;
//            return ocean.biomeAt(x, z, worldSeed);
        }
        return biome;
    }

    @Override
    public Biome[] getBiomeChunk(ICLocation location) {
        return getBiomeChunk(location.getX(), location.getZ());
    }

    @Override
    public Biome[] getBiomeChunk(int x, int z) {
        final ICIsland island = getIslandAt(x, z);
        if (island == null) {
            final Biome[] chunk = new Biome[256];
            for (int i = 0; i < 256; ++i) {
                chunk[i] = ocean.biomeAt(x + i % 16, z + i / 16, worldSeed);
            }
            return chunk;
        }
        final ICLocation origin = island.getInnerRegion().getMin();
        final Biome[] biomes = island.getBiomeChunk(x - origin.getX(), z - origin.getZ());
        if (biomes == null) {
            final Biome[] chunk = new Biome[256];
            for (int i = 0; i < 256; ++i) {
                chunk[i] = ocean.biomeAt(x + i % 16, z + i / 16, worldSeed);
            }
            return chunk;
        }
        for (int i = 0; i < 256; ++i) {
            if (biomes[i] == null) {
                biomes[i] = ocean.biomeAt(x + i % 16, z + i / 16, worldSeed);
            }
        }
        return biomes;
    }

    @Override
    public ICIsland getIslandAt(final ICLocation location) {
        return getIslandAt(location.getX(), location.getZ());
    }

    @Override
    public ICIsland getIslandAt(final int x, final int z) {
        final ICLocation center = islandDistribution.getCenterAt(x, z, worldSeed);
        if (center == null) {
            return null;
        }
        return databaseCache.get(center);
    }

    @Override
    public Set<ICIsland> getIslandsAt(final ICLocation location) {
        return getIslandsAt(location.getX(), location.getZ());
    }

    @Override
    public Set<ICIsland> getIslandsAt(final int x, final int z) {
        final Set<ICLocation> centers = islandDistribution.getCentersAt(x, z, worldSeed);
        final Set<ICIsland> islands = new HashSet<ICIsland>(centers.size());
        for (final ICLocation center : centers) {
            islands.add(databaseCache.get(center));
        }
        return islands;
    }

    private class DatabaseCacheLoader implements CacheLoader<ICLocation, ICIsland> {
        @Override
        public ICIsland load(final ICLocation center) {
            final ICRegion innerRegion = islandDistribution.getInnerRegion(center, worldSeed);
            final ICRegion outerRegion = islandDistribution.getOuterRegion(center, worldSeed);
            final IslandDatabase.Result fromDatabase = database.load(worldName, center.getX(), center.getZ());
            if (fromDatabase == null) {
                final long islandSeed = pickIslandSeed(center.getX(), center.getZ());
                final IslandGenerator generator = pickIslandGenerator(islandSeed);
                database.save(worldName, center.getX(), center.getZ(), islandSeed);

                ICIsland island = new DefaultIsland(innerRegion, outerRegion, islandSeed, cache);
                final int xSize = island.getInnerRegion().getMax().getX() - island.getInnerRegion().getMin().getX();
                final int zSize = island.getInnerRegion().getMax().getZ() - island.getInnerRegion().getMin().getZ();

                cache.addIsland(island, pickIslandGenerator(islandSeed).generate(xSize, zSize, islandSeed));
                return island;
            }
            return new DefaultIsland(innerRegion, outerRegion, fromDatabase.getIslandSeed(), cache);
        }

        private long pickIslandSeed(final int centerX, final int centerZ) {
            return new Random(worldSeed ^ ((long) centerX << 24 | centerZ & 0x00FFFFFFL)).nextLong();
        }

        private IslandGenerator pickIslandGenerator(final long islandSeed) {
            return islandGenerators.get(new Random(islandSeed).nextInt(islandGenerators.size()));
        }
    }

    @Override
    public int hashCode() {
        return worldName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DefaultWorld other = (DefaultWorld) obj;
        return worldName.equals(other.worldName);
    }
}
