package me.khorae.minigame.Listeners;

import me.khorae.minigame.Managers.EconomyManager;
import me.khorae.minigame.Managers.ScoreboardManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EconomyListener implements Listener {

    private final EconomyManager economyManager;
    private final ScoreboardManager scoreboardManager;

    public EconomyListener(EconomyManager economyManager, ScoreboardManager scoreboardManager) {
        this.economyManager = economyManager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            EntityType entityType = event.getEntityType();

            int reward = switch (entityType) {
                case ZOMBIE -> 15;
                case SKELETON -> 25;
                case CREEPER -> 30;
                case CAVE_SPIDER -> 15;
                case ENDERMAN -> 40;
                case WITCH -> 25;
                default -> 0;
            };

            if (reward > 0) {
                economyManager.addCoins(player.getUniqueId(), reward);
                scoreboardManager.addKill(player);
                scoreboardManager.updateScoreboard(player);  // Actualizar el scoreboard
            }
        }
    }
}
