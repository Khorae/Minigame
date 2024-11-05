package net.minecraft.network;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.SampleLogger;

public class BandwidthDebugMonitor {
    private final AtomicInteger bytesReceived = new AtomicInteger();
    private final SampleLogger bandwidthLogger;

    public BandwidthDebugMonitor(SampleLogger log) {
        this.bandwidthLogger = log;
    }

    public void onReceive(int bytes) {
        this.bytesReceived.getAndAdd(bytes);
    }

    public void tick() {
        this.bandwidthLogger.logSample((long)this.bytesReceived.getAndSet(0));
    }
}
