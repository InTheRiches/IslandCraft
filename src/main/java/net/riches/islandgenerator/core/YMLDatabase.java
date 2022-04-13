package net.riches.islandgenerator.core;

import net.riches.islandgenerator.IslandCraft;
import net.riches.islandgenerator.api.IslandGenerator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YMLDatabase implements IslandDatabase {

    private FileConfiguration config;

    public void setup() {
        File file = new File(IslandCraft.getInstance().getDataFolder(), "islands.yml");

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
    public void save(String worldName, int centerX, int centerZ, long islandSeed) {
        int count = getCount();
        config.set("config.islands." + count + ".worldname", worldName);
        config.set("config.islands." + count + ".centerX", centerX);
        config.set("config.islands." + count + ".centerZ", centerZ);
        config.set("config.islands." + count + ".seed", islandSeed);
        try {
            config.save(new File(IslandCraft.getInstance().getDataFolder(), "islands.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result load(String worldName, int centerX, int centerZ) {
        for (int i = 0; config.getString("config.islands." + i + ".worldname") != null; ++i) {
            if (!config.getString("config.islands." + i + ".worldname").equals(worldName)) continue;
            if (config.getInt("config.islands." + i + ".centerX") != centerX) continue;
            if (config.getInt("config.islands." + i + ".centerZ") != centerZ) continue;
            return new Result(config.getLong("config.islands." + i + ".seed"));
        }
        return null;
    }

    @Override
    public boolean isEmpty(String worldName) {
        return false;
    }

    private int getCount() {
        int i;
        for (i = 0; config.getString("config.islands." + i + ".worldname") != null; ++i) {
            i++;
        }
        return i;
    }
}
