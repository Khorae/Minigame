package org.bukkit.craftbukkit.v1_20_R3.entity;

import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.CaveSpider;

public class CraftCaveSpider extends CraftSpider implements CaveSpider {
    public CraftCaveSpider(CraftServer server, net.minecraft.world.entity.monster.CaveSpider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.CaveSpider getHandle() {
        return (net.minecraft.world.entity.monster.CaveSpider) this.entity;
    }

    @Override
    public String toString() {
        return "CraftCaveSpider";
    }
}
