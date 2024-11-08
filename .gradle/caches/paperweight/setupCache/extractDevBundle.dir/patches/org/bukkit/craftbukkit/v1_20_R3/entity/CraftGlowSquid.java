package org.bukkit.craftbukkit.v1_20_R3.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.GlowSquid;

public class CraftGlowSquid extends CraftSquid implements GlowSquid {

    public CraftGlowSquid(CraftServer server, net.minecraft.world.entity.GlowSquid entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.GlowSquid getHandle() {
        return (net.minecraft.world.entity.GlowSquid) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftGlowSquid";
    }

    @Override
    public int getDarkTicksRemaining() {
        return this.getHandle().getDarkTicksRemaining();
    }

    @Override
    public void setDarkTicksRemaining(int darkTicksRemaining) {
        Preconditions.checkArgument(darkTicksRemaining >= 0, "darkTicksRemaining must be >= 0");
        this.getHandle().setDarkTicks(darkTicksRemaining);
    }
}
