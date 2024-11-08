package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetActionBarTextPacket implements Packet<ClientGamePacketListener> {
    private final Component text;
    public net.kyori.adventure.text.Component adventure$text; // Paper
    public net.md_5.bungee.api.chat.BaseComponent[] components; // Paper

    public ClientboundSetActionBarTextPacket(Component message) {
        this.text = message;
    }

    public ClientboundSetActionBarTextPacket(FriendlyByteBuf buf) {
        this.text = buf.readComponentTrusted();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        // Paper start
        if (this.adventure$text != null) {
            buf.writeComponent(this.adventure$text);
        } else if (this.components != null) {
            buf.writeComponent(this.components);
        } else
        // Paper end
        buf.writeComponent(this.text);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.setActionBarText(this);
    }

    public Component getText() {
        return this.text;
    }
}
