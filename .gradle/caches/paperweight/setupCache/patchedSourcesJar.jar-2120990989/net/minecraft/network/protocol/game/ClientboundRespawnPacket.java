package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundRespawnPacket(CommonPlayerSpawnInfo commonPlayerSpawnInfo, byte dataToKeep) implements Packet<ClientGamePacketListener> {
    public static final byte KEEP_ATTRIBUTES = 1;
    public static final byte KEEP_ENTITY_DATA = 2;
    public static final byte KEEP_ALL_DATA = 3;

    public ClientboundRespawnPacket(FriendlyByteBuf buf) {
        this(new CommonPlayerSpawnInfo(buf), buf.readByte());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.commonPlayerSpawnInfo.write(buf);
        buf.writeByte(this.dataToKeep);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleRespawn(this);
    }

    public boolean shouldKeep(byte flag) {
        return (this.dataToKeep & flag) != 0;
    }
}
