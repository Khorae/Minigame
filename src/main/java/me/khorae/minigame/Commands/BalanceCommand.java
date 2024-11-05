package me.khorae.minigame.Commands;

import me.khorae.minigame.Managers.EconomyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public BalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int balance = economyManager.getBalance(player.getUniqueId());
            player.sendMessage("Tu balance actual es: " + balance + " monedas.");
            return true;
        } else {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return false;
        }
    }
}
