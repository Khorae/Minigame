package me.khorae.minigame;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class CustomItems {

    // Método para crear un ítem personalizado llamado "Esencia de Jefe"
    public static ItemStack getBossEssence() {
        ItemStack essence = new ItemStack(Material.NETHER_STAR);  // Item base como referencia
        ItemMeta meta = essence.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Esencia de Jefe");
            meta.setLore(java.util.Arrays.asList(
                    ChatColor.GRAY + "Un fragmento de energía poderosa.",
                    ChatColor.GRAY + "Drop raro de un jefe."
            ));
            essence.setItemMeta(meta);
        }
        return essence;
    }
    public static ItemStack getBossHelmet(){
        ItemStack customHelmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = customHelmet.getItemMeta();
        if (meta != null){
            meta.displayName(Component.text("Boss's Helmet"));
            meta.lore(java.util.Arrays.asList(
                    Component.text("Forged with the essence of the gods")
                            .color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields powers beyond normal")
                            .color(NamedTextColor.DARK_PURPLE)
            ));
            meta.addEnchant(Enchantment.DURABILITY,10,true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,5,true);
            meta.addEnchant(Enchantment.THORNS,4,true);
            customHelmet.setItemMeta(meta);

            ShapedRecipe recipe = new ShapedRecipe(customHelmet);
            recipe.shape(
                    "EEE",
                    "EzE",
                    "zzz"
            );
            recipe.setIngredient('E', new RecipeChoice.ExactChoice(CustomItems.getBossEssence()));

            Bukkit.addRecipe(recipe);
        }
        return customHelmet;
    }

    public static ItemStack getBossChest(){
        ItemStack customChest = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta meta = customChest.getItemMeta();
        if (meta != null){
            meta.displayName(Component.text("Boss's Chestplate"));
            meta.lore(Arrays.asList(
                    Component.text("Forged with the essence of the gods")
                            .color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields powers beyond normal")
                            .color(NamedTextColor.DARK_PURPLE)
            ));
            meta.addEnchant(Enchantment.DURABILITY,10,true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,5,true);
            meta.addEnchant(Enchantment.THORNS,4,true);
            customChest.setItemMeta(meta);

            ShapedRecipe recipe = new ShapedRecipe(customChest);
            recipe.shape(
                    "EzE",
                    "EEE",
                    "EEE"
            );
            recipe.setIngredient('E', new RecipeChoice.ExactChoice(CustomItems.getBossEssence()));

            Bukkit.addRecipe(recipe);
        }
        return customChest;
    }
    public static ItemStack getBossLegs(){
        ItemStack customLegs = new ItemStack(Material.NETHERITE_LEGGINGS);
        ItemMeta meta = customLegs.getItemMeta();
        if (meta != null){
            meta.displayName(Component.text("Boss's Leggings"));
            meta.lore(Arrays.asList(
                    Component.text("Forged with the essence of the gods")
                            .color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields powers beyond normal")
                            .color(NamedTextColor.DARK_PURPLE)
            ));
            meta.addEnchant(Enchantment.DURABILITY,10,true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,5,true);
            meta.addEnchant(Enchantment.THORNS,4,true);
            customLegs.setItemMeta(meta);

            ShapedRecipe recipe = new ShapedRecipe(customLegs);
            recipe.shape(
                    "EEE",
                    "EzE",
                    "EzE"
            );
            recipe.setIngredient('E', new RecipeChoice.ExactChoice(CustomItems.getBossEssence()));

            Bukkit.addRecipe(recipe);
        }
        return customLegs;
    }
    public static ItemStack getBossBoots(){
        ItemStack customBoots = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = customBoots.getItemMeta();
        if (meta != null){
            meta.displayName(Component.text("Boss's Helmet"));
            meta.lore(Arrays.asList(
                    Component.text("Forged with the essence of the gods").color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields powers beyond normal").color(NamedTextColor.DARK_PURPLE)
            ));
            meta.addEnchant(Enchantment.DURABILITY,10,true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,5,true);
            meta.addEnchant(Enchantment.THORNS,4,true);
            customBoots.setItemMeta(meta);

            ShapedRecipe recipe = new ShapedRecipe(customBoots);
            recipe.shape(
                    "zzz",
                    "EzE",
                    "EzE"
            );
            recipe.setIngredient('E', new RecipeChoice.ExactChoice(CustomItems.getBossEssence()));

            Bukkit.addRecipe(recipe);
        }
        return customBoots;
    }
    public static ItemStack getBossSword() {
        // Crear el ítem resultante del crafteo
        ItemStack customSword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = customSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Boss's Sword");
            meta.lore(Arrays.asList(
                    Component.text("Forged with the essence of the gods")
                            .color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields powers beyond normal")
                            .color(NamedTextColor.DARK_PURPLE)
            ));
            meta.addEnchant(Enchantment.DAMAGE_ALL,25,true);
            meta.addEnchant(Enchantment.DURABILITY,10,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
            customSword.setItemMeta(meta);
        }

        // Crear la receta personalizada
        ShapedRecipe recipe = new ShapedRecipe(customSword);
        recipe.shape(" E ", " E ", " S ");
        recipe.setIngredient('E', new RecipeChoice.ExactChoice(CustomItems.getBossEssence()));
        recipe.setIngredient('S', Material.STICK);

        // Registrar la receta
        Bukkit.addRecipe(recipe);
        return customSword;
    }
    public static ItemStack getSpawnerItem() {
        ItemStack spawner = new ItemStack(Material.SPAWNER); // Usar un ítem único como el spawner
        ItemMeta meta = spawner.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Spawner Personalizado");
            meta.setLore(java.util.Arrays.asList(
                    ChatColor.YELLOW + "Colócalo para activar un spawner",
                    ChatColor.LIGHT_PURPLE + "Generará mobs personalizados."
            ));
            meta.setUnbreakable(true);
            spawner.setItemMeta(meta);
        }
        return spawner;
    }
}

