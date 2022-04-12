package net.riches.islandgenerator.core;

import net.riches.islandgenerator.api.IslandGenerator;
import org.bukkit.block.Biome;

public class EmptyIslandGenerator implements IslandGenerator {
    public EmptyIslandGenerator(final String[] args) {
        if (args.length != 0) {
            throw new IllegalArgumentException("EmptyIslandGenerator requrires 0 parameters, " + args.length + " given");
        }
    }

    @Override
    public Biome[] generate(final int xSize, final int zSize, final long islandSeed) {
        return new Biome[xSize * zSize];
    }
}
