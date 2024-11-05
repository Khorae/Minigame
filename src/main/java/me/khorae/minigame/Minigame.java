package me.khorae.minigame;

import me.khorae.minigame.Commands.*;
import me.khorae.minigame.Listeners.*;
import me.khorae.minigame.Managers.EconomyManager;
import me.khorae.minigame.Managers.ScoreboardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Minigame extends JavaPlugin {

    private World arena;
    private BossFight bossFight;
    private EconomyManager economyManager;
    private ShopGUI shopGUI;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {

        // Crear o cargar el mundo
        loadOrCreateArenaWorld();

        bossFight = new BossFight(this);
        BossAreaListener bossAreaListener = new BossAreaListener(
                bossFight, new Location(arena, 20, 6, -149), 19, 6, -122, 21, 10, -122
        );
        economyManager = new EconomyManager(getDataFolder());
        scoreboardManager = new ScoreboardManager(economyManager,getDataFolder());
        shopGUI = new ShopGUI(economyManager,scoreboardManager);
        List<Location> mobSpawnLocations = Arrays.asList(
                new Location(Bukkit.getWorld("Arena"),0,-63,3),
                new Location(Bukkit.getWorld("Arena"),0,-63,0),
                new Location(Bukkit.getWorld("Arena"),-7,-63,-1),
                new Location(Bukkit.getWorld("Arena"),-12,-63,-25),
                new Location(Bukkit.getWorld("Arena"),0,-63,-24),
                new Location(Bukkit.getWorld("Arena"),0,-63,-12),
                new Location(Bukkit.getWorld("Arena"),18,-62,-15),
                new Location(Bukkit.getWorld("Arena"),22,-62,-23),
                new Location(Bukkit.getWorld("Arena"),33,-60,-20),
                new Location(Bukkit.getWorld("Arena"),44,-59,-4),
                new Location(Bukkit.getWorld("Arena"),40,-58,0),
                new Location(Bukkit.getWorld("Arena"),26,-53,5),
                new Location(Bukkit.getWorld("Arena"),16,-50,6),
                new Location(Bukkit.getWorld("Arena"),16,-50,1),
                new Location(Bukkit.getWorld("Arena"),4,-47,4),
                new Location(Bukkit.getWorld("Arena"),-5,-44,4),
                new Location(Bukkit.getWorld("Arena"),-11,-44,6),
                new Location(Bukkit.getWorld("Arena"),-19,-44,2),
                new Location(Bukkit.getWorld("Arena"),-29,-44,-7),
                new Location(Bukkit.getWorld("Arena"),-30,-44,-8),
                new Location(Bukkit.getWorld("Arena"),-23,-44,-16),
                new Location(Bukkit.getWorld("Arena"),-2,-44,-14),
                new Location(Bukkit.getWorld("Arena"),-3,-44,-13),
                new Location(Bukkit.getWorld("Arena"),-7,-44,-7),
                new Location(Bukkit.getWorld("Arena"),22,-42,-100),
                new Location(Bukkit.getWorld("Arena"),22,-42,-97),
                new Location(Bukkit.getWorld("Arena"),28,-42,-98),
                new Location(Bukkit.getWorld("Arena"),40,-42,-98),
                new Location(Bukkit.getWorld("Arena"),42,-42,-92),
                new Location(Bukkit.getWorld("Arena"),52,-42,-93),
                new Location(Bukkit.getWorld("Arena"),62,-38,-78),
                new Location(Bukkit.getWorld("Arena"),68,-38,-75),
                new Location(Bukkit.getWorld("Arena"),70,-38,-69),
                new Location(Bukkit.getWorld("Arena"),75,-38,-64),
                new Location(Bukkit.getWorld("Arena"),74,-38,-55),
                new Location(Bukkit.getWorld("Arena"),76,-38,-49),
                new Location(Bukkit.getWorld("Arena"),73,-38,-46),
                new Location(Bukkit.getWorld("Arena"),62,-32,-41),
                new Location(Bukkit.getWorld("Arena"),52,-32,-50),
                new Location(Bukkit.getWorld("Arena"),53,-32,-44),
                new Location(Bukkit.getWorld("Arena"),40,-28,-50),
                new Location(Bukkit.getWorld("Arena"),32,-26,-48),
                new Location(Bukkit.getWorld("Arena"),26,-23,-53),
                new Location(Bukkit.getWorld("Arena"),25,-16,-72),
                new Location(Bukkit.getWorld("Arena"),25,-12,-83),
                new Location(Bukkit.getWorld("Arena"),30,-12,-78)
                // Añade tantas ubicaciones como desees
        );
        CustomSpawner customSpawner = new CustomSpawner(mobSpawnLocations, this);
        customSpawner.startSpawner();

        if (arena==null){
            System.out.println("El mundo 'Arena' no se cargo correctamente");
            return;
        }


        //Listeners
        Bukkit.getPluginManager().registerEvents(new WorldLoadListener(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(arena,bossFight,scoreboardManager), this);
        Bukkit.getPluginManager().registerEvents(new BossDeathListener(bossFight,economyManager), this);
        Bukkit.getPluginManager().registerEvents(new EconomyListener(economyManager,scoreboardManager),this);
        Bukkit.getPluginManager().registerEvents(shopGUI,this);
        Bukkit.getPluginManager().registerEvents(new ExplosionProtectorListener(),this);
        Bukkit.getPluginManager().registerEvents(bossAreaListener,this);
        Bukkit.getPluginManager().registerEvents(new FightEndListener(bossAreaListener),this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(),this);
        Bukkit.getPluginManager().registerEvents(new CustomEffectsListener(),this);
        Bukkit.getPluginManager().registerEvents(new KitUseListener(),this);


        //Comandos
        getCommand("sword").setExecutor(new swordCommand());
        getCommand("spawnboss").setExecutor(new spawnBossCommand(bossFight));
        getCommand("loadarena").setExecutor(new loadArenaWorldCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(economyManager));
        getCommand("shop").setExecutor(new ShopCommand(shopGUI));
        getCommand("getspawner").setExecutor(new SpawnerCommand());
        getCommand("clearbossbars").setExecutor(new ClearBossBarsCommand());

        //Recetas personaliszadas
        addCustomRecipeBossEssenceSword();
        addCustomRecipeBossEssenceHelmet();
        addCustomRecipeBossEssenceChestplate();
        addCustomRecipeBossEssenceLeggings();
        addCustomRecipeBossEssenceBoots();
    }


    private void addCustomRecipeBossEssenceSword() {
        // Crear el ítem resultante del crafteo
        ItemStack customSword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = customSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Boss's Sword");
            meta.lore(java.util.Arrays.asList(
                    Component.text("Forged with the essence of the gods")
                            .color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("It wields powers beyond normal")
                            .color(NamedTextColor.DARK_PURPLE)
            ));
            meta.addEnchant(Enchantment.DAMAGE_ALL,25,true);
            meta.addEnchant(Enchantment.DURABILITY,10,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
            AttributeModifier damageModifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "Custom Damage",
                    30,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            );
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
            customSword.setItemMeta(meta);
        }

        // Crear la receta personalizada
        ShapedRecipe recipe = new ShapedRecipe(customSword);
        recipe.shape(" E ", " E ", " S ");
        recipe.setIngredient('E', new RecipeChoice.ExactChoice(CustomItems.getBossEssence()));
        recipe.setIngredient('S', Material.STICK);

        // Registrar la receta
        Bukkit.addRecipe(recipe);
    }

    private void addCustomRecipeBossEssenceHelmet(){
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
    }

    private void addCustomRecipeBossEssenceChestplate(){
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
    }
    private void addCustomRecipeBossEssenceLeggings(){
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
    }
    private void addCustomRecipeBossEssenceBoots(){
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
    }

    public void loadOrCreateArenaWorld() {
        arena = Bukkit.getWorld("Arena");
        if (arena == null) {
            getLogger().info("El mundo 'Arena' no está cargado. Intentando cargarlo manualmente...");
            WorldCreator creator = new WorldCreator("Arena");
            creator.type(WorldType.FLAT);
            creator.generateStructures(false);
            arena = creator.createWorld();

            if (arena == null) {
                getLogger().severe("¡Error! No se pudo cargar o crear el mundo 'Arena'.");
            } else {
                getLogger().info("El mundo 'Arena' ha sido cargado o creado correctamente.");
            }
        } else {
            getLogger().info("El mundo 'Arena' ya está cargado.");
        }
    }

    @Override
    public void onDisable() {
        // Lógica para el cierre del plugin
        economyManager.saveBalances();
        scoreboardManager.saveKills();
        System.out.println("El plugin se ha deshabilitado");
    }
}