package net.riches.islandgenerator.api;

import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;

public class ICBiome {
  public static List<org.bukkit.block.Biome> getOverworldBiomes() {
      ArrayList<Biome> biomes = new ArrayList<>(List.of(Biome.values()));
      biomes.remove(Biome.CUSTOM);
      biomes.remove(Biome.ICE_SPIKES);
      for (NetherBiomes biome : NetherBiomes.values()) {
          biomes.remove(biome.toBiome());
      }
      return biomes;
  }
}
