package net.riches.islandgenerator.core;

import net.riches.islandgenerator.api.ICBiome;
import net.riches.islandgenerator.api.IslandGenerator;
import net.riches.islandgenerator.core.mosiac.Poisson;
import net.riches.islandgenerator.core.mosiac.Site;
import net.riches.islandgenerator.core.noise.Noise;
import net.riches.islandgenerator.core.noise.OctaveNoise;
import org.bukkit.block.Biome;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class DefaultIslandGenerator implements IslandGenerator {
    private static final double MIN_DISTANCE = 8;
    private static final double NOISE = 2.7;
    private static final double CIRCLE = 2;
    private static final double SQUARE = 0;
    private static final double THRESHOLD = 2;
    private final Color ocean;
    private final Color normal;
    private final Color mountains;
    private final Color hills;
    private final Color forest;
    private final Color forestMountains;
    private final Color outerCoast;
    private final Color innerCoast;

    // private final Color river; // unused for now

//  final String... args
    public DefaultIslandGenerator() {
//        if (args.length != 9) {
//            throw new IllegalArgumentException("IslandGeneratorAlpha requires 9 parameters");
//        }
        ocean = new Color(ICBiome.getOverworldBiomes().size(), true);
        normal = biomeColor(Biome.DEEP_OCEAN.toString(), ocean);
        mountains = biomeColor(Biome.WINDSWEPT_HILLS.toString(), normal);
        hills = biomeColor(Biome.WINDSWEPT_GRAVELLY_HILLS.toString(), normal);
        forest = biomeColor(Biome.BIRCH_FOREST.toString(), normal);
        forestMountains = biomeColor(Biome.WINDSWEPT_FOREST.toString(), forest);
        outerCoast = biomeColor(Biome.BEACH.toString(), normal);
        innerCoast = biomeColor(Biome.PLAINS.toString(), normal);
        // river = biomeColor(args[8], normal); // unused for now
    }

    @Override
    public Biome[] generate(final int xSize, final int zSize, final long islandSeed) {
        final Poisson poisson = new Poisson(xSize, zSize, MIN_DISTANCE);
        final List<Site> sites = poisson.generate(new Random(islandSeed));
        final Noise shapeNoise = new OctaveNoise(islandSeed);
        final Noise hillsNoise = new OctaveNoise(islandSeed + 1);
        final Noise forestNoise = new OctaveNoise(islandSeed + 2);
        final Noise mountainsNoise = new OctaveNoise(islandSeed + 3);
        // Find borders
        final Queue<Site> oceanSites = new LinkedList<>();
        for (final Site site : sites) {
            if (site.polygon == null) {
                site.isOcean = true;
                oceanSites.add(site);
            }
        }
        final List<Site> suspectCoast = new ArrayList<>();
        final List<Site> coast = new ArrayList<>();
        // Find oceans and coasts
        while (!oceanSites.isEmpty()) {
            final Site site = oceanSites.remove();
            for (final Site neighbor : site.neighbors) {
                if (site.polygon == null) {
                    if (!neighbor.isOcean) {
                        neighbor.isOcean = true;
                        oceanSites.add(neighbor);
                    }
                } else {
                    final double dx = (double) (neighbor.x - (xSize / 2)) / (double) (xSize / 2);
                    final double dz = (double) (neighbor.z - (zSize / 2)) / (double) (zSize / 2);
                    if (NOISE * noise(dx, dz, shapeNoise) + CIRCLE * circle(dx, dz)
                            + SQUARE * square(dx, dz) > THRESHOLD) {
                        if (!neighbor.isOcean) {
                            neighbor.isOcean = true;
                            oceanSites.add(neighbor);
                        }
                    } else {
                        neighbor.isInnerCoast = true;
                        suspectCoast.add(neighbor);
                    }
                }
            }
        }
        // Create coast
        SITE: for (final Site site : suspectCoast) {
            for (final Site neighbor : site.neighbors) {
                if (!neighbor.isOcean && !neighbor.isInnerCoast) {
                    coast.add(site);
                    continue SITE;
                }
            }
            site.isInnerCoast = false;
            site.isOcean = true;
        }
        // Create shallow ocean
        for (final Site site : coast) {
            for (final Site neighbor : site.neighbors) {
                if (neighbor.isOcean) {
                    neighbor.isOcean = false;
                    neighbor.isOuterCoast = true;
                }
            }
        }
        // Create blank image
        final BufferedImage image = new BufferedImage(xSize, zSize, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setBackground(ocean);
        graphics.clearRect(0, 0, xSize, zSize);
        // Render island
        for (final Site site : sites) {
            if (site.isOcean) {
                continue;
            } else if (site.isOuterCoast) {
                graphics.setColor(outerCoast);
            } else if (site.isInnerCoast) {
                graphics.setColor(innerCoast);
            } else if (noise(site, 0.375, 160.0, mountainsNoise)) {
                if (noise(site, 0.375, 160.0, forestNoise)) {
                    graphics.setColor(forestMountains);
                } else {
                    graphics.setColor(mountains);
                }
            } else {
                if (noise(site, 0.375, 80.0, hillsNoise)) {
                    graphics.setColor(hills);
                } else if (noise(site, 0.375, 160.0, forestNoise)) {
                    graphics.setColor(forest);
                } else {
                    graphics.setColor(normal);
                }
            }
            graphics.fillPolygon(site.polygon);
            graphics.drawPolygon(site.polygon);
        }
        // Save result
        graphics.dispose();
        final org.bukkit.block.Biome[] result = new org.bukkit.block.Biome[xSize * zSize];
        final org.bukkit.block.Biome[] values = ICBiome.getOverworldBiomes().toArray(new org.bukkit.block.Biome[0]);
        final int maxOrdinal = values.length;
        for (int i = 0; i < result.length; ++i) {
            final int x = i % xSize;
            final int z = i / xSize;
            final int ordinal = image.getRGB(x, z);
            if (ordinal < maxOrdinal) {
                result[i] = values[ordinal];
            }
        }
        return result;
    }

    private static Color biomeColor(final String name, final Color backup) {
        if (name.equals("~")) {
            return backup;
        }
        return new Color(Biome.valueOf(name).ordinal(), true);
    }

    private static boolean noise(Site site, final double threshold, final double period, final Noise noise) {
        return noise.noise(site.x / period, site.z / period) < threshold;
    }

    private static double noise(final double dx, final double dz, final Noise noise) {
        return noise.noise(dx, dz);
    }

    private static double circle(final double dx, final double dz) {
        return (dx * dx + dz * dz) / 2;
    }

    private static double square(final double dx, final double dz) {
        return Math.max(Math.abs(dx), Math.abs(dz));
    }
}
