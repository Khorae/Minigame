package net.minecraft.network;

import io.netty.util.Attribute;
import net.minecraft.network.protocol.Packet;

public interface ProtocolSwapHandler {
    static void swapProtocolIfNeeded(Attribute<ConnectionProtocol.CodecData<?>> protocolAttribute, Packet<?> packet) {
        ConnectionProtocol connectionProtocol = packet.nextProtocol();
        if (connectionProtocol != null) {
            ConnectionProtocol.CodecData<?> codecData = protocolAttribute.get();
            ConnectionProtocol connectionProtocol2 = codecData.protocol();
            if (connectionProtocol != connectionProtocol2) {
                ConnectionProtocol.CodecData<?> codecData2 = connectionProtocol.codec(codecData.flow());
                protocolAttribute.set(codecData2);
            }
        }
    }
}
