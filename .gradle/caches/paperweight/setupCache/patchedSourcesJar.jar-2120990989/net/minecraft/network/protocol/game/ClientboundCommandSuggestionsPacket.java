package net.minecraft.network.protocol.game;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandSuggestionsPacket implements Packet<ClientGamePacketListener> {
    private final int id;
    private final Suggestions suggestions;

    public ClientboundCommandSuggestionsPacket(int completionId, Suggestions suggestions) {
        this.id = completionId;
        this.suggestions = suggestions;
    }

    public ClientboundCommandSuggestionsPacket(FriendlyByteBuf buf) {
        this.id = buf.readVarInt();
        int i = buf.readVarInt();
        int j = buf.readVarInt();
        StringRange stringRange = StringRange.between(i, i + j);
        List<Suggestion> list = buf.readList(buf2 -> {
            String string = buf2.readUtf();
            Component component = buf2.readNullable(FriendlyByteBuf::readComponentTrusted);
            return new Suggestion(stringRange, string, component);
        });
        this.suggestions = new Suggestions(stringRange, list);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeVarInt(this.suggestions.getRange().getStart());
        buf.writeVarInt(this.suggestions.getRange().getLength());
        buf.writeCollection(this.suggestions.getList(), (buf2, suggestion) -> {
            buf2.writeUtf(suggestion.getText());
            buf2.writeNullable(suggestion.getTooltip(), (buf3, tooltip) -> buf3.writeComponent(ComponentUtils.fromMessage(tooltip)));
        });
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}
