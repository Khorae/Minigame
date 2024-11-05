package me.khorae.minigame.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class FightEndListener implements Listener {

    private final BossAreaListener bossAreaListener;

    public FightEndListener(BossAreaListener bossAreaListener) {
        this.bossAreaListener = bossAreaListener;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Comprobar si el último daño recibido fue por el jefe (Zombie Boss)
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            Entity damager = damageEvent.getDamager();

            if (damager != null && damager.getCustomName() != null &&
                    ChatColor.stripColor(damager.getCustomName()).equals("Zombie Boss")) {
                Bukkit.broadcastMessage(ChatColor.RED + "Pathetic... ");
                bossAreaListener.resetBossFight(); // Reiniciar la pelea si el jefe mata al jugador
                event.setKeepInventory(true);
                event.setKeepLevel(true);
                event.setDroppedExp(0);
                event.getDrops().clear();
                return;
            }
        }

        // Si muere por otra causa
        Bukkit.broadcastMessage(ChatColor.GRAY + "You died, yet the boss still lives...");
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        String bossName = ChatColor.stripColor("Zombie Boss");

        if (event.getEntity().getCustomName() != null &&
                ChatColor.stripColor(event.getEntity().getCustomName()).equals(bossName)) {
            // Mensaje global
            Bukkit.broadcastMessage(ChatColor.GREEN + "¡El jefe ha sido derrotado!");

            // Reiniciar la pelea
            bossAreaListener.resetBossFight();
        }
    }
}

