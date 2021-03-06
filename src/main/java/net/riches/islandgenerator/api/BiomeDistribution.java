package net.riches.islandgenerator.api;

import org.bukkit.block.Biome;

public interface BiomeDistribution {
    /**
     * Returns the biome to be generated at the given location in a world with
     * the given random seed.
     *
     * @param x
     *            the x-coordinate of the location to get the biome at (measured
     *            in blocks)
     * @param z
     *            the z-coordinate of the location to get the biome at (measured
     *            in blocks)
     * @param worldSeed
     *            the random seed of the world to get the biome from
     * @return the biome to generate
     */
    Biome biomeAt(int x, int z, long worldSeed);
}
