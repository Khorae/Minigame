package me.khorae.minigame.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ScoreboardManager {

    private final EconomyManager economyManager;
    private final HashMap<UUID, Integer> playerKills = new HashMap<>();
    private final File dataFile;
    private final FileConfiguration dataConfig;

    public ScoreboardManager(EconomyManager economyManager, File dataFolder) {
        this.economyManager = economyManager;

        // Inicializar archivo de datos para guardar las kills
        dataFile = new File(dataFolder, "kills.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        loadKills();
    }

    // Cargar las kills desde el archivo YAML
    private void loadKills() {
        for (String key : dataConfig.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            int kills = dataConfig.getInt(key);
            playerKills.put(playerId, kills);
        }
    }

    // Guardar las kills en el archivo YAML
    public void saveKills() {
        for (UUID playerId : playerKills.keySet()) {
            dataConfig.set(playerId.toString(), playerKills.get(playerId));
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Crear un scoreboard y asignarlo a un jugador
    public void createScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("stats", "dummy", ChatColor.GOLD + "Tus Estadísticas");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Crear líneas de texto para el scoreboard
        Score coinsScore = objective.getScore(ChatColor.YELLOW + "Monedas:");
        coinsScore.setScore(economyManager.getBalance(player.getUniqueId()));

        Score killsScore = objective.getScore(ChatColor.GREEN + "Mobs asesinados:");
        killsScore.setScore(playerKills.getOrDefault(player.getUniqueId(), 0));

        player.setScoreboard(board);
    }

    // Actualizar el scoreboard del jugador
    public void updateScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective("stats");
        if (objective == null) return;

        // Actualizar monedas y kills
        objective.getScore(ChatColor.YELLOW + "Monedas:")
                .setScore(economyManager.getBalance(player.getUniqueId()));
        objective.getScore(ChatColor.GREEN + "Mobs asesinados:")
                .setScore(playerKills.getOrDefault(player.getUniqueId(), 0));
    }

    // Incrementar las kills de un jugador
    public void addKill(Player player) {
        UUID playerId = player.getUniqueId();
        playerKills.put(playerId, playerKills.getOrDefault(playerId, 0) + 1);
        updateScoreboard(player);
    }
}
