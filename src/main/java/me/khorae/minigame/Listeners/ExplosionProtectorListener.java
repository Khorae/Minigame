package me.khorae.minigame.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;


public class ExplosionProtectorListener  implements Listener {
    @EventHandler
    public void onExplode(EntityExplodeEvent event){
        event.blockList().clear();
    }
}
