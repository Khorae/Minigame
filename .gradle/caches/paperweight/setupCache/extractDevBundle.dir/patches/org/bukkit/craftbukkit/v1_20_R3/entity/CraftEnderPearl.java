package org.bukkit.craftbukkit.v1_20_R3.entity;

import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.EnderPearl;

public class CraftEnderPearl extends CraftThrowableProjectile implements EnderPearl {
    public CraftEnderPearl(CraftServer server, ThrownEnderpearl entity) {
        super(server, entity);
    }

    @Override
    public ThrownEnderpearl getHandle() {
        return (ThrownEnderpearl) this.entity;
    }

    @Override
    public String toString() {
        return "CraftEnderPearl";
    }
}
