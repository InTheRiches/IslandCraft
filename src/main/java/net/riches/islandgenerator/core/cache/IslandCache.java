package net.riches.islandgenerator.core.cache;

import net.riches.islandgenerator.api.ICBiome;
import net.riches.islandgenerator.api.ICIsland;
import org.bukkit.block.Biome;

public class IslandCache {
    private final Cache<ICIsland, Biome[]> cache;

    public IslandCache() {
        cache = new ExpiringLoadingCache<ICIsland, Biome[]>(30, new IslandCacheLoader());
    }

    public Biome biomeAt(final ICIsland island, final int relativeX, final int relativeZ) {
        final Biome[] biomes = cache.get(island);
        final int xSize = island.getInnerRegion().getMax().getZ() - island.getInnerRegion().getMin().getZ();
        return biomes[relativeZ * xSize + relativeX];
    }

    private static final int BLOCKS_PER_CHUNK = 16;

    public Biome[] biomeChunk(final ICIsland island, final int relativeX, final int relativeZ) {
        final int xSize = island.getInnerRegion().getMax().getZ() - island.getInnerRegion().getMin().getZ();
        final Biome[] result = new Biome[BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK];
        final Biome[] biomes = cache.get(island);
        for (int z = 0; z < BLOCKS_PER_CHUNK; ++z) {
            System.arraycopy(biomes, xSize * (relativeZ + z) + relativeX, result, z * BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK);
        }
        return result;
    }

    public Biome[] biomeAll(final ICIsland island) {
        return cache.get(island).clone();
    }

    private static class IslandCacheLoader implements CacheLoader<ICIsland, Biome[]> {
        @Override
        public Biome[] load(final ICIsland island) {
            final int xSize = island.getInnerRegion().getMax().getX() - island.getInnerRegion().getMin().getX();
            final int zSize = island.getInnerRegion().getMax().getZ() - island.getInnerRegion().getMin().getZ();
            final long islandSeed = island.getSeed();
            return island.getGenerator().generate(xSize, zSize, islandSeed);
        }
    }
}
