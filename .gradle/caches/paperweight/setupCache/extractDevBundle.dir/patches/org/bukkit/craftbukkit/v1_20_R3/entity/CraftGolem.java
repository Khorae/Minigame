package org.bukkit.craftbukkit.v1_20_R3.entity;

import net.minecraft.world.entity.animal.AbstractGolem;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Golem;

public class CraftGolem extends CraftCreature implements Golem {
    public CraftGolem(CraftServer server, AbstractGolem entity) {
        super(server, entity);
    }

    @Override
    public AbstractGolem getHandle() {
        return (AbstractGolem) this.entity;
    }

    @Override
    public String toString() {
        return "CraftGolem";
    }
}
