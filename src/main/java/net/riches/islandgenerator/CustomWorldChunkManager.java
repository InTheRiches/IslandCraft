package net.riches.islandgenerator;

import net.riches.islandgenerator.api.ICBiome;
import net.riches.islandgenerator.api.ICWorld;
import net.riches.islandgenerator.api.NetherBiomes;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class CustomWorldChunkManager extends BiomeProvider {

    private List<ICWorld> worlds = new ArrayList<>();

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        for (ICWorld world : worlds) {
            if (!world.getName().equals(worldInfo.getName())) continue;

            return world.getBiomeAt(x, z);
        }
        return null;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        if (!worldInfo.getName().equals("main")) {
            List<Biome> biomes = new ArrayList<>();
            for (NetherBiomes ICbiome : NetherBiomes.values()) {
                biomes.add(ICbiome.toBiome());
            }
            return biomes;
        }
        return ICBiome.getOverworldBiomes();
    }

    public void addWorld(ICWorld world) {
        this.worlds.add(world);
    }
}
