package net.riches.islandgenerator.nms;

import net.riches.islandgenerator.api.ICBiome;
import net.riches.islandgenerator.api.ICWorld;
import org.bukkit.block.Biome;

import java.util.Arrays;

public class IslandCraftBiomeGenerator extends BiomeGenerator {
    private final ICWorld world;

    public IslandCraftBiomeGenerator(final ICWorld world) {
        this.world = world;
    }

    @Override
    public Biome generateBiome(final int x, final int z) {
        try {
            return world.getBiomeAt(x, z);
        } catch (final Exception e) {
            return Biome.DEEP_OCEAN;
        }
    }

    @Override
    public Biome[] generateChunkBiomes(final int x, final int z) {
        try {
            return world.getBiomeChunk(x, z);
        } catch (final Exception e) {
            final Biome[] result = new Biome[256];
            Arrays.fill(result, Biome.DEEP_OCEAN);
            return result;
        }
    }

    @Override
    public void cleanupCache() {
        // Nothing to do
    }
}