package org.bukkit.craftbukkit.v1_20_R3.entity;

import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Vehicle;

public abstract class CraftVehicle extends CraftEntity implements Vehicle {
    public CraftVehicle(CraftServer server, net.minecraft.world.entity.Entity entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftVehicle{passenger=" + this.getPassenger() + '}';
    }
}
