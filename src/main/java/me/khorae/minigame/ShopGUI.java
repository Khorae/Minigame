package me.khorae.minigame;

import me.khorae.minigame.Managers.EconomyManager;
import me.khorae.minigame.Managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopGUI implements Listener {

    private final EconomyManager economyManager;
    private final ScoreboardManager scoreboardManager;
    private final Inventory shopInventory;
    private final HashMap<Integer, Integer> itemPrices = new HashMap<>();

    public ShopGUI(EconomyManager economyManager, ScoreboardManager scoreboardManager) {
        this.economyManager = economyManager;
        this.scoreboardManager = scoreboardManager;
        this.shopInventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Tienda");

        initializeShopItems();
    }

    // Inicializar ítems en la tienda
    private void initializeShopItems() {
        addItemToShop(10, CustomShopItems.getGoblinSword(), 100);
        addItemToShop(12, CustomShopItems.getDiamondSword(), 20);
        addItemToShop(14, CustomShopItems.getRareSword(),200);
        addItemToShop(16, CustomShopItems.getLegendarySword(),500);
        addItemToShop(19, CustomShopItems.getGodSword(), 5000);
        addItemToShop(21, CustomShopItems.getKnockbackStick(), 200);
        addItemToShop(23, CustomShopItems.getGoblinApple(), 50);
        addItemToShop(25, CustomShopItems.getDemiGodApple(), 500);
        addItemToShop(28, CustomShopItems.getImmortalityApple(), 1000);
        addItemToShop(30, CustomShopItems.getBuffPotion(), 500);
        addItemToShop(32, CustomShopItems.getWarriorKit(), 1500);
        addItemToShop(34, CustomShopItems.getDemiGodKit(), 5000);
    }

    // Agregar un ítem a la tienda con su precio
    private void addItemToShop(int slot, ItemStack item, int price) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = (meta.hasLore() ? meta.getLore() : new ArrayList<>());
            lore.add(ChatColor.YELLOW + "Precio: " + price + " monedas"); // Añadir el precio al lore
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        shopInventory.setItem(slot, item);
        itemPrices.put(slot, price);
    }

    // Abrir la tienda para un jugador
    public void openShop(Player player) {
        player.openInventory(shopInventory);
    }

    // Manejar la compra de ítems en la tienda
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Tienda")) {
            event.setCancelled(true); // Evitar que los ítems se muevan directamente

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && itemPrices.containsKey(event.getRawSlot())) {
                int price = itemPrices.get(event.getRawSlot());

                if (economyManager.getBalance(player.getUniqueId()) >= price) {
                    economyManager.addCoins(player.getUniqueId(), -price);


                        player.getInventory().addItem(clickedItem.clone()); // Entregar ítem normal



                    economyManager.addCoins(player.getUniqueId(), -price);
                    scoreboardManager.updateScoreboard(player); // Actualizar el scoreboard
                    player.sendMessage(ChatColor.GREEN + "Has comprado " + clickedItem.getItemMeta().getDisplayName() +
                            " por " + price + " monedas.");
                } else {
                    player.sendMessage(ChatColor.RED + "No tienes suficientes monedas.");
                }
            }
        }
    }

    // Entregar los ítems del Kit Guerrero al jugador

}
