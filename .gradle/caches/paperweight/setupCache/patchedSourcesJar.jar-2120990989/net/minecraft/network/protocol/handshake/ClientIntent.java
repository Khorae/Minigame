package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;

public enum ClientIntent {
    STATUS,
    LOGIN;

    private static final int STATUS_ID = 1;
    private static final int LOGIN_ID = 2;

    public static ClientIntent byId(int id) {
        return switch (id) {
            case 1 -> STATUS;
            case 2 -> LOGIN;
            default -> throw new IllegalArgumentException("Unknown connection intent: " + id);
        };
    }

    public int id() {
        return switch (this) {
            case STATUS -> 1;
            case LOGIN -> 2;
        };
    }

    public ConnectionProtocol protocol() {
        return switch (this) {
            case STATUS -> ConnectionProtocol.STATUS;
            case LOGIN -> ConnectionProtocol.LOGIN;
        };
    }
}
