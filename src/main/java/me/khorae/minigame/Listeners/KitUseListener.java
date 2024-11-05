package me.khorae.minigame.Listeners;

import me.khorae.minigame.CustomShopItems;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KitUseListener implements Listener {

    @EventHandler
    public void onPlayerUseKit(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return; // Asegurar que sea clic derecho

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        // Verificar el nombre del ítem para determinar si es un kit
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (itemName.equals("Warrior Kit")) {
            // Entregar el Warrior Kit
            for (ItemStack kitItem : CustomShopItems.getWarriorKitItems()) {
                player.getInventory().addItem(kitItem);
            }
            player.sendMessage(ChatColor.GREEN + "¡Has recibido el Warrior Kit!");
            item.setAmount(0); // Eliminar el papel del kit
        } else if (itemName.equals("Demi-God Kit")) {
            // Entregar el Demi-God Kit
            for (ItemStack kitItem : CustomShopItems.getDemiGodItems()) {
                player.getInventory().addItem(kitItem);
            }
            player.sendMessage(ChatColor.GREEN + "¡Has recibido el Demi-God Kit!");
            item.setAmount(0); // Eliminar el papel del kit
        }
    }
}
