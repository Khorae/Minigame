package me.khorae.minigame.Commands;

import me.khorae.minigame.BossFight;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class spawnBossCommand implements CommandExecutor {

    private final BossFight bossFight;

    public spawnBossCommand(BossFight bossFight) {
        this.bossFight = bossFight;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            bossFight.spawnBoss(location);
            player.sendMessage("¡El jefe ha aparecido!");
            return true;
        } else {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return false;
        }
    }
}

