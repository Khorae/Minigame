package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INLINE_LIST_THRESHOLD = 8;
    private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
    private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    private static final String NAME_VALUE_SEPARATOR = String.valueOf(':');
    private static final String ELEMENT_SEPARATOR = String.valueOf(',');
    private static final String LIST_OPEN = "[";
    private static final String LIST_CLOSE = "]";
    private static final String LIST_TYPE_SEPARATOR = ";";
    private static final String ELEMENT_SPACING = " ";
    private static final String STRUCT_OPEN = "{";
    private static final String STRUCT_CLOSE = "}";
    private static final String NEWLINE = "\n";
    private final String indentation;
    private final int depth;
    private Component result = CommonComponents.EMPTY;

    public TextComponentTagVisitor(String prefix, int indentationLevel) {
        this.indentation = prefix;
        this.depth = indentationLevel;
    }

    public Component visit(Tag element) {
        element.accept(this);
        return this.result;
    }

    @Override
    public void visitString(StringTag element) {
        String string = StringTag.quoteAndEscape(element.getAsString());
        String string2 = string.substring(0, 1);
        Component component = Component.literal(string.substring(1, string.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
        this.result = Component.literal(string2).append(component).append(string2);
    }

    @Override
    public void visitByte(ByteTag element) {
        Component component = Component.literal("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(element.getAsNumber())).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitShort(ShortTag element) {
        Component component = Component.literal("s").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(element.getAsNumber())).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitInt(IntTag element) {
        this.result = Component.literal(String.valueOf(element.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitLong(LongTag element) {
        Component component = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(element.getAsNumber())).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitFloat(FloatTag element) {
        Component component = Component.literal("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(element.getAsFloat())).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitDouble(DoubleTag element) {
        Component component = Component.literal("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        this.result = Component.literal(String.valueOf(element.getAsDouble())).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public void visitByteArray(ByteArrayTag element) {
        Component component = Component.literal("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent mutableComponent = Component.literal("[").append(component).append(";");
        byte[] bs = element.getAsByteArray();

        for (int i = 0; i < bs.length; i++) {
            MutableComponent mutableComponent2 = Component.literal(String.valueOf(bs[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            mutableComponent.append(" ").append(mutableComponent2).append(component);
            if (i != bs.length - 1) {
                mutableComponent.append(ELEMENT_SEPARATOR);
            }
        }

        mutableComponent.append("]");
        this.result = mutableComponent;
    }

    @Override
    public void visitIntArray(IntArrayTag element) {
        Component component = Component.literal("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent mutableComponent = Component.literal("[").append(component).append(";");
        int[] is = element.getAsIntArray();

        for (int i = 0; i < is.length; i++) {
            mutableComponent.append(" ").append(Component.literal(String.valueOf(is[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
            if (i != is.length - 1) {
                mutableComponent.append(ELEMENT_SEPARATOR);
            }
        }

        mutableComponent.append("]");
        this.result = mutableComponent;
    }

    @Override
    public void visitLongArray(LongArrayTag element) {
        Component component = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent mutableComponent = Component.literal("[").append(component).append(";");
        long[] ls = element.getAsLongArray();

        for (int i = 0; i < ls.length; i++) {
            Component component2 = Component.literal(String.valueOf(ls[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            mutableComponent.append(" ").append(component2).append(component);
            if (i != ls.length - 1) {
                mutableComponent.append(ELEMENT_SEPARATOR);
            }
        }

        mutableComponent.append("]");
        this.result = mutableComponent;
    }

    @Override
    public void visitList(ListTag element) {
        if (element.isEmpty()) {
            this.result = Component.literal("[]");
        } else if (INLINE_ELEMENT_TYPES.contains(element.getElementType()) && element.size() <= 8) {
            String string = ELEMENT_SEPARATOR + " ";
            MutableComponent mutableComponent = Component.literal("[");

            for (int i = 0; i < element.size(); i++) {
                if (i != 0) {
                    mutableComponent.append(string);
                }

                mutableComponent.append(new TextComponentTagVisitor(this.indentation, this.depth).visit(element.get(i)));
            }

            mutableComponent.append("]");
            this.result = mutableComponent;
        } else {
            MutableComponent mutableComponent2 = Component.literal("[");
            if (!this.indentation.isEmpty()) {
                mutableComponent2.append("\n");
            }

            for (int j = 0; j < element.size(); j++) {
                MutableComponent mutableComponent3 = Component.literal(Strings.repeat(this.indentation, this.depth + 1));
                mutableComponent3.append(new TextComponentTagVisitor(this.indentation, this.depth + 1).visit(element.get(j)));
                if (j != element.size() - 1) {
                    mutableComponent3.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }

                mutableComponent2.append(mutableComponent3);
            }

            if (!this.indentation.isEmpty()) {
                mutableComponent2.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            mutableComponent2.append("]");
            this.result = mutableComponent2;
        }
    }

    @Override
    public void visitCompound(CompoundTag compound) {
        if (compound.isEmpty()) {
            this.result = Component.literal("{}");
        } else {
            MutableComponent mutableComponent = Component.literal("{");
            Collection<String> collection = compound.getAllKeys();
            if (LOGGER.isDebugEnabled()) {
                List<String> list = Lists.newArrayList(compound.getAllKeys());
                Collections.sort(list);
                collection = list;
            }

            if (!this.indentation.isEmpty()) {
                mutableComponent.append("\n");
            }

            Iterator<String> iterator = collection.iterator();

            while (iterator.hasNext()) {
                String string = iterator.next();
                MutableComponent mutableComponent2 = Component.literal(Strings.repeat(this.indentation, this.depth + 1))
                    .append(handleEscapePretty(string))
                    .append(NAME_VALUE_SEPARATOR)
                    .append(" ")
                    .append(new TextComponentTagVisitor(this.indentation, this.depth + 1).visit(compound.get(string)));
                if (iterator.hasNext()) {
                    mutableComponent2.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
                }

                mutableComponent.append(mutableComponent2);
            }

            if (!this.indentation.isEmpty()) {
                mutableComponent.append("\n").append(Strings.repeat(this.indentation, this.depth));
            }

            mutableComponent.append("}");
            this.result = mutableComponent;
        }
    }

    protected static Component handleEscapePretty(String name) {
        if (SIMPLE_VALUE.matcher(name).matches()) {
            return Component.literal(name).withStyle(SYNTAX_HIGHLIGHTING_KEY);
        } else {
            String string = StringTag.quoteAndEscape(name);
            String string2 = string.substring(0, 1);
            Component component = Component.literal(string.substring(1, string.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
            return Component.literal(string2).append(component).append(string2);
        }
    }

    @Override
    public void visitEnd(EndTag element) {
        this.result = CommonComponents.EMPTY;
    }
}
