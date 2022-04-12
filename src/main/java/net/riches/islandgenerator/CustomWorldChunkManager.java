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
    public org.bukkit.block.Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return Biome.BADLANDS;
//        System.out.println("getBiome was ran");
//        for (ICWorld world : worlds) {
//            if (!world.getName().equals(worldInfo.getName())) continue;

//            System.out.println("returning getBiomeAt");
//            return world.getBiomeAt(x, z);
//        }
//        return null;
    }

    @Override
    public List<org.bukkit.block.Biome> getBiomes(WorldInfo worldInfo) {
        if (!worldInfo.getName().equals("main")) {
            List<org.bukkit.block.Biome> biomes = new ArrayList<>();
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
