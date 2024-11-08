package org.bukkit.craftbukkit.v1_20_R3.entity;

import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.entity.EntityRemoveEvent;

public class CraftWindCharge extends CraftFireball implements WindCharge {
    public CraftWindCharge(CraftServer server, net.minecraft.world.entity.projectile.WindCharge entity) {
        super(server, entity);
    }

    @Override
    public void explode() {
        this.getHandle().explode();
        this.getHandle().discard(EntityRemoveEvent.Cause.EXPLODE); // SPIGOT-7577 - explode doesn't discard the entity, this happens only in tick and onHitBlock
    }

    @Override
    public net.minecraft.world.entity.projectile.WindCharge getHandle() {
        return (net.minecraft.world.entity.projectile.WindCharge) this.entity;
    }

    @Override
    public String toString() {
        return "CraftWindCharge";
    }
}
