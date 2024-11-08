package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public interface PlainTextContents extends ComponentContents {
    MapCodec<PlainTextContents> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContents::text)).apply(instance, PlainTextContents::create)
    );
    ComponentContents.Type<PlainTextContents> TYPE = new ComponentContents.Type<>(CODEC, "text");
    PlainTextContents EMPTY = new PlainTextContents() {
        @Override
        public String toString() {
            return "empty";
        }

        @Override
        public String text() {
            return "";
        }
    };

    static PlainTextContents create(String string) {
        return (PlainTextContents)(string.isEmpty() ? EMPTY : new PlainTextContents.LiteralContents(string));
    }

    String text();

    @Override
    default ComponentContents.Type<?> type() {
        return TYPE;
    }

    public static record LiteralContents(@Override String text) implements PlainTextContents {
        @Override
        public <T> Optional<T> visit(FormattedText.ContentConsumer<T> visitor) {
            return visitor.accept(this.text);
        }

        @Override
        public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> visitor, Style style) {
            return visitor.accept(style, this.text);
        }

        @Override
        public String toString() {
            return "literal{" + this.text + "}";
        }
    }
}
