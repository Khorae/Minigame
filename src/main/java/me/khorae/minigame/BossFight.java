package me.khorae.minigame;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;
public class BossFight {
    private final JavaPlugin plugin;
    private BossBar bossBar;
    private Zombie boss;
    private boolean secondPhase = false;
    private final List<Zombie> minions = new ArrayList<>();

    public BossFight(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnBoss(Location location){
        if (boss!=null && !boss.isDead()){
            boss.remove();
        }

        // Limpiar cualquier estado anterior (si es necesario)
        clearMinions();
        secondPhase = false;  // Reiniciar el estado de la segunda fase

        World world = location.getWorld();
        if (world==null) return;

        boss = (Zombie)world.spawnEntity(location, EntityType.ZOMBIE);
        boss.customName(Component.text("Zombie Boss").color(NamedTextColor.GREEN));
        boss.setCustomNameVisible(true);
        boss.setMaxHealth(200);
        boss.setHealth(200);
        boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false,false,false));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2, false,false,false));
        boss.setBaby(false);
        boss.setRemoveWhenFarAway(false);

        equipBoss(boss);

        bossBar = Bukkit.createBossBar("Zombie Boss", BarColor.GREEN, BarStyle.SEGMENTED_10);
        bossBar.setVisible(true);

        Bukkit.getOnlinePlayers().forEach(this::addPlayerToBossBar);


        startBossBarUpdater();
        startSpecialAbilities();
    }
    private void equipBoss(Zombie boss) {
        boss.getEquipment().setHelmet(CustomItems.getBossHelmet());
        boss.getEquipment().setChestplate(CustomItems.getBossChest());
        boss.getEquipment().setLeggings(CustomItems.getBossLegs());
        boss.getEquipment().setBoots(CustomItems.getBossBoots());
        boss.getEquipment().setItemInMainHand(CustomItems.getBossSword());

        // Evitar que los ítems equipados caigan al morir
        boss.getEquipment().setHelmetDropChance(0.0f);
        boss.getEquipment().setChestplateDropChance(0.0f);
        boss.getEquipment().setLeggingsDropChance(0.0f);
        boss.getEquipment().setBootsDropChance(0.0f);
        boss.getEquipment().setItemInMainHandDropChance(0.0f);
    }
    private void startBossBarUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (boss == null || boss.isDead()) {
                    // El jefe ha muerto, eliminar la BossBar
                    bossBar.removeAll();
                    cancel(); // Detener la tarea
                    return;
                }

                // Actualizar el progreso de la BossBar basado en la vida del jefe
                double healthPercentage = boss.getHealth() / boss.getMaxHealth();
                bossBar.setProgress(healthPercentage);

                if (healthPercentage <= 0.5 && !secondPhase) {
                    activateSecondPhase();
                }
            }
        }.runTaskTimer(plugin, 0, 1);  // Ejecutar cada segundo (20 ticks)
    }
    public void addPlayerToBossBar(Player player) {
        if (player != null && bossBar != null) {
            bossBar.addPlayer(player);  // Solo añadir si el jugador no es null
        }
    }
    public boolean isBossNearby(Player player) {
        if (boss == null || boss.isDead()) return false;
        return boss.getLocation().distance(player.getLocation()) < 20; // Rango de 20 bloques
    }

    // Activar la segunda fase del jefe
    private void activateSecondPhase() {
        secondPhase = true;  // Marcar que estamos en la segunda fase

        // Cambiar la BossBar a un color más amenazante
        bossBar.setColor(BarColor.PURPLE);
        bossBar.setTitle("§5Zombie Boss");

        // Darle nuevos efectos al jefe para hacerlo más difícil
        boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));

        // Mensaje global para los jugadores
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "I'M NOT DONE YET!");
        summonMinions(boss.getLocation());

    }
    private void startSpecialAbilities(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if (boss==null || boss.isDead()){
                    cancel();
                    return;
                }

                boss.getNearbyEntities(10,5,10).stream()
                        .filter(entity -> entity instanceof LivingEntity)
                        .forEach(entity -> {
                            LivingEntity target = (LivingEntity) entity;
                            target.setVelocity(target.getVelocity().add(target.getLocation().getDirection().multiply(2)));
                            target.sendMessage(ChatColor.GREEN + "Vuela miserable humano");
                        });
            }
        }.runTaskTimer(plugin,0,200);
    }
    private void summonMinions(Location location) {
        Bukkit.broadcastMessage(ChatColor.RED + "¡El Jefe Zombi ha invocado a sus secuaces!");

        for (int i = 0; i < 5; i++) {
            Location spawnLocation = location.clone().add(i - 2, 0, i - 2);  // Posición aleatoria alrededor del jefe
            Zombie minion = (Zombie) location.getWorld().spawn(spawnLocation, Zombie.class);

            minion.customName(Component.text("Minion").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
            minion.setCustomNameVisible(true);
            minion.setMaxHealth(50.0);
            minion.setHealth(50.0);

            // Dar efectos a los secuaces para hacerlos más difíciles
            minion.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
            minion.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            minion.setBaby(false);
            equipMinion(minion);


            minions.add(minion);  // Añadir el secuaz a la lista
        }
    }
    private void equipMinion(Zombie minion) {
        // Crear ítems para equipar
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);

        ItemMeta metaWeapon = weapon.getItemMeta();
        metaWeapon.addEnchant(Enchantment.DAMAGE_ALL,2,false);
        metaWeapon.addEnchant(Enchantment.DURABILITY,2,false);
        weapon.setItemMeta(metaWeapon);

        ItemMeta metaHelmet = helmet.getItemMeta();
        metaHelmet.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
        metaHelmet.addEnchant(Enchantment.DURABILITY,2,false);
        helmet.setItemMeta(metaHelmet);

        ItemMeta metaChest = chestplate.getItemMeta();
        metaChest.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
        metaChest.addEnchant(Enchantment.DURABILITY,2,false);
        helmet.setItemMeta(metaChest);

        ItemMeta metaLegs = leggings.getItemMeta();
        metaLegs.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
        metaLegs.addEnchant(Enchantment.DURABILITY,2,false);
        helmet.setItemMeta(metaLegs);

        ItemMeta metaBoots = boots.getItemMeta();
        metaBoots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1,false);
        metaBoots.addEnchant(Enchantment.DURABILITY,2,false);
        helmet.setItemMeta(metaBoots);

        // Equipar al zombie con los ítems
        minion.getEquipment().setHelmet(helmet);
        minion.getEquipment().setChestplate(chestplate);
        minion.getEquipment().setLeggings(leggings);
        minion.getEquipment().setBoots(boots);
        minion.getEquipment().setItemInMainHand(weapon);

        // Evitar que los ítems caigan al morir el secuaz
        minion.getEquipment().setHelmetDropChance(0.0f);
        minion.getEquipment().setChestplateDropChance(0.0f);
        minion.getEquipment().setLeggingsDropChance(0.0f);
        minion.getEquipment().setBootsDropChance(0.0f);
        minion.getEquipment().setItemInMainHandDropChance(0.0f);
    }
    public void clearMinions() {
        for (Zombie minion : minions) {
            if (minion != null && minion.isValid()) {  // Verificar si el minion sigue existiendo en el mundo
                minion.remove();  // Eliminar el minion del mundo
            }
        }
        minions.clear();  // Vaciar la lista para evitar referencias innecesarias
    }
    public void despawnBoss() {
        if (boss != null && !boss.isDead()) {
            boss.remove(); // Desaparecer al jefe
            boss = null;
        }
    }
}