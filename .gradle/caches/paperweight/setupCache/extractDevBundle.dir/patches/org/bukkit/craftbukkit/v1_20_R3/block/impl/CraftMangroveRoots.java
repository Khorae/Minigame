/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_20_R3.block.impl;

public final class CraftMangroveRoots extends org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData implements org.bukkit.block.data.Waterlogged {

    public CraftMangroveRoots() {
        super();
    }

    public CraftMangroveRoots(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_20_R3.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.MangroveRootsBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return this.get(CraftMangroveRoots.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        this.set(CraftMangroveRoots.WATERLOGGED, waterlogged);
    }
}
