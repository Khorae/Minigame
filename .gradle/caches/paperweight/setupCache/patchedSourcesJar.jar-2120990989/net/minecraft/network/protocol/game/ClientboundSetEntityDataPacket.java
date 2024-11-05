package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener> {
    public static final int EOF_MARKER = 255;

    public ClientboundSetEntityDataPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), unpack(buf));
    }

    private static void pack(List<SynchedEntityData.DataValue<?>> trackedValues, FriendlyByteBuf buf) {
        for (SynchedEntityData.DataValue<?> dataValue : trackedValues) {
            dataValue.write(buf);
        }

        buf.writeByte(255);
    }

    private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf buf) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

        int i;
        while ((i = buf.readUnsignedByte()) != 255) {
            list.add(SynchedEntityData.DataValue.read(buf, i));
        }

        return list;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        pack(this.packedItems, buf);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleSetEntityData(this);
    }
}
