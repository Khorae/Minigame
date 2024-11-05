package net.minecraft.network.chat;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public class ComponentSerialization {
    public static final Codec<Component> CODEC = ExtraCodecs.recursive("Component", ComponentSerialization::createCodec);
    public static final Codec<Component> FLAT_CODEC = ExtraCodecs.FLAT_JSON
        .flatXmap(json -> CODEC.parse(JsonOps.INSTANCE, json), text -> CODEC.encodeStart(JsonOps.INSTANCE, text));

    private static MutableComponent createFromList(List<Component> texts) {
        MutableComponent mutableComponent = texts.get(0).copy();

        for (int i = 1; i < texts.size(); i++) {
            mutableComponent.append(texts.get(i));
        }

        return mutableComponent;
    }

    public static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcher(
        T[] types, Function<T, MapCodec<? extends E>> typeToCodec, Function<E, T> valueToType, String dispatchingKey
    ) {
        MapCodec<E> mapCodec = new ComponentSerialization.FuzzyCodec<>(
            Stream.<T>of(types).map(typeToCodec).toList(), object -> typeToCodec.apply(valueToType.apply(object))
        );
        Codec<T> codec = StringRepresentable.fromValues((Supplier<T[]>)(() -> types));
        MapCodec<E> mapCodec2 = codec.dispatchMap(dispatchingKey, valueToType, type -> typeToCodec.apply((T)type).codec());
        MapCodec<E> mapCodec3 = new ComponentSerialization.StrictEither<>(dispatchingKey, mapCodec2, mapCodec);
        return ExtraCodecs.orCompressed(mapCodec3, mapCodec2);
    }

    // Paper start - adventure; create separate codec for each locale
    private static final java.util.Map<java.util.Locale, Codec<Component>> LOCALIZED_CODECS = new java.util.concurrent.ConcurrentHashMap<>();

    public static Codec<Component> localizedCodec(final java.util.@org.checkerframework.checker.nullness.qual.Nullable Locale locale) {
        if (locale == null) {
            return CODEC;
        }
        return LOCALIZED_CODECS.computeIfAbsent(locale,
            loc -> ExtraCodecs.recursive("Component", selfCodec -> createCodec(selfCodec, loc)));
    }
    // Paper end - adventure; create separate codec for each locale

    private static Codec<Component> createCodec(Codec<Component> selfCodec) {
        // Paper start - adventure; create separate codec for each locale
        return createCodec(selfCodec, null);
    }

    private static Codec<Component> createCodec(Codec<Component> selfCodec, @javax.annotation.Nullable java.util.Locale locale) {
        // Paper end - adventure; create separate codec for each locale
        ComponentContents.Type<?>[] types = new ComponentContents.Type[]{
            PlainTextContents.TYPE, TranslatableContents.TYPE, KeybindContents.TYPE, ScoreContents.TYPE, SelectorContents.TYPE, NbtContents.TYPE
        };
        MapCodec<ComponentContents> mapCodec = createLegacyComponentMatcher(types, ComponentContents.Type::codec, ComponentContents::type, "type");
        Codec<Component> codec = RecordCodecBuilder.create(
            instance -> instance.group(
                        mapCodec.forGetter(Component::getContents),
                        ExtraCodecs.strictOptionalField(ExtraCodecs.nonEmptyList(selfCodec.listOf()), "extra", List.of()).forGetter(Component::getSiblings),
                        Style.Serializer.MAP_CODEC.forGetter(Component::getStyle)
                    )
                    .apply(instance, MutableComponent::new)
        );
        // Paper start - adventure; create separate codec for each locale
        final Codec<Component> origCodec = codec;
        codec = new Codec<>() {
            @Override
            public <T> DataResult<com.mojang.datafixers.util.Pair<Component, T>> decode(final DynamicOps<T> ops, final T input) {
                return origCodec.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(final Component input, final DynamicOps<T> ops, final T prefix) {
                final net.kyori.adventure.text.Component adventureComponent;
                if (input instanceof io.papermc.paper.adventure.AdventureComponent adv) {
                    adventureComponent = adv.adventure$component();
                } else if (locale != null && input.getContents() instanceof TranslatableContents && io.papermc.paper.adventure.PaperAdventure.hasAnyTranslations()) {
                    adventureComponent = io.papermc.paper.adventure.PaperAdventure.asAdventure(input);
                } else {
                    return origCodec.encode(input, ops, prefix);
                }
                return io.papermc.paper.adventure.PaperAdventure.localizedCodec(locale)
                    .encode(adventureComponent, ops, prefix);
            }

            @Override
            public String toString() {
                return origCodec.toString() + "[AdventureComponentAware]";
            }
        };
        // Paper end - adventure; create separate codec for each locale
        return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(selfCodec.listOf())), codec)
            .xmap(either -> either.map(either2 -> either2.map(Component::literal, ComponentSerialization::createFromList), text -> (Component)text), text -> {
                String string = text.tryCollapseToString();
                return string != null ? Either.left(Either.left(string)) : Either.right(text);
            });
    }

    static class FuzzyCodec<T> extends MapCodec<T> {
        private final List<MapCodec<? extends T>> codecs;
        private final Function<T, MapEncoder<? extends T>> encoderGetter;

        public FuzzyCodec(List<MapCodec<? extends T>> codecs, Function<T, MapEncoder<? extends T>> codecGetter) {
            this.codecs = codecs;
            this.encoderGetter = codecGetter;
        }

        public <S> DataResult<T> decode(DynamicOps<S> dynamicOps, MapLike<S> mapLike) {
            for (MapDecoder<? extends T> mapDecoder : this.codecs) {
                DataResult<? extends T> dataResult = mapDecoder.decode(dynamicOps, mapLike);
                if (dataResult.result().isPresent()) {
                    return (DataResult<T>)dataResult;
                }
            }

            return DataResult.error(() -> "No matching codec found");
        }

        public <S> RecordBuilder<S> encode(T object, DynamicOps<S> dynamicOps, RecordBuilder<S> recordBuilder) {
            MapEncoder<T> mapEncoder = (MapEncoder<T>)this.encoderGetter.apply(object);
            return mapEncoder.encode(object, dynamicOps, recordBuilder);
        }

        public <S> Stream<S> keys(DynamicOps<S> dynamicOps) {
            return this.codecs.stream().flatMap(codec -> codec.keys(dynamicOps)).distinct();
        }

        public String toString() {
            return "FuzzyCodec[" + this.codecs + "]";
        }
    }

    static class StrictEither<T> extends MapCodec<T> {
        private final String typeFieldName;
        private final MapCodec<T> typed;
        private final MapCodec<T> fuzzy;

        public StrictEither(String dispatchingKey, MapCodec<T> withKeyCodec, MapCodec<T> withoutKeyCodec) {
            this.typeFieldName = dispatchingKey;
            this.typed = withKeyCodec;
            this.fuzzy = withoutKeyCodec;
        }

        public <O> DataResult<T> decode(DynamicOps<O> dynamicOps, MapLike<O> mapLike) {
            return mapLike.get(this.typeFieldName) != null ? this.typed.decode(dynamicOps, mapLike) : this.fuzzy.decode(dynamicOps, mapLike);
        }

        public <O> RecordBuilder<O> encode(T object, DynamicOps<O> dynamicOps, RecordBuilder<O> recordBuilder) {
            return this.fuzzy.encode(object, dynamicOps, recordBuilder);
        }

        public <T1> Stream<T1> keys(DynamicOps<T1> dynamicOps) {
            return Stream.concat(this.typed.keys(dynamicOps), this.fuzzy.keys(dynamicOps)).distinct();
        }
    }
}
