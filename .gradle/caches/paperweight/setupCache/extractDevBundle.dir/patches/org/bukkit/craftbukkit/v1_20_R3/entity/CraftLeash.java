package org.bukkit.craftbukkit.v1_20_R3.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.LeashHitch;

public class CraftLeash extends CraftHanging implements LeashHitch {
    public CraftLeash(CraftServer server, LeashFenceKnotEntity entity) {
        super(server, entity);
    }

    @Override
    public boolean setFacingDirection(BlockFace face, boolean force) {
        Preconditions.checkArgument(face == BlockFace.SELF, "%s is not a valid facing direction", face);

        return force || this.getHandle().generation || this.getHandle().survives();
    }

    @Override
    public BlockFace getFacing() {
        // Leash hitch has no facing direction, so we return self
        return BlockFace.SELF;
    }

    @Override
    public LeashFenceKnotEntity getHandle() {
        return (LeashFenceKnotEntity) this.entity;
    }

    @Override
    public String toString() {
        return "CraftLeash";
    }
}
