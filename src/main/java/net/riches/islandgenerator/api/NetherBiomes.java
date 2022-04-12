package net.riches.islandgenerator.api;

import org.bukkit.block.Biome;

public enum NetherBiomes {
    BASALT_DELTAS,
    CRIMSON_FOREST,
    NETHER_WASTES,
    SOUL_SAND_VALLEY,
    WARPED_FOREST;

    public Biome toBiome() {
        return Biome.valueOf(this.name());
    }
}
