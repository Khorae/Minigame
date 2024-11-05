package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class StructureUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
    public static String testStructuresDir = "gameteststructures";

    public static Rotation getRotationForRotationSteps(int steps) {
        switch (steps) {
            case 0:
                return Rotation.NONE;
            case 1:
                return Rotation.CLOCKWISE_90;
            case 2:
                return Rotation.CLOCKWISE_180;
            case 3:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + steps);
        }
    }

    public static int getRotationStepsForRotation(Rotation rotation) {
        switch (rotation) {
            case NONE:
                return 0;
            case CLOCKWISE_90:
                return 1;
            case CLOCKWISE_180:
                return 2;
            case COUNTERCLOCKWISE_90:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + rotation);
        }
    }

    public static AABB getStructureBounds(StructureBlockEntity structureBlockEntity) {
        return AABB.of(getStructureBoundingBox(structureBlockEntity));
    }

    public static BoundingBox getStructureBoundingBox(StructureBlockEntity structureBlockEntity) {
        BlockPos blockPos = getStructureOrigin(structureBlockEntity);
        BlockPos blockPos2 = getTransformedFarCorner(blockPos, structureBlockEntity.getStructureSize(), structureBlockEntity.getRotation());
        return BoundingBox.fromCorners(blockPos, blockPos2);
    }

    public static BlockPos getStructureOrigin(StructureBlockEntity structureBlockEntity) {
        return structureBlockEntity.getBlockPos().offset(structureBlockEntity.getStructurePos());
    }

    public static void addCommandBlockAndButtonToStartTest(BlockPos pos, BlockPos relativePos, Rotation rotation, ServerLevel world) {
        BlockPos blockPos = StructureTemplate.transform(pos.offset(relativePos), Mirror.NONE, rotation, pos);
        world.setBlockAndUpdate(blockPos, Blocks.COMMAND_BLOCK.defaultBlockState());
        CommandBlockEntity commandBlockEntity = (CommandBlockEntity)world.getBlockEntity(blockPos);
        commandBlockEntity.getCommandBlock().setCommand("test runthis");
        BlockPos blockPos2 = StructureTemplate.transform(blockPos.offset(0, 0, -1), Mirror.NONE, rotation, blockPos);
        world.setBlockAndUpdate(blockPos2, Blocks.STONE_BUTTON.defaultBlockState().rotate(rotation));
    }

    public static void createNewEmptyStructureBlock(String testName, BlockPos pos, Vec3i relativePos, Rotation rotation, ServerLevel world) {
        BoundingBox boundingBox = getStructureBoundingBox(pos.above(), relativePos, rotation);
        clearSpaceForStructure(boundingBox, world);
        world.setBlockAndUpdate(pos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)world.getBlockEntity(pos);
        structureBlockEntity.setIgnoreEntities(false);
        structureBlockEntity.setStructureName(new ResourceLocation(testName));
        structureBlockEntity.setStructureSize(relativePos);
        structureBlockEntity.setMode(StructureMode.SAVE);
        structureBlockEntity.setShowBoundingBox(true);
    }

    public static StructureBlockEntity prepareTestStructure(GameTestInfo state, BlockPos pos, Rotation rotation, ServerLevel world) {
        Vec3i vec3i = world.getStructureManager()
            .get(new ResourceLocation(state.getStructureName()))
            .orElseThrow(() -> new IllegalStateException("Missing test structure: " + state.getStructureName()))
            .getSize();
        BoundingBox boundingBox = getStructureBoundingBox(pos, vec3i, rotation);
        BlockPos blockPos;
        if (rotation == Rotation.NONE) {
            blockPos = pos;
        } else if (rotation == Rotation.CLOCKWISE_90) {
            blockPos = pos.offset(vec3i.getZ() - 1, 0, 0);
        } else if (rotation == Rotation.CLOCKWISE_180) {
            blockPos = pos.offset(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
        } else {
            if (rotation != Rotation.COUNTERCLOCKWISE_90) {
                throw new IllegalArgumentException("Invalid rotation: " + rotation);
            }

            blockPos = pos.offset(0, 0, vec3i.getX() - 1);
        }

        forceLoadChunks(boundingBox, world);
        clearSpaceForStructure(boundingBox, world);
        return createStructureBlock(state, blockPos.below(), rotation, world);
    }

    private static void forceLoadChunks(BoundingBox box, ServerLevel world) {
        box.intersectingChunks().forEach(chunkPos -> world.setChunkForced(chunkPos.x, chunkPos.z, true));
    }

    public static void clearSpaceForStructure(BoundingBox area, ServerLevel world) {
        int i = area.minY() - 1;
        BoundingBox boundingBox = new BoundingBox(area.minX() - 2, area.minY() - 3, area.minZ() - 3, area.maxX() + 3, area.maxY() + 20, area.maxZ() + 3);
        BlockPos.betweenClosedStream(boundingBox).forEach(pos -> clearBlock(i, pos, world));
        world.getBlockTicks().clearArea(boundingBox);
        world.clearBlockEvents(boundingBox);
        AABB aABB = new AABB(
            (double)boundingBox.minX(),
            (double)boundingBox.minY(),
            (double)boundingBox.minZ(),
            (double)boundingBox.maxX(),
            (double)boundingBox.maxY(),
            (double)boundingBox.maxZ()
        );
        List<Entity> list = world.getEntitiesOfClass(Entity.class, aABB, entity -> !(entity instanceof Player));
        list.forEach(Entity::discard);
    }

    public static BlockPos getTransformedFarCorner(BlockPos pos, Vec3i size, Rotation rotation) {
        BlockPos blockPos = pos.offset(size).offset(-1, -1, -1);
        return StructureTemplate.transform(blockPos, Mirror.NONE, rotation, pos);
    }

    public static BoundingBox getStructureBoundingBox(BlockPos pos, Vec3i relativePos, Rotation rotation) {
        BlockPos blockPos = getTransformedFarCorner(pos, relativePos, rotation);
        BoundingBox boundingBox = BoundingBox.fromCorners(pos, blockPos);
        int i = Math.min(boundingBox.minX(), boundingBox.maxX());
        int j = Math.min(boundingBox.minZ(), boundingBox.maxZ());
        return boundingBox.move(pos.getX() - i, 0, pos.getZ() - j);
    }

    public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos pos, int radius, ServerLevel world) {
        return findStructureBlocks(pos, radius, world).stream().filter(structureBlockPos -> doesStructureContain(structureBlockPos, pos, world)).findFirst();
    }

    @Nullable
    public static BlockPos findNearestStructureBlock(BlockPos pos, int radius, ServerLevel world) {
        Comparator<BlockPos> comparator = Comparator.comparingInt(posx -> posx.distManhattan(pos));
        Collection<BlockPos> collection = findStructureBlocks(pos, radius, world);
        Optional<BlockPos> optional = collection.stream().min(comparator);
        return optional.orElse(null);
    }

    public static Collection<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerLevel world) {
        Collection<BlockPos> collection = Lists.newArrayList();
        BoundingBox boundingBox = new BoundingBox(pos).inflatedBy(radius);
        BlockPos.betweenClosedStream(boundingBox).forEach(blockPos -> {
            if (world.getBlockState(blockPos).is(Blocks.STRUCTURE_BLOCK)) {
                collection.add(blockPos.immutable());
            }
        });
        return collection;
    }

    private static StructureBlockEntity createStructureBlock(GameTestInfo state, BlockPos pos, Rotation rotation, ServerLevel world) {
        world.setBlockAndUpdate(pos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)world.getBlockEntity(pos);
        structureBlockEntity.setMode(StructureMode.LOAD);
        structureBlockEntity.setRotation(rotation);
        structureBlockEntity.setIgnoreEntities(false);
        structureBlockEntity.setStructureName(new ResourceLocation(state.getStructureName()));
        structureBlockEntity.setMetaData(state.getTestName());
        if (!structureBlockEntity.loadStructureInfo(world)) {
            throw new RuntimeException("Failed to load structure info for test: " + state.getTestName() + ". Structure name: " + state.getStructureName());
        } else {
            return structureBlockEntity;
        }
    }

    private static void clearBlock(int altitude, BlockPos pos, ServerLevel world) {
        BlockState blockState;
        if (pos.getY() < altitude) {
            blockState = Blocks.STONE.defaultBlockState();
        } else {
            blockState = Blocks.AIR.defaultBlockState();
        }

        BlockInput blockInput = new BlockInput(blockState, Collections.emptySet(), null);
        blockInput.place(world, pos, 2);
        world.blockUpdated(pos, blockState.getBlock());
    }

    private static boolean doesStructureContain(BlockPos structureBlockPos, BlockPos pos, ServerLevel world) {
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)world.getBlockEntity(structureBlockPos);
        return getStructureBoundingBox(structureBlockEntity).isInside(pos);
    }
}
