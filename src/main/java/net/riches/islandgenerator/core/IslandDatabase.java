package net.riches.islandgenerator.core;

public interface IslandDatabase {
    record Result(long islandSeed, String generator) {

        public long getIslandSeed() {
            return islandSeed;
        }

        public String getGenerator() {
            return generator;
        }
    }

    void save(String worldName, int centerX, int centerZ, long islandSeed, String generator);

    Result load(String worldName, int centerX, int centerZ);

    boolean isEmpty(String worldName);
}
