package net.riches.islandgenerator.core;

public interface IslandDatabase {
    record Result(long islandSeed) {

        public long getIslandSeed() {
            return islandSeed;
        }
    }

    void save(String worldName, int centerX, int centerZ, long islandSeed);

    Result load(String worldName, int centerX, int centerZ);

    boolean isEmpty(String worldName);
}
