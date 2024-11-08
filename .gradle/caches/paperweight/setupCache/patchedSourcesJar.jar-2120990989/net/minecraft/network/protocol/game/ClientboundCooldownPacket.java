package net.minecraft.network.protocol.game;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.Item;

public class ClientboundCooldownPacket implements Packet<ClientGamePacketListener> {
    private final Item item;
    private final int duration;

    public ClientboundCooldownPacket(Item item, int cooldown) {
        this.item = item;
        this.duration = cooldown;
    }

    public ClientboundCooldownPacket(FriendlyByteBuf buf) {
        this.item = buf.readById(BuiltInRegistries.ITEM);
        this.duration = buf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeId(BuiltInRegistries.ITEM, this.item);
        buf.writeVarInt(this.duration);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleItemCooldown(this);
    }

    public Item getItem() {
        return this.item;
    }

    public int getDuration() {
        return this.duration;
    }
}
