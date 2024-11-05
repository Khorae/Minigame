package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket implements Packet<ClientGamePacketListener> {
    private final boolean reset;
    private final List<AdvancementHolder> added;
    private final Set<ResourceLocation> removed;
    private final Map<ResourceLocation, AdvancementProgress> progress;

    public ClientboundUpdateAdvancementsPacket(
        boolean clearCurrent, Collection<AdvancementHolder> toEarn, Set<ResourceLocation> toRemove, Map<ResourceLocation, AdvancementProgress> toSetProgress
    ) {
        this.reset = clearCurrent;
        this.added = List.copyOf(toEarn);
        this.removed = Set.copyOf(toRemove);
        this.progress = Map.copyOf(toSetProgress);
    }

    public ClientboundUpdateAdvancementsPacket(FriendlyByteBuf buf) {
        this.reset = buf.readBoolean();
        this.added = buf.readList(AdvancementHolder::read);
        this.removed = buf.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readResourceLocation);
        this.progress = buf.readMap(FriendlyByteBuf::readResourceLocation, AdvancementProgress::fromNetwork);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.reset);
        buf.writeCollection(this.added, (buf2, task) -> task.write(buf2));
        buf.writeCollection(this.removed, FriendlyByteBuf::writeResourceLocation);
        buf.writeMap(this.progress, FriendlyByteBuf::writeResourceLocation, (buf2, progress) -> progress.serializeToNetwork(buf2));
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        listener.handleUpdateAdvancementsPacket(this);
    }

    public List<AdvancementHolder> getAdded() {
        return this.added;
    }

    public Set<ResourceLocation> getRemoved() {
        return this.removed;
    }

    public Map<ResourceLocation, AdvancementProgress> getProgress() {
        return this.progress;
    }

    public boolean shouldReset() {
        return this.reset;
    }
}
