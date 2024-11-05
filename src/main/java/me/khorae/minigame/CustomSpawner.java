package me.khorae.minigame;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CustomSpawner {

    private final List<Location> spawnLocations;
    private final JavaPlugin plugin;
    private final List<EntityType> possibleMobs = Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CAVE_SPIDER,
            EntityType.ENDERMAN,
            EntityType.CREEPER,
            EntityType.WITCH
    );

    public CustomSpawner(List<Location> spawnLocations, JavaPlugin plugin) {
        this.spawnLocations = spawnLocations;
        this.plugin = plugin;
    }

    // Iniciar el spawner con un bucle repetitivo
    public void startSpawner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnCustomMobs(); // Generar mobs personalizados en varias ubicaciones
            }
        }.runTaskTimer(plugin, 0, 20 * 45); // Ejecutar cada 45 segundos
    }

    // Método para generar un mob personalizado
    private void spawnCustomMobs() {
        Random random = new Random();

        for (Location spawnLocation : spawnLocations) {
            EntityType mobType = possibleMobs.get(random.nextInt(possibleMobs.size()));
            Location spawnLoc = spawnLocation.clone().add(0, 0, 0); // Ajuste de altura si es necesario
            LivingEntity mob = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, mobType);

            switch (mobType) {
                case ZOMBIE -> configureZombie((Zombie) mob);
                case SKELETON -> configureSkeleton((Skeleton) mob);
                case CAVE_SPIDER -> configureCaveSpider((CaveSpider) mob);
                case ENDERMAN -> configureEnderman((Enderman) mob);
                case CREEPER -> configureCreeper((Creeper) mob);
                case WITCH -> configureWitch((Witch) mob);
            }
        }
    }

    // Configuración específica para el Zombie
    private void configureZombie(Zombie zombie) {
        zombie.customName(Component.text("Fighter Zombie"));
        zombie.setCustomNameVisible(true);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
        zombie.setHealth(40.0);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,1));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,1));

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta metaSword = sword.getItemMeta();
        if (metaSword != null) {
            metaSword.displayName(Component.text("Undead Sword").color(NamedTextColor.LIGHT_PURPLE));
            metaSword.addEnchant(Enchantment.DAMAGE_ALL,2,false);
            metaSword.addEnchant(Enchantment.SWEEPING_EDGE,1,false);
            metaSword.addEnchant(Enchantment.DURABILITY,1,false);
            sword.setItemMeta(metaSword);
        }
        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta metaChest = chest.getItemMeta();
        if (metaChest!=null){
            metaChest.displayName(Component.text("Undead Chestplate").color(NamedTextColor.LIGHT_PURPLE));
            metaChest.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2,false);
            metaChest.addEnchant(Enchantment.DURABILITY,1,false);
            chest.setItemMeta(metaChest);
        }
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemMeta metaBoots = boots.getItemMeta();
        if (metaBoots!=null){
            metaBoots.displayName(Component.text("Undead Boots").color(NamedTextColor.LIGHT_PURPLE));
            metaBoots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2,false);
            metaBoots.addEnchant(Enchantment.DURABILITY,1,false);
            metaBoots.addEnchant(Enchantment.PROTECTION_FALL,2,false);
            boots.setItemMeta(metaBoots);
        }
        ItemStack pants = new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta metaPants = pants.getItemMeta();
        if (metaPants!=null){
            metaPants.displayName(Component.text("Undead Pants").color(NamedTextColor.LIGHT_PURPLE));
            metaPants.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2,false);
            metaPants.addEnchant(Enchantment.DURABILITY,1,false);
            metaPants.addEnchant(Enchantment.THORNS,1,false);
        }
        zombie.getEquipment().setLeggings(pants);
        zombie.getEquipment().setChestplate(chest);
        zombie.getEquipment().setBoots(boots);
        zombie.getEquipment().setItemInMainHand(sword);
        zombie.getEquipment().setItemInMainHandDropChance(0.15f);
        zombie.getEquipment().setChestplateDropChance(0.15f);
        zombie.getEquipment().setBootsDropChance(0.15f);
        zombie.getEquipment().setLeggingsDropChance(0.15f);
    }

    // Configuración específica para el Skeleton
    private void configureSkeleton(Skeleton skeleton) {
        skeleton.customName(Component.text("Fast Skeleton"));
        skeleton.setCustomNameVisible(true);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
        skeleton.setHealth(35.0);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        skeleton.setArrowCooldown(15);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,1));

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta metaBow = bow.getItemMeta();
        if (metaBow!=null){
            metaBow.displayName(Component.text("Resilient Bow"));
            metaBow.addEnchant(Enchantment.ARROW_DAMAGE,2,false);
            metaBow.addEnchant(Enchantment.DURABILITY,2,false);
            metaBow.addEnchant(Enchantment.ARROW_INFINITE,1,true);
            bow.setItemMeta(metaBow);
        }
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta metaHelmet = helmet.getItemMeta();
        if (metaHelmet!=null){
            metaHelmet.displayName(Component.text("Boney Helmet"));
            metaHelmet.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,3,false);
            metaHelmet.addEnchant(Enchantment.DURABILITY,2,false);
            metaHelmet.addEnchant(Enchantment.PROTECTION_FALL,2,false);
            helmet.setItemMeta(metaHelmet);
        }
        skeleton.getEquipment().setItemInMainHand(bow);
        skeleton.getEquipment().setHelmet(helmet);
        skeleton.getEquipment().setItemInMainHandDropChance(0.10f);
        skeleton.getEquipment().setHelmetDropChance(0.15f);
    }

    // Configuración específica para el Spider
    private void configureCaveSpider(CaveSpider caveSpider) {
        caveSpider.customName(Component.text("Venomous Spider"));
        caveSpider.setCustomNameVisible(true);
        caveSpider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        caveSpider.setHealth(25);
        caveSpider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        caveSpider.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,1));
        caveSpider.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,1));
    }

    // Configuración específica para el Enderman
    private void configureEnderman(Enderman enderman) {
        enderman.customName(Component.text("Furious Enderman"));
        enderman.setCustomNameVisible(true);
        enderman.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70.0);
        enderman.setHealth(70.0);
        enderman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE,2));
        enderman.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,1));
        enderman.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,1));
        enderman.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,1));
    }

    // Configuración específica para el Creeper
    private void configureCreeper(Creeper creeper) {
        creeper.customName(Component.text("Kaboom Creeper").color(NamedTextColor.LIGHT_PURPLE));
        creeper.setCustomNameVisible(true);
        creeper.setPowered(true); // Creeper cargado para explosión más potente
        creeper.setExplosionRadius(12);
        creeper.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,Integer.MAX_VALUE,2));
        creeper.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,1));
    }

    // Configuración específica para la Witch
    private void configureWitch(Witch witch) {
        witch.customName(Component.text("Evil Witch").color(NamedTextColor.LIGHT_PURPLE));
        witch.setCustomNameVisible(true);
        witch.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(45.0);
        witch.setHealth(45.0);
        witch.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
        witch.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,Integer.MAX_VALUE,1));
        witch.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,Integer.MAX_VALUE,2));
        witch.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,Integer.MAX_VALUE,1));
    }
}
