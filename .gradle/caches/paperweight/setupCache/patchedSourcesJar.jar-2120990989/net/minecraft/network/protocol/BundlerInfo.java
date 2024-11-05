package net.minecraft.network.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;

public interface BundlerInfo {
    int BUNDLE_SIZE_LIMIT = 4096;
    BundlerInfo EMPTY = new BundlerInfo() {
        @Override
        public void unbundlePacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
            consumer.accept(packet);
        }

        @Nullable
        @Override
        public BundlerInfo.Bundler startPacketBundling(Packet<?> splitter) {
            return null;
        }
    };

    static <T extends PacketListener, P extends BundlePacket<T>> BundlerInfo createForPacket(
        Class<P> bundlePacketType, Function<Iterable<Packet<T>>, P> bundleFunction, BundleDelimiterPacket<T> splitter
    ) {
        return new BundlerInfo() {
            @Override
            public void unbundlePacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
                if (packet.getClass() == bundlePacketType) {
                    P bundlePacket = (P)packet;
                    consumer.accept(splitter);
                    bundlePacket.subPackets().forEach(consumer);
                    consumer.accept(splitter);
                } else {
                    consumer.accept(packet);
                }
            }

            @Nullable
            @Override
            public BundlerInfo.Bundler startPacketBundling(Packet<?> splitter) {
                return splitter == splitter ? new BundlerInfo.Bundler() {
                    private final List<Packet<T>> bundlePackets = new ArrayList<>();

                    @Nullable
                    @Override
                    public Packet<?> addPacket(Packet<?> packet) {
                        if (packet == splitter) {
                            return bundleFunction.apply(this.bundlePackets);
                        } else if (this.bundlePackets.size() >= 4096) {
                            throw new IllegalStateException("Too many packets in a bundle");
                        } else {
                            this.bundlePackets.add((Packet<T>)packet);
                            return null;
                        }
                    }
                } : null;
            }
        };
    }

    void unbundlePacket(Packet<?> packet, Consumer<Packet<?>> consumer);

    @Nullable
    BundlerInfo.Bundler startPacketBundling(Packet<?> splitter);

    public interface Bundler {
        @Nullable
        Packet<?> addPacket(Packet<?> packet);
    }

    public interface Provider {
        BundlerInfo bundlerInfo();
    }
}
