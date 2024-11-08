package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    @javax.annotation.Nullable private final byte[] encodeBuf; // Paper - Use Velocity cipher
    @javax.annotation.Nullable // Paper - Use Velocity cipher
    private final Deflater deflater;
    @javax.annotation.Nullable // Paper - Use Velocity cipher
    private final com.velocitypowered.natives.compression.VelocityCompressor compressor; // Paper - Use Velocity cipher
    private int threshold;

    // Paper start - Use Velocity cipher
    public CompressionEncoder(int compressionThreshold) {
        this(null, compressionThreshold);
    }
    public CompressionEncoder(@javax.annotation.Nullable com.velocitypowered.natives.compression.VelocityCompressor compressor, int compressionThreshold) {
        this.threshold = compressionThreshold;
        if (compressor == null) {
            this.encodeBuf = new byte[8192];
            this.deflater = new Deflater();
        } else {
            this.encodeBuf = null;
            this.deflater = null;
        }
        this.compressor = compressor;
        // Paper end - Use Velocity cipher
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception { // Paper - Use Velocity cipher
        int i = byteBuf.readableBytes();
        if (i < this.threshold) {
            VarInt.write(byteBuf2, 0);
            byteBuf2.writeBytes(byteBuf);
        } else {
            if (this.deflater != null) { // Paper - Use Velocity cipher
            byte[] bs = new byte[i];
            byteBuf.readBytes(bs);
            VarInt.write(byteBuf2, bs.length);
            this.deflater.setInput(bs, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.encodeBuf);
                byteBuf2.writeBytes(this.encodeBuf, 0, j);
            }

            this.deflater.reset();
            // Paper start - Use Velocity cipher
                return;
            }

            VarInt.write(byteBuf2, i);
            ByteBuf compatibleIn = com.velocitypowered.natives.util.MoreByteBufUtils.ensureCompatible(channelHandlerContext.alloc(), this.compressor, byteBuf);
            try {
                this.compressor.deflate(compatibleIn, byteBuf2);
            } finally {
                compatibleIn.release();
            }
        }
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception{
        if (this.compressor != null) {
            // We allocate bytes to be compressed plus 1 byte. This covers two cases:
            //
            // - Compression
            //    According to https://github.com/ebiggers/libdeflate/blob/master/libdeflate.h#L103,
            //    if the data compresses well (and we do not have some pathological case) then the maximum
            //    size the compressed size will ever be is the input size minus one.
            // - Uncompressed
            //    This is fairly obvious - we will then have one more than the uncompressed size.
            int initialBufferSize = msg.readableBytes() + 1;
            return com.velocitypowered.natives.util.MoreByteBufUtils.preferredBuffer(ctx.alloc(), this.compressor, initialBufferSize);
        }

        return super.allocateBuffer(ctx, msg, preferDirect);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.compressor != null) {
            this.compressor.close();
            // Paper end - Use Velocity cipher
        }
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int compressionThreshold) {
        this.threshold = compressionThreshold;
    }
}
