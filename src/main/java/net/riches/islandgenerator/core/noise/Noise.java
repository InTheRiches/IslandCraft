package net.riches.islandgenerator.core.noise;

public interface Noise {
    /** Returns noise value in range [0, 1] **/
    double noise(double x, double z);
}
