/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_20_R3.block.impl;

public final class CraftWaterloggedTransparent extends org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData implements org.bukkit.block.data.Waterlogged {

    public CraftWaterloggedTransparent() {
        super();
    }

    public CraftWaterloggedTransparent(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_20_R3.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.WaterloggedTransparentBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return this.get(CraftWaterloggedTransparent.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        this.set(CraftWaterloggedTransparent.WATERLOGGED, waterlogged);
    }
}
