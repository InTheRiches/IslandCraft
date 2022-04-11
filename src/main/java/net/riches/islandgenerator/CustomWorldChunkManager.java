package net.riches.islandgenerator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.List;

public class CustomWorldChunkManager extends BiomeProvider {

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return null;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return null;
    }
}
