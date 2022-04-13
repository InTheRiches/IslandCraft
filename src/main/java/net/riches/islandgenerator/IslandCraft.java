package net.riches.islandgenerator;

import net.riches.islandgenerator.api.ICClassLoader;
import net.riches.islandgenerator.core.DefaultWorld;
import net.riches.islandgenerator.core.WorldConfig;
import net.riches.islandgenerator.core.YMLDatabase;
import net.riches.islandgenerator.core.cache.IslandCache;
import net.riches.islandgenerator.events.WorldListener;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public final class IslandCraft extends JavaPlugin {

    private static IslandCraft instance;
    private CustomWorldChunkManager biomeProvider;
    private FileConfiguration config;
    private YMLDatabase database;
    private IslandCache cache;
    private ICClassLoader classLoader;
    private WorldConfig worldConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.database = new YMLDatabase();
        this.database.setup();
        this.cache = new IslandCache();
        this.classLoader = new ICClassLoader();
        this.biomeProvider = new CustomWorldChunkManager();
        this.worldConfig = new WorldConfig("main");

        //this.configs.add(new WorldConfig("main"));
        this.getServer().getPluginManager().registerEvents(new WorldListener(), this);

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateWorld();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

//    @Override
//    public BiomeProvider getDefaultBiomeProvider(String worldName, @Nullable String id) {
//        if (!worldName.equals("nether") && !worldName.equals("the_end")) {
//            return new CustomWorldChunkManager();
//        }
//        return super.getDefaultBiomeProvider(worldName, id);
//    }

    public CustomWorldChunkManager getBiomeProvider() {
        return biomeProvider;
    }

    public static IslandCraft getInstance() {
        return instance;
    }

    private void generateWorld() {
        WorldCreator creator = new WorldCreator("main");
        this.biomeProvider.addWorld(new DefaultWorld("main", creator.seed(), database, worldConfig, cache, classLoader));
        creator.biomeProvider(IslandCraft.getInstance().getBiomeProvider());
        creator.createWorld();
    }
}
