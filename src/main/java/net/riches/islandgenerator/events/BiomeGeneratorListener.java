package net.riches.islandgenerator.events;

//import net.riches.islandgenerator.IslandCraft;
//import net.riches.islandgenerator.nms.BiomeGenerator;
//import net.riches.islandgenerator.nms.IslandCraftBiomeGenerator;
//import net.riches.islandgenerator.nms.NMSHandler;
//import net.riches.islandgenerator.api.ICClassLoader;
//import net.riches.islandgenerator.api.ICWorld;
//import net.riches.islandgenerator.core.DefaultWorld;
//import net.riches.islandgenerator.core.IslandDatabase;
//import net.riches.islandgenerator.core.cache.IslandCache;
//import org.bukkit.World;
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.world.WorldInitEvent;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class BiomeGeneratorListener implements Listener {
//    private final Set<String> worldsDone;
//    private final IslandCraft islandCraft;
//    private final IslandDatabase database;
//    private final NMSHandler nms;
//    private final IslandCache cache;
//    private final ICClassLoader classLoader;
//
//    public BiomeGeneratorListener(final IslandCraft plugin, final IslandDatabase database, final NMSHandler nms) {
//        this.islandCraft = plugin;
//        this.database = database;
//        this.nms = nms;
//        if (!islandCraft.getConfig().contains("worlds") || !islandCraft.getConfig().isConfigurationSection("worlds")) {
//            throw new IllegalArgumentException("No configuration section for 'worlds' found in config.yml");
//        }
//        worldsDone = new HashSet<>();
//        cache = new IslandCache();
//        classLoader = new ICClassLoader();
//    }
//
//    @EventHandler
//    public void onWorldInit(final WorldInitEvent event) {
//        final World world = event.getWorld();
//        final String worldName = world.getName();
//        if (worldsDone.contains(worldName)) {
//            return;
//        }
//        islandCraft.reloadConfig();
//        final ConfigurationSection worlds = islandCraft.getConfig().getConfigurationSection("worlds");
//        if (worlds == null) {
//            return;
//        }
//        final ConfigurationSection config = worlds.getConfigurationSection(worldName);
//        if (config == null) {
//            return;
//        }
//
//        final ICWorld icWorld = new DefaultWorld(worldName, world.getSeed(), database, new BukkitWorldConfig(worldName, config), cache, classLoader);
//        final BiomeGenerator biomeGenerator = new IslandCraftBiomeGenerator(icWorld);
//        nms.installBiomeGenerator(world, biomeGenerator);
//        worldsDone.add(worldName);
//        islandCraft.getIslandCraft().addWorld(icWorld);
//    }
//
//    @EventHandler
//    public void onChunkLoad(final ChunkLoadEvent event) {
//        // First time server is run it will generate some chunks to find spawn
//        // point this happens before WorldInitEvent. This event catches the
//        // first one of those chunks, applies the hack, and regenerates the
//        // chunk with the new WorldChunkManager.
//        final World world = event.getWorld();
//        final String worldName = world.getName();
//        if (worldsDone.contains(worldName)) {
//            return;
//        }
//        final ConfigurationSection worlds = islandCraft.getConfig().getConfigurationSection("worlds");
//        if (worlds == null) {
//            return;
//        }
//        final ConfigurationSection config = worlds.getConfigurationSection(worldName);
//        if (config == null) {
//            return;
//        }
//        ICLogger.logger.info("Installing biome generator in ChunkLoadEvent for world with name: " + worldName);
//        final ICWorld icWorld = new DefaultWorld(worldName, world.getSeed(), database, new BukkitWorldConfig(worldName, config), cache, classLoader);
//        final BiomeGenerator biomeGenerator = new IslandCraftBiomeGenerator(icWorld);
//        if (nms.installBiomeGenerator(world, biomeGenerator)) {
//            // If this is the very first time, regenerate the chunk
//            if (database.isEmpty(worldName)) {
//                final Chunk chunk = event.getChunk();
//                ICLogger.logger.info(String.format("Regenerating spawn chunk at x: %d, z: %d", chunk.getX(), chunk.getZ()));
//                world.regenerateChunk(chunk.getX(), chunk.getZ());
//            }
//        }
//        worldsDone.add(worldName);
//        islandCraft.getIslandCraft().addWorld(icWorld);
//    }
//}
