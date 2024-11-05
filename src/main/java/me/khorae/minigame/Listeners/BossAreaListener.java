package me.khorae.minigame.Listeners;

import me.khorae.minigame.BossFight;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossAreaListener implements Listener {

    private final BossFight bossFight;
    private final Location bossSpawnLocation;
    private final int x1, y1, z1, x2, y2, z2;
    private boolean bossSpawned = false;
    private boolean cooldownActive = false; // Control del cooldown
    private Set<Location> wallBlocks = new HashSet<>();

    // Almacenar la ubicación anterior de cada jugador
    private final ConcurrentHashMap<UUID, Location> playerLastLocation = new ConcurrentHashMap<>();

    public BossAreaListener(BossFight bossFight, Location bossSpawnLocation,
                            int x1, int y1, int z1, int x2, int y2, int z2) {
        this.bossFight = bossFight;
        this.bossSpawnLocation = bossSpawnLocation;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Location currentLocation = player.getLocation();

        // Obtener la ubicación anterior del jugador
        Location lastLocation = playerLastLocation.getOrDefault(playerId, currentLocation);

        // Guardar la ubicación actual como la última para la próxima vez
        playerLastLocation.put(playerId, currentLocation);

        // Verificar si el jugador está entrando al área (desde afuera hacia adentro)
        if (!isInArea(lastLocation) && isInArea(currentLocation) && !bossSpawned && !cooldownActive) {
            spawnBoss(); // Generar el jefe
        }
    }

    // Verificar si la ubicación está dentro del área del jefe
    private boolean isInArea(Location loc) {
        return loc.getBlockX() >= x1 && loc.getBlockX() <= x2 &&
                loc.getBlockY() >= y1 && loc.getBlockY() <= y2 &&
                loc.getBlockZ() >= z1 && loc.getBlockZ() <= z2;
    }

    // Generar el jefe y cerrar el área
    private void spawnBoss() {
        bossFight.spawnBoss(bossSpawnLocation);
        Bukkit.broadcastMessage(ChatColor.RED + "¡El jefe ha aparecido!");

        buildWalls(); // Cerrar el área con paredes
        bossSpawned = true;
    }

    // Construir paredes con bloques irrompibles
    private void buildWalls() {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                setBlock(new Location(bossSpawnLocation.getWorld(), x, y, z1), Material.BARRIER);
                setBlock(new Location(bossSpawnLocation.getWorld(), x, y, z2), Material.BARRIER);
            }
            for (int z = z1; z <= z2; z++) {
                setBlock(new Location(bossSpawnLocation.getWorld(), x1, y, z), Material.BARRIER);
                setBlock(new Location(bossSpawnLocation.getWorld(), x2, y, z), Material.BARRIER);
            }
        }
    }

    // Establecer un bloque y almacenar su ubicación
    private void setBlock(Location location, Material material) {
        location.getBlock().setType(material);
        wallBlocks.add(location); // Guardar para eliminación posterior
    }

    // Eliminar las paredes
    public void removeWalls() {
        for (Location location : wallBlocks) {
            location.getBlock().setType(Material.AIR);
        }
        wallBlocks.clear();
    }

    // Reiniciar el estado del jefe y del área con cooldown
    public void resetBossFight() {
        bossFight.despawnBoss(); // Eliminar el jefe si sigue vivo
        removeWalls(); // Eliminar las paredes
        bossSpawned = false; // Permitir nueva aparición tras cooldown

        // Iniciar el cooldown de 30 segundos
        startCooldown();
    }

    // Iniciar el cooldown de 30 segundos
    private void startCooldown() {
        cooldownActive = true;
        Bukkit.broadcastMessage(ChatColor.YELLOW + "El jefe podrá reaparecer en 30 segundos...");

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldownActive = false; // Terminar el cooldown
                Bukkit.broadcastMessage(ChatColor.GREEN + "El jefe puede reaparecer.");
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Minigame"), 600L); // 30 segundos (600 ticks)
    }
}
