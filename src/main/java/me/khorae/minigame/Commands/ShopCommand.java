package me.khorae.minigame.Commands;

import me.khorae.minigame.ShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final ShopGUI shopGUI;

    public ShopCommand(ShopGUI shopGUI) {
        this.shopGUI = shopGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            shopGUI.openShop(player); // Abrir la tienda
            return true;
        } else {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return false;
        }
    }
}
