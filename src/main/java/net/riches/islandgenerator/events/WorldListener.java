package net.riches.islandgenerator.events;

import net.riches.islandgenerator.IslandCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;

import java.util.List;

public class WorldListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getWorld().getName().equals("world")) {
            Location old = e.getPlayer().getLocation();
            Location n = new Location(Bukkit.getWorld("main"), old.getX(), old.getY(), old.getZ(), old.getPitch(), old.getYaw());
            e.getPlayer().teleport(n);
        }
    }
;}
