package me.khorae.minigame.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

public class CustomEffectsListener implements Listener {
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.getItem().hasItemMeta()) {
            ItemMeta meta = event.getItem().getItemMeta();
            String itemName = ChatColor.stripColor(meta.getDisplayName());

            // Comparación con el nombre del ítem sin colores
            if (itemName.equalsIgnoreCase("Goblin Apple")) {
                applyGoblinAppleEffects(player);
                player.sendMessage(ChatColor.GREEN + "You gain a goblin's power");
            } else if (itemName.equalsIgnoreCase("God Apple")) {
                applyGodAppleEffects(player);
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "You gain the power of a God");
            } else if (itemName.equalsIgnoreCase("DemiGod Apple")) {
                applyDemiGodAppleEffects(player);
                player.sendMessage(ChatColor.DARK_PURPLE + "You gain the power of a Demi God");
            }
        }
    }
    private void applyGoblinAppleEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 1));
    }

    private void applyGodAppleEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 90, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 90, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 90, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 100, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 9));  // Instant effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 90, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 90, 9));
    }

    private void applyDemiGodAppleEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 120, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 120, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 120, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2));  // Instant effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 120, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 2));
    }
}