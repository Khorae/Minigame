package org.bukkit.craftbukkit.v1_20_R3.entity;

import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Cow;

public class CraftCow extends CraftAnimals implements Cow {

    public CraftCow(CraftServer server, net.minecraft.world.entity.animal.Cow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.Cow getHandle() {
        return (net.minecraft.world.entity.animal.Cow) this.entity;
    }

    @Override
    public String toString() {
        return "CraftCow";
    }
}
