package net.riches.islandgenerator.core.distribution;

import net.riches.islandgenerator.api.BiomeDistribution;
import net.riches.islandgenerator.api.ICBiome;
import org.bukkit.block.Biome;

public class ConstantBiomeDistribution implements BiomeDistribution {
    private final Biome biome;

    public ConstantBiomeDistribution(final String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("ConstantBiomeDistribution requrires 1 parameter");
        }
        biome = Biome.valueOf(args[0]);
    }

    @Override
    public Biome biomeAt(final int x, final int z, final long worldSeed) {
        return biome;
    }
}
