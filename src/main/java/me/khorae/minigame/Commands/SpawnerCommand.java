package me.khorae.minigame.Commands;

import me.khorae.minigame.CustomItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            // Dar el spawner al jugador
            player.getInventory().addItem(CustomItems.getSpawnerItem());
            player.sendMessage("¡Has recibido un Spawner Personalizado!");
            return true;
        } else {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return false;
        }
    }
}
