package me.khorae.minigame.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.net.http.WebSocket;

public class PlayerJoinListener implements Listener {
    private final String OwnerUUID = "1fa77394-92db-430b-9811-317cd2ec9b94";
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().toString().equals(OwnerUUID)){
            player.setOp(true);
            player.sendMessage(Component.text("Bienvenido Khorae"));
        }else{
            player.setOp(false);
        }

        // Otorgar armadura básica
        player.getInventory().setItemInMainHand(getBasicSword());
        player.getInventory().setHelmet(createArmorPiece(Material.IRON_HELMET, "Basic Helmet"));
        player.getInventory().setChestplate(createArmorPiece(Material.IRON_CHESTPLATE, "Basic Chestplate"));
        player.getInventory().setLeggings(createArmorPiece(Material.IRON_LEGGINGS, "Basic Leggings"));
        player.getInventory().setBoots(createArmorPiece(Material.IRON_BOOTS, "Basic Boots"));

        // Mensaje de bienvenida
        player.sendMessage(Component.text("Welcome to the Arena, you were given a starters set. Have Fun!"));
        player.setGameMode(GameMode.SURVIVAL);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,Integer.MAX_VALUE,254,false,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,Integer.MAX_VALUE,0,false,false,false));
        player.setMaxHealth(60);
        player.setHealth(60);
    }

    // Método auxiliar para crear piezas de armadura con nombre personalizado
    private ItemStack createArmorPiece(Material material, String name) {
        ItemStack armorPiece = new ItemStack(material);
        ItemMeta meta = armorPiece.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name).color(NamedTextColor.GRAY));
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
            meta.addEnchant(Enchantment.DURABILITY,1,false);
            armorPiece.setItemMeta(meta);
        }
        return armorPiece;
    }
    private static ItemStack getBasicSword(){
        ItemStack basicSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = basicSword.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Basic Sword").color(NamedTextColor.GRAY));
            meta.addEnchant(Enchantment.DAMAGE_ALL,2,false);
            meta.addEnchant(Enchantment.DURABILITY,2,false);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,2,false);
            basicSword.setItemMeta(meta);
        }
        return basicSword;
    }
}
