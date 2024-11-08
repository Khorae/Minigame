package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public abstract class CraftLootable<T extends RandomizableContainerBlockEntity> extends CraftContainer<T> implements Nameable, Lootable, com.destroystokyo.paper.loottable.PaperLootableBlockInventory { // Paper

    public CraftLootable(World world, T tileEntity) {
        super(world, tileEntity);
    }

    protected CraftLootable(CraftLootable<T> state) {
        super(state);
    }

    @Override
    public void applyTo(T lootable) {
        super.applyTo(lootable);

        if (this.getSnapshot().lootTable == null) {
            lootable.setLootTable((ResourceLocation) null, 0L);
        }
    }

    @Override
    public LootTable getLootTable() {
        if (this.getSnapshot().lootTable == null) {
            return null;
        }

        ResourceLocation key = this.getSnapshot().lootTable;
        return Bukkit.getLootTable(CraftNamespacedKey.fromMinecraft(key));
    }

    @Override
    public void setLootTable(LootTable table) {
        this.setLootTable(table, this.getSeed());
    }

    @Override
    public long getSeed() {
        return this.getSnapshot().lootTableSeed;
    }

    @Override
    public void setSeed(long seed) {
        this.setLootTable(this.getLootTable(), seed);
    }

    public void setLootTable(LootTable table, long seed) {
        ResourceLocation key = (table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey());
        this.getSnapshot().setLootTable(key, seed);
    }

    @Override
    public abstract CraftLootable<T> copy();
}
