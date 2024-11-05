package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public interface PacketListener {
    PacketFlow flow();

    ConnectionProtocol protocol();

    void onDisconnect(Component reason);

    boolean isAcceptingMessages();

    default boolean shouldHandleMessage(Packet<?> packet) {
        return this.isAcceptingMessages();
    }

    default boolean shouldPropagateHandlingExceptions() {
        return true;
    }

    default void fillCrashReport(CrashReport report) {
        CrashReportCategory crashReportCategory = report.addCategory("Connection");
        crashReportCategory.setDetail("Protocol", () -> this.protocol().id());
        crashReportCategory.setDetail("Flow", () -> this.flow().toString());
        this.fillListenerSpecificCrashDetails(crashReportCategory);
    }

    default void fillListenerSpecificCrashDetails(CrashReportCategory section) {
    }
}
