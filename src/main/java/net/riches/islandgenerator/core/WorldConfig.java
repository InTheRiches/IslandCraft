package net.riches.islandgenerator.core;

import net.riches.islandgenerator.IslandCraft;
import net.riches.islandgenerator.api.ICWorld;
import net.riches.islandgenerator.api.ICWorldConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldConfig implements ICWorldConfig {

    private final String worldName;
    private final FileConfiguration config;

    public WorldConfig(String worldName) {
        this.worldName = worldName;
        File file = new File(IslandCraft.getInstance().getDataFolder(), "worlds.yml");

        if (!file.exists()) {
            try{
                file.createNewFile();
            }catch (IOException e){
                //bruh
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public String getOcean() {
        if (!config.contains("ocean") || !config.isString("ocean")) {}
        return config.getString("ocean", "net.riches.islandgenerator.core.distribution.ConstantBiomeDistribution DEEP_OCEAN");
    }

    @Override
    public String getIslandDistribution() {
        if (!config.contains("island-distribution") || !config.isString("island-distribution")) {
        }
        return config.getString("island-distribution", "net.riches.islandgenerator.core.distribution.EmptyIslandDistribution");
    }

    @Override
    public String[] getIslandGenerstors() {
        if (!config.contains("island-generators") || !config.isList("island-generators")) {
            List<String> islandGenerators = new ArrayList<>();
            islandGenerators.add("net.riches.islandgenerator.core.DefaultIslandGenerator");
            return islandGenerators.toArray(new String[0]);
        }
        List<String> islandGenerators = config.getStringList("island-generators");
        if (islandGenerators.isEmpty()) {
            islandGenerators.add("net.riches.islandgenerator.core.DefaultIslandGenerator");
        }
        return islandGenerators.toArray(new String[islandGenerators.size()]);
    }

    public String getWorldName() {
        return worldName;
    }
}
