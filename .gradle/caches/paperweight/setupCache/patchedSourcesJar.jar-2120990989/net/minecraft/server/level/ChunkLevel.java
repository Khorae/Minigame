package net.minecraft.server.level;

import net.minecraft.world.level.chunk.ChunkStatus;

public class ChunkLevel {
    private static final int FULL_CHUNK_LEVEL = 33;
    private static final int BLOCK_TICKING_LEVEL = 32;
    private static final int ENTITY_TICKING_LEVEL = 31;
    public static final int MAX_LEVEL = 33 + ChunkStatus.maxDistance();

    public static ChunkStatus generationStatus(int level) {
        return level < 33 ? ChunkStatus.FULL : ChunkStatus.getStatusAroundFullChunk(level - 33);
    }

    public static int byStatus(ChunkStatus status) {
        return 33 + ChunkStatus.getDistance(status);
    }

    public static FullChunkStatus fullStatus(int level) {
        if (level <= 31) {
            return FullChunkStatus.ENTITY_TICKING;
        } else if (level <= 32) {
            return FullChunkStatus.BLOCK_TICKING;
        } else {
            return level <= 33 ? FullChunkStatus.FULL : FullChunkStatus.INACCESSIBLE;
        }
    }

    public static int byStatus(FullChunkStatus type) {
        return switch (type) {
            case INACCESSIBLE -> MAX_LEVEL;
            case FULL -> 33;
            case BLOCK_TICKING -> 32;
            case ENTITY_TICKING -> 31;
        };
    }

    public static boolean isEntityTicking(int level) {
        return level <= 31;
    }

    public static boolean isBlockTicking(int level) {
        return level <= 32;
    }

    public static boolean isLoaded(int level) {
        return level <= MAX_LEVEL;
    }
}
