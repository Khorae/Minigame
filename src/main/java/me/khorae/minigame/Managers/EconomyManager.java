package me.khorae.minigame.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class EconomyManager {

    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final HashMap<UUID, Integer> playerBalances = new HashMap<>();

    public EconomyManager(File dataFolder) {
        dataFile = new File(dataFolder, "economy.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadBalances();
    }

    // Cargar los balances desde el archivo
    private void loadBalances() {
        for (String key : dataConfig.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            int balance = dataConfig.getInt(key);
            playerBalances.put(playerId, balance);
        }
    }

    // Guardar los balances en el archivo
    public void saveBalances() {
        for (UUID playerId : playerBalances.keySet()) {
            dataConfig.set(playerId.toString(), playerBalances.get(playerId));
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Obtener el balance de un jugador
    public int getBalance(UUID playerId) {
        return playerBalances.getOrDefault(playerId, 0);
    }

    // Añadir monedas a un jugador
    public void addCoins(UUID playerId, int amount) {
        int currentBalance = getBalance(playerId);
        playerBalances.put(playerId, currentBalance + amount);
    }
}
