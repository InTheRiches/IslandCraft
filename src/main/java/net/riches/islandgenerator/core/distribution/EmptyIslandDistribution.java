package net.riches.islandgenerator.core.distribution;

import net.riches.islandgenerator.api.ICLocation;
import net.riches.islandgenerator.api.ICRegion;
import net.riches.islandgenerator.api.IslandDistribution;

import java.util.HashSet;
import java.util.Set;

public class EmptyIslandDistribution implements IslandDistribution {
    public EmptyIslandDistribution(final String[] args) {
        if (args.length != 0) {
            throw new IllegalArgumentException("EmptyIslandDistribution requrires 0 parameters, " + args.length + " given");
        }
    }

    @Override
    public ICLocation getCenterAt(final int x, final int z, final long worldSeed) {
        return null;
    }

    @Override
    public Set<ICLocation> getCentersAt(final int x, final int z, final long worldSeed) {
        return new HashSet<>(0);
    }

    @Override
    public ICRegion getInnerRegion(final ICLocation center, final long worldSeed) {
        return null;
    }

    @Override
    public ICRegion getOuterRegion(final ICLocation center, final long worldSeed) {
        return null;
    }
}
