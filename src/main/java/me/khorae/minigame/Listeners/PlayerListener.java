package me.khorae.minigame.Listeners;

import me.khorae.minigame.BossFight;
import me.khorae.minigame.Managers.ScoreboardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import java.time.Duration;

public class PlayerListener implements Listener {

    private final BossFight bossFight;
    private final World arena;
    private final Location locArena;
    private final ScoreboardManager scoreboardManager;

    // Constructor que recibe el mundo arena
    public PlayerListener(World arena, BossFight bossFight,ScoreboardManager scoreboardManager) {
        this.arena = arena;
        this.locArena = new Location(arena, 10, -60, 10);  // Coordenadas objetivo
        this.bossFight = bossFight;
        this.scoreboardManager = scoreboardManager;
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Comprobar si el jugador ha cambiado de bloque
        if (hasChangedBlock(event)) {
            // Verificar si el jugador está dentro del rango especificado
            if (isInRange(player.getLocation(), locArena, 3, 3, 1)) {
                // Crear el título
                Title title = Title.title(
                        Component.text("Welcome").color(NamedTextColor.RED),
                        Component.text("to the arena").color(NamedTextColor.RED),
                        Title.Times.times(
                                Duration.ofSeconds(1),
                                Duration.ofSeconds(1),
                                Duration.ofSeconds(1)
                        )
                );
                // Mostrar el título
                player.showTitle(title);
            }
        }
        // Si el jugador está cerca del jefe, añadirlo a la BossBar
        if (bossFight.isBossNearby(player)) {
            bossFight.addPlayerToBossBar(player);
        }
    }

    // Método para verificar si el jugador cambió de bloque
    private boolean hasChangedBlock(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        return from.getBlockX() != to.getBlockX() ||
                from.getBlockY() != to.getBlockY() ||
                from.getBlockZ() != to.getBlockZ();
    }

    // Método para comprobar si el jugador está dentro de un rango específico
    private boolean isInRange(Location playerLocation, Location targetLocation, int rangeX, int rangeY, int rangeZ) {
        return playerLocation.getWorld().equals(targetLocation.getWorld()) &&
                Math.abs(playerLocation.getBlockX() - targetLocation.getBlockX()) <= rangeX &&
                Math.abs(playerLocation.getBlockY() - targetLocation.getBlockY()) <= rangeY &&
                Math.abs(playerLocation.getBlockZ() - targetLocation.getBlockZ()) <= rangeZ;
    }



    // Teletransporta al jugador al unirse
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Location spawnLocation = new Location(arena, 16, -60, 16,180,0);
        event.getPlayer().teleport(spawnLocation);
        event.getPlayer().sendMessage("¡Bienvenido al mundo Arena!");
        scoreboardManager.createScoreboard(event.getPlayer());
    }

    // Establece la ubicación de reaparición del jugador al morir
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location respawnLocation = new Location(arena, 16, -60, 16,180,0);
        event.setRespawnLocation(respawnLocation);
        System.out.println("[Minigame] El jugador reaparecerá en el mundo 'Arena'.");
        Player player = event.getPlayer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,Integer.MAX_VALUE,254,false,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,Integer.MAX_VALUE,0,false,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,1,false,false,false));
        player.setMaxHealth(60);
        player.setHealth(60);
    }

}
