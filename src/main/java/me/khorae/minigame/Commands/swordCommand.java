package me.khorae.minigame.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class swordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta =sword.getItemMeta();

        meta.displayName(Component.text("Killer").color(NamedTextColor.DARK_PURPLE));
        meta.addEnchant(Enchantment.DAMAGE_ALL,255,true);
        sword.setItemMeta(meta);
        if (sender instanceof Player player){
        player.getInventory().addItem(sword);
        }
        return true;
    }
}
