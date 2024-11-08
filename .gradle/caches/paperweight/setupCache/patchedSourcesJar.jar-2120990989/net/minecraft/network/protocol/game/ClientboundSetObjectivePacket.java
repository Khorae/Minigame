package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket implements Packet<ClientGamePacketListener> {
    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    private final String objectiveName;
    private final Component displayName;
    private final ObjectiveCriteria.RenderType renderType;
    @Nullable
    private final NumberFormat numberFormat;
    private final int method;

    public ClientboundSetObjectivePacket(Objective objective, int mode) {
        this.objectiveName = objective.getName();
        this.displayName = objective.getDisplayName();
        this.renderType = objective.getRenderType();
        this.numberFormat = objective.numberFormat();
        this.method = mode;
    }

    public ClientboundSetObjectivePacket(FriendlyByteBuf buf) {
        this.objectiveName = buf.readUtf();
        this.method = buf.readByte();
        if (this.method != 0 && this.method != 2) {
            this.displayName = CommonComponents.EMPTY;
            this.renderType = ObjectiveCriteria.RenderType.INTEGER;
            this.numberFormat = null;
        } else {
            this.displayName = buf.readComponentTrusted();
            this.renderType = buf.readEnum(ObjectiveCriteria.RenderType.class);
            this.numberFormat = buf.readNullable(NumberFormatTypes::readFromStream);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.objectiveName);
        buf.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            buf.writeComponent(this.displayName);
            buf.writeEnum(this.renderType);
            buf.writeNullable(this.numberFormat, NumberFormatTypes::writeToStream);
        }
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleAddObjective(this);
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public int getMethod() {
        return this.method;
    }

    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }

    @Nullable
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
}
