package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagType<T extends Tag> {
    T load(DataInput input, NbtAccounter tracker) throws IOException;

    StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor, NbtAccounter tracker) throws IOException;

    default void parseRoot(DataInput input, StreamTagVisitor visitor, NbtAccounter tracker) throws IOException {
        switch (visitor.visitRootEntry(this)) {
            case CONTINUE:
                this.parse(input, visitor, tracker);
            case HALT:
            default:
                break;
            case BREAK:
                this.skip(input, tracker);
        }
    }

    void skip(DataInput input, int count, NbtAccounter tracker) throws IOException;

    void skip(DataInput input, NbtAccounter tracker) throws IOException;

    default boolean isValue() {
        return false;
    }

    String getName();

    String getPrettyName();

    static TagType<EndTag> createInvalid(int type) {
        return new TagType<EndTag>() {
            private IOException createException() {
                return new IOException("Invalid tag id: " + type);
            }

            @Override
            public EndTag load(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
                throw this.createException();
            }

            @Override
            public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor, NbtAccounter tracker) throws IOException {
                throw this.createException();
            }

            @Override
            public void skip(DataInput input, int count, NbtAccounter tracker) throws IOException {
                throw this.createException();
            }

            @Override
            public void skip(DataInput input, NbtAccounter tracker) throws IOException {
                throw this.createException();
            }

            @Override
            public String getName() {
                return "INVALID[" + type + "]";
            }

            @Override
            public String getPrettyName() {
                return "UNKNOWN_" + type;
            }
        };
    }

    public interface StaticSize<T extends Tag> extends TagType<T> {
        @Override
        default void skip(DataInput input, NbtAccounter tracker) throws IOException {
            input.skipBytes(this.size());
        }

        @Override
        default void skip(DataInput input, int count, NbtAccounter tracker) throws IOException {
            input.skipBytes(this.size() * count);
        }

        int size();
    }

    public interface VariableSize<T extends Tag> extends TagType<T> {
        @Override
        default void skip(DataInput input, int count, NbtAccounter tracker) throws IOException {
            for (int i = 0; i < count; i++) {
                this.skip(input, tracker);
            }
        }
    }
}
