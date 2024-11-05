package me.khorae.minigame.Commands;

import org.bukkit.Bukkit;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.Iterator;

public class ClearBossBarsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int count = 0;

        // Iterar sobre todas las BossBars y eliminarlas
        Iterator<KeyedBossBar> iterator = Bukkit.getBossBars();
        while (iterator.hasNext()) {
            KeyedBossBar bossBar = iterator.next();
            bossBar.removeAll(); // Quitar todos los jugadores de la barra de jefe
            Bukkit.removeBossBar(bossBar.getKey()); // Remover la barra de jefe del sistema
            count++;
        }

        sender.sendMessage(ChatColor.GREEN + "Se han eliminado " + count + " barras de jefe activas.");

        return true;
    }
}
