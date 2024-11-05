package me.khorae.minigame.Listeners;

import me.khorae.minigame.BossFight;
import me.khorae.minigame.CustomItems;
import me.khorae.minigame.Managers.EconomyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BossDeathListener implements Listener {
    private final BossFight bossFight;
    private final EconomyManager economyManager;
    public BossDeathListener(BossFight bossFight, EconomyManager economyManager) {
        this.bossFight = bossFight;
        this.economyManager = economyManager;
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        String bossName = ChatColor.stripColor("Zombie Boss");
        // Comprobar si el jefe tiene el nombre esperado como cadena de texto
        if (event.getEntity().getCustomName() != null &&
                ChatColor.stripColor(event.getEntity().getCustomName()).equals(bossName)) {

            // Limpiar los drops predeterminados
            event.getDrops().clear();

            // Crear los ítems que se dropearán
            ItemStack essence = CustomItems.getBossEssence();
            essence.setAmount(40);

            ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
            ItemMeta meta = sword.getItemMeta();
            meta.displayName(Component.text("Zombie Sword").color(NamedTextColor.DARK_GREEN));
            meta.addEnchant(Enchantment.DAMAGE_ALL, 20, true);
            sword.setItemMeta(meta);

            // Dropear los ítems en la ubicación del jefe
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), sword);
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.DIAMOND, 5));
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.STICK,1));
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), essence);

            // Mensaje global al morir el jefe
            Bukkit.broadcastMessage(ChatColor.GREEN + "This won't be the last time you hear from me...");

            // Actualizar BossBar para los jugadores si bossFight no es nulo
            if (bossFight != null) {
                Bukkit.getOnlinePlayers().forEach(player -> bossFight.addPlayerToBossBar(player));
            }

            if (event.getEntity().getKiller() != null) {
                Player killer = event.getEntity().getKiller();
                economyManager.addCoins(killer.getUniqueId(), 1000); // Otorgar 1000 monedas
                killer.sendMessage(ChatColor.GOLD + "¡Has recibido 1000 monedas por derrotar al jefe!");
            }

            // Limpiar los minions invocados por el jefe
            bossFight.clearMinions();
        }
    }
    @EventHandler
    public void onMinionDeath(EntityDeathEvent event){
        String minionName = ChatColor.stripColor("Minion");
        if (event.getEntity().getCustomName() != null &&
                ChatColor.stripColor(event.getEntity().getCustomName()).equals(minionName)) {

            if (event.getEntity().getKiller() != null) {
                Player killer = event.getEntity().getKiller();
                economyManager.addCoins(killer.getUniqueId(), 100); // Otorgar 100 monedas
                killer.sendMessage(ChatColor.GOLD + "¡Has recibido 100 monedas por derrotar un Minion!");
            }
        }
    }
}
