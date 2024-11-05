package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record GoalDebugPayload(int entityId, BlockPos pos, List<GoalDebugPayload.DebugGoal> goals) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation("debug/goal_selector");

    public GoalDebugPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBlockPos(), buf.readList(GoalDebugPayload.DebugGoal::new));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeBlockPos(this.pos);
        buf.writeCollection(this.goals, (bufx, goal) -> goal.write(bufx));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static record DebugGoal(int priority, boolean isRunning, String name) {
        public DebugGoal(FriendlyByteBuf buf) {
            this(buf.readInt(), buf.readBoolean(), buf.readUtf(255));
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(this.priority);
            buf.writeBoolean(this.isRunning);
            buf.writeUtf(this.name);
        }
    }
}
