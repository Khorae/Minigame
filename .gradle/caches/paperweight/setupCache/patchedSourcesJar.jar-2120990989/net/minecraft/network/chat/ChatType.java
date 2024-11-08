package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ChatType(ChatTypeDecoration chat, ChatTypeDecoration narration) {
    public static final Codec<ChatType> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                    ChatTypeDecoration.CODEC.fieldOf("chat").forGetter(ChatType::chat),
                    ChatTypeDecoration.CODEC.fieldOf("narration").forGetter(ChatType::narration)
                )
                .apply(instance, ChatType::new)
    );
    public static final ChatTypeDecoration DEFAULT_CHAT_DECORATION = ChatTypeDecoration.withSender("chat.type.text");
    public static final ResourceKey<ChatType> CHAT = create("chat");
    public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
    public static final ResourceKey<ChatType> MSG_COMMAND_INCOMING = create("msg_command_incoming");
    public static final ResourceKey<ChatType> MSG_COMMAND_OUTGOING = create("msg_command_outgoing");
    public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_INCOMING = create("team_msg_command_incoming");
    public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_OUTGOING = create("team_msg_command_outgoing");
    public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");

    private static ResourceKey<ChatType> create(String id) {
        return ResourceKey.create(Registries.CHAT_TYPE, new ResourceLocation(id));
    }

    public static void bootstrap(BootstapContext<ChatType> messageTypeRegisterable) {
        messageTypeRegisterable.register(CHAT, new ChatType(DEFAULT_CHAT_DECORATION, ChatTypeDecoration.withSender("chat.type.text.narrate")));
        messageTypeRegisterable.register(
            SAY_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.announcement"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
        );
        messageTypeRegisterable.register(
            MSG_COMMAND_INCOMING,
            new ChatType(ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
        );
        messageTypeRegisterable.register(
            MSG_COMMAND_OUTGOING,
            new ChatType(ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
        );
        messageTypeRegisterable.register(
            TEAM_MSG_COMMAND_INCOMING,
            new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.text"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
        );
        messageTypeRegisterable.register(
            TEAM_MSG_COMMAND_OUTGOING,
            new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.sent"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
        );
        messageTypeRegisterable.register(
            EMOTE_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.emote"), ChatTypeDecoration.withSender("chat.type.emote"))
        );
    }

    public static ChatType.Bound bind(ResourceKey<ChatType> typeKey, Entity entity) {
        return bind(typeKey, entity.level().registryAccess(), entity.getDisplayName());
    }

    public static ChatType.Bound bind(ResourceKey<ChatType> typeKey, CommandSourceStack source) {
        return bind(typeKey, source.registryAccess(), source.getDisplayName());
    }

    public static ChatType.Bound bind(ResourceKey<ChatType> typeKey, RegistryAccess registryManager, Component name) {
        Registry<ChatType> registry = registryManager.registryOrThrow(Registries.CHAT_TYPE);
        return registry.getOrThrow(typeKey).bind(name);
    }

    public ChatType.Bound bind(Component name) {
        return new ChatType.Bound(this, name);
    }

    public static record Bound(ChatType chatType, Component name, @Nullable Component targetName) {
        Bound(ChatType type, Component name) {
            this(type, name, null);
        }

        public Component decorate(Component content) {
            return this.chatType.chat().decorate(content, this);
        }

        public Component decorateNarration(Component content) {
            return this.chatType.narration().decorate(content, this);
        }

        public ChatType.Bound withTargetName(Component targetName) {
            return new ChatType.Bound(this.chatType, this.name, targetName);
        }

        public ChatType.BoundNetwork toNetwork(RegistryAccess registryManager) {
            Registry<ChatType> registry = registryManager.registryOrThrow(Registries.CHAT_TYPE);
            return new ChatType.BoundNetwork(registry.getId(this.chatType), this.name, this.targetName);
        }
    }

    public static record BoundNetwork(int chatType, Component name, @Nullable Component targetName) {
        public BoundNetwork(FriendlyByteBuf buf) {
            this(buf.readVarInt(), buf.readComponentTrusted(), buf.readNullable(FriendlyByteBuf::readComponentTrusted));
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(this.chatType);
            buf.writeComponent(this.name);
            buf.writeNullable(this.targetName, FriendlyByteBuf::writeComponent);
        }

        public Optional<ChatType.Bound> resolve(RegistryAccess registryManager) {
            Registry<ChatType> registry = registryManager.registryOrThrow(Registries.CHAT_TYPE);
            ChatType chatType = registry.byId(this.chatType);
            return Optional.ofNullable(chatType).map(type -> new ChatType.Bound(type, this.name, this.targetName));
        }
    }
}
