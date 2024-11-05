package me.khorae.minigame;

import com.sun.jna.platform.win32.OaIdl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class CustomShopItems {
    public static ItemStack getGoblinSword(){
        ItemStack goblinSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta goblinSwordMeta = goblinSword.getItemMeta();
        if (goblinSwordMeta!=null){
            goblinSwordMeta.displayName(Component.text("Goblin Sword").color(NamedTextColor.GREEN));
            goblinSwordMeta.addEnchant(Enchantment.DAMAGE_ALL,2,false);
            goblinSwordMeta.addEnchant(Enchantment.SWEEPING_EDGE,1,false);
            goblinSwordMeta.lore(java.util.Arrays.asList(
                    Component.text("A goblin's weapon").color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields a great power").color(NamedTextColor.DARK_PURPLE)
            ));
            goblinSword.setItemMeta(goblinSwordMeta);
        }
        return goblinSword;
    }
    public static ItemStack getGoblinApple(){
        ItemStack goblinApple = new ItemStack(Material.APPLE);
        ItemMeta goblinAppleMeta = goblinApple.getItemMeta();
        if (goblinAppleMeta!=null){
            goblinAppleMeta.displayName(Component.text("Goblin Apple").color(NamedTextColor.GREEN));
            goblinAppleMeta.lore(java.util.Arrays.asList(
                    Component.text("You feel the power of a goblin").color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("Your body feels lighter and stronger").color(NamedTextColor.DARK_PURPLE)
            ));
            goblinAppleMeta.addEnchant(Enchantment.DURABILITY,1,false);
            goblinApple.setItemMeta(goblinAppleMeta);
        }
        return goblinApple;
    }
    public static ItemStack getDiamondSword(){
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta diamondSwordMeta = diamondSword.getItemMeta();
        if (diamondSwordMeta!=null){
            diamondSwordMeta.lore(java.util.Arrays.asList(
                    Component.text("Just a Diamond Sword").color(NamedTextColor.LIGHT_PURPLE)
            ));
            diamondSword.setItemMeta(diamondSwordMeta);
        }
        return diamondSword;
    }
    public static ItemStack getKnockbackStick(){
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta metaStick = stick.getItemMeta();
        if (metaStick!=null){
            metaStick.displayName(Component.text("Bye Bye Stick").color(NamedTextColor.GOLD));
            metaStick.lore(java.util.Arrays.asList(
               Component.text("Let them fly!").color(NamedTextColor.LIGHT_PURPLE),
               Component.text("Sends the taget far far away").color(NamedTextColor.DARK_PURPLE)
            ));
            metaStick.addEnchant(Enchantment.KNOCKBACK,255,true);
            stick.setItemMeta(metaStick);
        }
        return stick;
    }
    public static ItemStack getImmortalityApple(){
        ItemStack gapple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta metaGapple = gapple.getItemMeta();
        if (metaGapple!=null){
            metaGapple.displayName(Component.text("God Apple").color(NamedTextColor.GOLD).decorate(TextDecoration.ITALIC));
            metaGapple.setUnbreakable(true);
            metaGapple.lore(java.util.Arrays.asList(
               Component.text("Grants a god's immortality").color(NamedTextColor.LIGHT_PURPLE),
               Component.text("Briefly grants a power beyond mortality").color(NamedTextColor.DARK_PURPLE)
            ));
            gapple.setItemMeta(metaGapple);
        }
        return gapple;
    }
    public static ItemStack getBuffPotion(){
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta metaPotion = (PotionMeta) potion.getItemMeta();
        if (metaPotion!=null){
            metaPotion.displayName(Component.text("Buff Potion").color(NamedTextColor.GOLD));
            metaPotion.lore(java.util.Arrays.asList(
                    Component.text("Grants immeasurable strength!").color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("Limitless force for a short time").color(NamedTextColor.DARK_PURPLE)
            ));
            PotionEffect strengthEffect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20*10,254);
            metaPotion.addCustomEffect(strengthEffect,true);
            potion.setItemMeta(metaPotion);
        }
        return potion;
    }
    public static ItemStack getRareSword(){
        ItemStack rareSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = rareSword.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Rare Sword").color(NamedTextColor.GREEN));
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.DAMAGE_ALL,5,true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,3,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,1,false);
            AttributeModifier damageModifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "Custom Damage",
                    10,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,damageModifier);
            rareSword.setItemMeta(meta);
        }
        return rareSword;
    }
    public static ItemStack getLegendarySword(){
        ItemStack legendarySword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = legendarySword.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Legendary Sword"));
            meta.addEnchant(Enchantment.DAMAGE_ALL,6,true);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,4,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,3,true);
            AttributeModifier damageModifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "Custom Damage",
                    18,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,damageModifier);
            legendarySword.setItemMeta(meta);
        }
        return legendarySword;
    }
    public static ItemStack getGodSword(){
        ItemStack godSword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta godSwordMeta = godSword.getItemMeta();
        if (godSwordMeta!=null){
            godSwordMeta.displayName(Component.text("God Sword").color(NamedTextColor.GOLD).decorate(TextDecoration.ITALIC));
            godSwordMeta.lore(java.util.Arrays.asList(
               Component.text("A god's might in a blade").color(NamedTextColor.LIGHT_PURPLE),
               Component.text("Strike with the force of the gods!").color(NamedTextColor.DARK_PURPLE)
            ));
            godSwordMeta.addEnchant(Enchantment.DAMAGE_ALL,500,true);
            godSwordMeta.addEnchant(Enchantment.FIRE_ASPECT,500,true);
            godSwordMeta.addEnchant(Enchantment.SWEEPING_EDGE,500,true);
            godSwordMeta.addEnchant(Enchantment.LOOT_BONUS_MOBS,10,true);
            godSwordMeta.setUnbreakable(true);
            AttributeModifier damageModifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "Custom Damage",
                    500,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
            godSwordMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
            godSword.setItemMeta(godSwordMeta);
        }
        return godSword;
    }
    public static ItemStack getWarriorSword(){
        ItemStack warriorSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = warriorSword.getItemMeta();
        if (meta!=null){
            meta.displayName(Component.text("Warrior Sword").color(NamedTextColor.GRAY));
            meta.addEnchant(Enchantment.DAMAGE_ALL,1,false);
            meta.addEnchant(Enchantment.DURABILITY,1,false);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,2,false);
            warriorSword.setItemMeta(meta);
        }
        return warriorSword;
    }
    public static ItemStack getWarriorKit() {
        ItemStack kit = new ItemStack(Material.PAPER); // Representar el kit con un cofre
        ItemMeta meta = kit.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Warrior Kit").color(NamedTextColor.GRAY));
            meta.lore(java.util.Arrays.asList(
                    Component.text("Contents:").color(NamedTextColor.WHITE),
                    Component.text("- Warrior Sword").color(NamedTextColor.GRAY),
                    Component.text("- Warrior Armor Set").color(NamedTextColor.GRAY),
                    Component.text("¡Rain chaos on the battlefield!").color(NamedTextColor.DARK_PURPLE)
            ));
            kit.setItemMeta(meta);
        }
        return kit;
    }

    // Método auxiliar para obtener los ítems del kit
    public static List<ItemStack> getWarriorKitItems() {
        List<ItemStack> items = new ArrayList<>();
        items.add(getWarriorSword()); // Espada personalizada
        items.add(createArmorPieceWarrior(Material.IRON_HELMET, "Warrior Helmet"));
        items.add(createArmorPieceWarrior(Material.IRON_CHESTPLATE, "Warrior Chestplate"));
        items.add(createArmorPieceWarrior(Material.IRON_LEGGINGS, "Warrior Leggings"));
        items.add(createArmorPieceWarrior(Material.IRON_BOOTS, "Warrior Boots"));
        return items;
    }

    // Método auxiliar para crear piezas de armadura personalizada
    private static ItemStack createArmorPieceWarrior(Material material, String name) {
        ItemStack armorPiece = new ItemStack(material);
        ItemMeta meta = armorPiece.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name).color(NamedTextColor.GOLD));
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
            meta.addEnchant(Enchantment.DURABILITY,2,false);
            armorPiece.setItemMeta(meta);
        }
        return armorPiece;
    }
    public static ItemStack getDemiGodKit(){
        ItemStack kitDemiGod = new ItemStack(Material.PAPER);
        ItemMeta meta = kitDemiGod.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Demi-God Kit").color(NamedTextColor.DARK_PURPLE));
            meta.lore(java.util.Arrays.asList(
                    Component.text("Contents:").color(NamedTextColor.WHITE),
                    Component.text("- DemiGod Sword").color(NamedTextColor.GRAY),
                    Component.text("- DemiGod Armor Set").color(NamedTextColor.GRAY),
                    Component.text("¡Become a DemiGod!").color(NamedTextColor.DARK_PURPLE)
            ));
            kitDemiGod.setItemMeta(meta);
        }
        return kitDemiGod;
    }

    public static List<ItemStack> getDemiGodItems(){
        List<ItemStack> items = new ArrayList<>();
        items.add(getDemiGodSword());
        items.add(getDemiGodApple());
        items.add(getDemiGodApple());
        items.add(getDemiGodApple());
        items.add(getDemiGodApple());
        items.add(getDemiGodApple());
        items.add(createArmorPieceDemiGod(Material.DIAMOND_HELMET,"DemiGod Helmet"));
        items.add(createArmorPieceDemiGod(Material.DIAMOND_CHESTPLATE,"DemiGod Chestplate"));
        items.add(createArmorPieceDemiGod(Material.DIAMOND_LEGGINGS,"DemiGod Leggings"));
        items.add(createArmorPieceDemiGod(Material.DIAMOND_BOOTS,"DemiGod Boots"));
        return items;
    }
    public static ItemStack getDemiGodSword(){
        ItemStack demiGodSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = demiGodSword.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("DemiGod Sword"));
            meta.addEnchant(Enchantment.DAMAGE_ALL,7,true);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,5,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS,3,true);
            demiGodSword.setItemMeta(meta);
        }
        return demiGodSword;
    }
    private static ItemStack createArmorPieceDemiGod(Material material, String name){
        ItemStack armorPiece = new ItemStack(material);
        ItemMeta meta = armorPiece.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name).color(NamedTextColor.DARK_PURPLE));
            meta.addEnchant(Enchantment.DURABILITY,5,true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,6,true);
            meta.addEnchant(Enchantment.THORNS,3,true);
            armorPiece.setItemMeta(meta);
        }
        return armorPiece;
    }
    public static ItemStack getDemiGodApple(){
        ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = apple.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("DemiGod Apple").color(NamedTextColor.DARK_PURPLE));
            meta.lore(java.util.Arrays.asList(
               Component.text("A DemiGod's Snack").color(NamedTextColor.LIGHT_PURPLE),
               Component.text("Grants a DemiGod's powers").color(NamedTextColor.LIGHT_PURPLE)
            ));
            apple.setItemMeta(meta);
        }
        return apple;
    }
}
