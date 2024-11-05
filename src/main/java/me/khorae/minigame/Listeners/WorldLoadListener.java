package me.khorae.minigame.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        if (world.getName().equalsIgnoreCase("Arena")) {
            Bukkit.getLogger().info("El mundo 'Arena' ha sido cargado.");
        }
    }
}

