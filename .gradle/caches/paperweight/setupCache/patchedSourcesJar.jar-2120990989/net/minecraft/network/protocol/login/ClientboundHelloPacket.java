package net.minecraft.network.protocol.login;

import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket implements Packet<ClientLoginPacketListener> {
    private final String serverId;
    private final byte[] publicKey;
    private final byte[] challenge;

    public ClientboundHelloPacket(String serverId, byte[] publicKey, byte[] nonce) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.challenge = nonce;
    }

    public ClientboundHelloPacket(FriendlyByteBuf buf) {
        this.serverId = buf.readUtf(20);
        this.publicKey = buf.readByteArray();
        this.challenge = buf.readByteArray();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.serverId);
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.challenge);
    }

    @Override
    public void handle(ClientLoginPacketListener listener) {
        listener.handleHello(this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() throws CryptException {
        return Crypt.byteToPublicKey(this.publicKey);
    }

    public byte[] getChallenge() {
        return this.challenge;
    }
}
