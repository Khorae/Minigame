/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_20_R3.block.impl;

public final class CraftSnow extends org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData implements org.bukkit.block.data.type.Snow {

    public CraftSnow() {
        super();
    }

    public CraftSnow(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_20_R3.block.data.type.CraftSnow

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty LAYERS = getInteger(net.minecraft.world.level.block.SnowLayerBlock.class, "layers");

    @Override
    public int getLayers() {
        return this.get(CraftSnow.LAYERS);
    }

    @Override
    public void setLayers(int layers) {
        this.set(CraftSnow.LAYERS, layers);
    }

    @Override
    public int getMinimumLayers() {
        return getMin(CraftSnow.LAYERS);
    }

    @Override
    public int getMaximumLayers() {
        return getMax(CraftSnow.LAYERS);
    }
}
