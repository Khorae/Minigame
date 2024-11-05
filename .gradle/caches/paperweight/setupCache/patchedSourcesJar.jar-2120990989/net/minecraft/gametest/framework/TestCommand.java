package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TestCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_CLEAR_RADIUS = 200;
    private static final int MAX_CLEAR_RADIUS = 1024;
    private static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
    private static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
    private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final int DEFAULT_X_SIZE = 5;
    private static final int DEFAULT_Y_SIZE = 5;
    private static final int DEFAULT_Z_SIZE = 5;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("test")
                .then(
                    Commands.literal("runthis")
                        .executes(context -> runNearbyTest(context.getSource(), false))
                        .then(Commands.literal("untilFailed").executes(context -> runNearbyTest(context.getSource(), true)))
                )
                .then(Commands.literal("resetthis").executes(context -> resetNearbyTest(context.getSource())))
                .then(Commands.literal("runthese").executes(context -> runAllNearbyTests(context.getSource(), false)))
                .then(
                    Commands.literal("runfailed")
                        .executes(context -> runLastFailedTests(context.getSource(), false, 0, 8))
                        .then(
                            Commands.argument("onlyRequiredTests", BoolArgumentType.bool())
                                .executes(context -> runLastFailedTests(context.getSource(), BoolArgumentType.getBool(context, "onlyRequiredTests"), 0, 8))
                                .then(
                                    Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                        .executes(
                                            context -> runLastFailedTests(
                                                    context.getSource(),
                                                    BoolArgumentType.getBool(context, "onlyRequiredTests"),
                                                    IntegerArgumentType.getInteger(context, "rotationSteps"),
                                                    8
                                                )
                                        )
                                        .then(
                                            Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                                .executes(
                                                    context -> runLastFailedTests(
                                                            context.getSource(),
                                                            BoolArgumentType.getBool(context, "onlyRequiredTests"),
                                                            IntegerArgumentType.getInteger(context, "rotationSteps"),
                                                            IntegerArgumentType.getInteger(context, "testsPerRow")
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("run")
                        .then(
                            Commands.argument("testName", TestFunctionArgument.testFunctionArgument())
                                .executes(context -> runTest(context.getSource(), TestFunctionArgument.getTestFunction(context, "testName"), 0))
                                .then(
                                    Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                        .executes(
                                            context -> runTest(
                                                    context.getSource(),
                                                    TestFunctionArgument.getTestFunction(context, "testName"),
                                                    IntegerArgumentType.getInteger(context, "rotationSteps")
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("runall")
                        .executes(context -> runAllTests(context.getSource(), 0, 8))
                        .then(
                            Commands.argument("testClassName", TestClassNameArgument.testClassName())
                                .executes(
                                    context -> runAllTestsInClass(context.getSource(), TestClassNameArgument.getTestClassName(context, "testClassName"), 0, 8)
                                )
                                .then(
                                    Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                        .executes(
                                            context -> runAllTestsInClass(
                                                    context.getSource(),
                                                    TestClassNameArgument.getTestClassName(context, "testClassName"),
                                                    IntegerArgumentType.getInteger(context, "rotationSteps"),
                                                    8
                                                )
                                        )
                                        .then(
                                            Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                                .executes(
                                                    context -> runAllTestsInClass(
                                                            context.getSource(),
                                                            TestClassNameArgument.getTestClassName(context, "testClassName"),
                                                            IntegerArgumentType.getInteger(context, "rotationSteps"),
                                                            IntegerArgumentType.getInteger(context, "testsPerRow")
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(
                            Commands.argument("rotationSteps", IntegerArgumentType.integer())
                                .executes(context -> runAllTests(context.getSource(), IntegerArgumentType.getInteger(context, "rotationSteps"), 8))
                                .then(
                                    Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                        .executes(
                                            context -> runAllTests(
                                                    context.getSource(),
                                                    IntegerArgumentType.getInteger(context, "rotationSteps"),
                                                    IntegerArgumentType.getInteger(context, "testsPerRow")
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("export")
                        .then(
                            Commands.argument("testName", StringArgumentType.word())
                                .executes(context -> exportTestStructure(context.getSource(), StringArgumentType.getString(context, "testName")))
                        )
                )
                .then(Commands.literal("exportthis").executes(context -> exportNearestTestStructure(context.getSource())))
                .then(Commands.literal("exportthese").executes(context -> exportAllNearbyTests(context.getSource())))
                .then(
                    Commands.literal("import")
                        .then(
                            Commands.argument("testName", StringArgumentType.word())
                                .executes(context -> importTestStructure(context.getSource(), StringArgumentType.getString(context, "testName")))
                        )
                )
                .then(
                    Commands.literal("pos")
                        .executes(context -> showPos(context.getSource(), "pos"))
                        .then(
                            Commands.argument("var", StringArgumentType.word())
                                .executes(context -> showPos(context.getSource(), StringArgumentType.getString(context, "var")))
                        )
                )
                .then(
                    Commands.literal("create")
                        .then(
                            Commands.argument("testName", StringArgumentType.word())
                                .executes(context -> createNewStructure(context.getSource(), StringArgumentType.getString(context, "testName"), 5, 5, 5))
                                .then(
                                    Commands.argument("width", IntegerArgumentType.integer())
                                        .executes(
                                            context -> createNewStructure(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "testName"),
                                                    IntegerArgumentType.getInteger(context, "width"),
                                                    IntegerArgumentType.getInteger(context, "width"),
                                                    IntegerArgumentType.getInteger(context, "width")
                                                )
                                        )
                                        .then(
                                            Commands.argument("height", IntegerArgumentType.integer())
                                                .then(
                                                    Commands.argument("depth", IntegerArgumentType.integer())
                                                        .executes(
                                                            context -> createNewStructure(
                                                                    context.getSource(),
                                                                    StringArgumentType.getString(context, "testName"),
                                                                    IntegerArgumentType.getInteger(context, "width"),
                                                                    IntegerArgumentType.getInteger(context, "height"),
                                                                    IntegerArgumentType.getInteger(context, "depth")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("clearall")
                        .executes(context -> clearAllTests(context.getSource(), 200))
                        .then(
                            Commands.argument("radius", IntegerArgumentType.integer())
                                .executes(context -> clearAllTests(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))
                        )
                )
        );
    }

    private static int createNewStructure(CommandSourceStack source, String testName, int x, int y, int z) {
        if (x <= 48 && y <= 48 && z <= 48) {
            ServerLevel serverLevel = source.getLevel();
            BlockPos blockPos = createTestPositionAround(source).below();
            StructureUtils.createNewEmptyStructureBlock(testName.toLowerCase(), blockPos, new Vec3i(x, y, z), Rotation.NONE, serverLevel);

            for (int i = 0; i < x; i++) {
                for (int j = 0; j < z; j++) {
                    BlockPos blockPos2 = new BlockPos(blockPos.getX() + i, blockPos.getY() + 1, blockPos.getZ() + j);
                    Block block = Blocks.POLISHED_ANDESITE;
                    BlockInput blockInput = new BlockInput(block.defaultBlockState(), Collections.emptySet(), null);
                    blockInput.place(serverLevel, blockPos2, 2);
                }
            }

            StructureUtils.addCommandBlockAndButtonToStartTest(blockPos, new BlockPos(1, 0, -1), Rotation.NONE, serverLevel);
            return 0;
        } else {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
    }

    private static int showPos(CommandSourceStack source, String variableName) throws CommandSyntaxException {
        BlockHitResult blockHitResult = (BlockHitResult)source.getPlayerOrException().pick(10.0, 1.0F, false);
        BlockPos blockPos = blockHitResult.getBlockPos();
        ServerLevel serverLevel = source.getLevel();
        Optional<BlockPos> optional = StructureUtils.findStructureBlockContainingPos(blockPos, 15, serverLevel);
        if (optional.isEmpty()) {
            optional = StructureUtils.findStructureBlockContainingPos(blockPos, 200, serverLevel);
        }

        if (optional.isEmpty()) {
            source.sendFailure(Component.literal("Can't find a structure block that contains the targeted pos " + blockPos));
            return 0;
        } else {
            StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(optional.get());
            BlockPos blockPos2 = blockPos.subtract(optional.get());
            String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
            String string2 = structureBlockEntity.getMetaData();
            Component component = Component.literal(string)
                .setStyle(
                    Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GREEN)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard")))
                        .withClickEvent(
                            new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + variableName + " = new BlockPos(" + string + ");")
                        )
                );
            source.sendSuccess(() -> Component.literal("Position relative to " + string2 + ": ").append(component), false);
            DebugPackets.sendGameTestAddMarker(serverLevel, new BlockPos(blockPos), string, -2147418368, 10000);
            return 1;
        }
    }

    private static int runNearbyTest(CommandSourceStack source, boolean rerunUntilFailed) {
        BlockPos blockPos = BlockPos.containing(source.getPosition());
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos2 = StructureUtils.findNearestStructureBlock(blockPos, 15, serverLevel);
        if (blockPos2 == null) {
            say(serverLevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        } else {
            GameTestRunner.clearMarkers(serverLevel);
            runTest(serverLevel, blockPos2, null, rerunUntilFailed);
            return 1;
        }
    }

    private static int resetNearbyTest(CommandSourceStack source) {
        BlockPos blockPos = BlockPos.containing(source.getPosition());
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos2 = StructureUtils.findNearestStructureBlock(blockPos, 15, serverLevel);
        if (blockPos2 == null) {
            say(serverLevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        } else {
            StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos2);
            structureBlockEntity.placeStructure(serverLevel);
            String string = structureBlockEntity.getMetaData();
            TestFunction testFunction = GameTestRegistry.getTestFunction(string);
            say(serverLevel, "Reset succeded for: " + testFunction, ChatFormatting.GREEN);
            return 1;
        }
    }

    private static int runAllNearbyTests(CommandSourceStack source, boolean rerunUntilFailed) {
        BlockPos blockPos = BlockPos.containing(source.getPosition());
        ServerLevel serverLevel = source.getLevel();
        Collection<BlockPos> collection = StructureUtils.findStructureBlocks(blockPos, 200, serverLevel);
        if (collection.isEmpty()) {
            say(serverLevel, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
            return 1;
        } else {
            GameTestRunner.clearMarkers(serverLevel);
            say(source, "Running " + collection.size() + " tests...");
            MultipleTestTracker multipleTestTracker = new MultipleTestTracker();
            collection.forEach(pos -> runTest(serverLevel, pos, multipleTestTracker, rerunUntilFailed));
            return 1;
        }
    }

    private static void runTest(ServerLevel world, BlockPos pos, @Nullable MultipleTestTracker tests, boolean rerunUntilFailed) {
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)world.getBlockEntity(pos);
        String string = structureBlockEntity.getMetaData();
        Optional<TestFunction> optional = GameTestRegistry.findTestFunction(string);
        if (optional.isEmpty()) {
            say(world, "Test function for test " + string + " could not be found", ChatFormatting.RED);
        } else {
            TestFunction testFunction = optional.get();
            GameTestInfo gameTestInfo = new GameTestInfo(testFunction, structureBlockEntity.getRotation(), world);
            gameTestInfo.setRerunUntilFailed(rerunUntilFailed);
            if (tests != null) {
                tests.addTestToTrack(gameTestInfo);
                gameTestInfo.addListener(new TestCommand.TestSummaryDisplayer(world, tests));
            }

            if (verifyStructureExists(world, gameTestInfo)) {
                runTestPreparation(testFunction, world);
                BoundingBox boundingBox = StructureUtils.getStructureBoundingBox(structureBlockEntity);
                BlockPos blockPos = new BlockPos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
                GameTestRunner.runTest(gameTestInfo, blockPos, GameTestTicker.SINGLETON);
            }
        }
    }

    private static boolean verifyStructureExists(ServerLevel world, GameTestInfo state) {
        if (world.getStructureManager().get(new ResourceLocation(state.getStructureName())).isEmpty()) {
            say(world, "Test structure " + state.getStructureName() + " could not be found", ChatFormatting.RED);
            return false;
        } else {
            return true;
        }
    }

    static void showTestSummaryIfAllDone(ServerLevel world, MultipleTestTracker tests) {
        if (tests.isDone()) {
            say(world, "GameTest done! " + tests.getTotalCount() + " tests were run", ChatFormatting.WHITE);
            if (tests.hasFailedRequired()) {
                say(world, tests.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
            } else {
                say(world, "All required tests passed :)", ChatFormatting.GREEN);
            }

            if (tests.hasFailedOptional()) {
                say(world, tests.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
            }
        }
    }

    private static int clearAllTests(CommandSourceStack source, int radius) {
        ServerLevel serverLevel = source.getLevel();
        GameTestRunner.clearMarkers(serverLevel);
        BlockPos blockPos = BlockPos.containing(
            source.getPosition().x,
            (double)source.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.containing(source.getPosition())).getY(),
            source.getPosition().z
        );
        GameTestRunner.clearAllTests(serverLevel, blockPos, GameTestTicker.SINGLETON, Mth.clamp(radius, 0, 1024));
        return 1;
    }

    private static int runTest(CommandSourceStack source, TestFunction testFunction, int rotationSteps) {
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos = createTestPositionAround(source);
        GameTestRunner.clearMarkers(serverLevel);
        runTestPreparation(testFunction, serverLevel);
        Rotation rotation = StructureUtils.getRotationForRotationSteps(rotationSteps);
        GameTestInfo gameTestInfo = new GameTestInfo(testFunction, rotation, serverLevel);
        if (!verifyStructureExists(serverLevel, gameTestInfo)) {
            return 0;
        } else {
            GameTestRunner.runTest(gameTestInfo, blockPos, GameTestTicker.SINGLETON);
            return 1;
        }
    }

    private static BlockPos createTestPositionAround(CommandSourceStack source) {
        BlockPos blockPos = BlockPos.containing(source.getPosition());
        int i = source.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos).getY();
        return new BlockPos(blockPos.getX(), i + 1, blockPos.getZ() + 3);
    }

    private static void runTestPreparation(TestFunction testFunction, ServerLevel world) {
        Consumer<ServerLevel> consumer = GameTestRegistry.getBeforeBatchFunction(testFunction.getBatchName());
        if (consumer != null) {
            consumer.accept(world);
        }
    }

    private static int runAllTests(CommandSourceStack source, int rotationSteps, int testsPerRow) {
        GameTestRunner.clearMarkers(source.getLevel());
        Collection<TestFunction> collection = GameTestRegistry.getAllTestFunctions();
        say(source, "Running all " + collection.size() + " tests...");
        GameTestRegistry.forgetFailedTests();
        runTests(source, collection, rotationSteps, testsPerRow);
        return 1;
    }

    private static int runAllTestsInClass(CommandSourceStack source, String testClass, int rotationSteps, int testsPerRow) {
        Collection<TestFunction> collection = GameTestRegistry.getTestFunctionsForClassName(testClass);
        GameTestRunner.clearMarkers(source.getLevel());
        say(source, "Running " + collection.size() + " tests from " + testClass + "...");
        GameTestRegistry.forgetFailedTests();
        runTests(source, collection, rotationSteps, testsPerRow);
        return 1;
    }

    private static int runLastFailedTests(CommandSourceStack source, boolean requiredOnly, int rotationSteps, int testsPerRow) {
        Collection<TestFunction> collection;
        if (requiredOnly) {
            collection = GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
        } else {
            collection = GameTestRegistry.getLastFailedTests();
        }

        if (collection.isEmpty()) {
            say(source, "No failed tests to rerun");
            return 0;
        } else {
            GameTestRunner.clearMarkers(source.getLevel());
            say(source, "Rerunning " + collection.size() + " failed tests (" + (requiredOnly ? "only required tests" : "including optional tests") + ")");
            runTests(source, collection, rotationSteps, testsPerRow);
            return 1;
        }
    }

    private static void runTests(CommandSourceStack source, Collection<TestFunction> testFunctions, int rotationSteps, int testsPerRow) {
        BlockPos blockPos = createTestPositionAround(source);
        ServerLevel serverLevel = source.getLevel();
        Rotation rotation = StructureUtils.getRotationForRotationSteps(rotationSteps);
        Collection<GameTestInfo> collection = GameTestRunner.runTests(testFunctions, blockPos, rotation, serverLevel, GameTestTicker.SINGLETON, testsPerRow);
        MultipleTestTracker multipleTestTracker = new MultipleTestTracker(collection);
        multipleTestTracker.addListener(new TestCommand.TestSummaryDisplayer(serverLevel, multipleTestTracker));
        multipleTestTracker.addFailureListener(test -> GameTestRegistry.rememberFailedTest(test.getTestFunction()));
    }

    private static void say(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), false);
    }

    private static int exportNearestTestStructure(CommandSourceStack source) {
        BlockPos blockPos = BlockPos.containing(source.getPosition());
        ServerLevel serverLevel = source.getLevel();
        BlockPos blockPos2 = StructureUtils.findNearestStructureBlock(blockPos, 15, serverLevel);
        if (blockPos2 == null) {
            say(serverLevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        } else {
            StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos2);
            return saveAndExportTestStructure(source, structureBlockEntity);
        }
    }

    private static int exportAllNearbyTests(CommandSourceStack source) {
        BlockPos blockPos = BlockPos.containing(source.getPosition());
        ServerLevel serverLevel = source.getLevel();
        Collection<BlockPos> collection = StructureUtils.findStructureBlocks(blockPos, 200, serverLevel);
        if (collection.isEmpty()) {
            say(serverLevel, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
            return 1;
        } else {
            boolean bl = true;

            for (BlockPos blockPos2 : collection) {
                StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos2);
                if (saveAndExportTestStructure(source, structureBlockEntity) != 0) {
                    bl = false;
                }
            }

            return bl ? 0 : 1;
        }
    }

    private static int saveAndExportTestStructure(CommandSourceStack source, StructureBlockEntity blockEntity) {
        String string = blockEntity.getStructureName();
        if (!blockEntity.saveStructure(true)) {
            say(source, "Failed to save structure " + string);
        }

        return exportTestStructure(source, string);
    }

    private static int exportTestStructure(CommandSourceStack source, String testName) {
        Path path = Paths.get(StructureUtils.testStructuresDir);
        ResourceLocation resourceLocation = new ResourceLocation(testName);
        Path path2 = source.getLevel().getStructureManager().getPathToGeneratedStructure(resourceLocation, ".nbt");
        Path path3 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, path2, resourceLocation.getPath(), path);
        if (path3 == null) {
            say(source, "Failed to export " + path2);
            return 1;
        } else {
            try {
                FileUtil.createDirectoriesSafe(path3.getParent());
            } catch (IOException var7) {
                say(source, "Could not create folder " + path3.getParent());
                LOGGER.error("Could not create export folder", (Throwable)var7);
                return 1;
            }

            say(source, "Exported " + testName + " to " + path3.toAbsolutePath());
            return 0;
        }
    }

    private static int importTestStructure(CommandSourceStack source, String testName) {
        Path path = Paths.get(StructureUtils.testStructuresDir, testName + ".snbt");
        ResourceLocation resourceLocation = new ResourceLocation(testName);
        Path path2 = source.getLevel().getStructureManager().getPathToGeneratedStructure(resourceLocation, ".nbt");

        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string = IOUtils.toString(bufferedReader);
            Files.createDirectories(path2.getParent());

            try (OutputStream outputStream = Files.newOutputStream(path2)) {
                NbtIo.writeCompressed(NbtUtils.snbtToStructure(string), outputStream);
            }

            say(source, "Imported to " + path2.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException var12) {
            LOGGER.error("Failed to load structure {}", testName, var12);
            return 1;
        }
    }

    private static void say(ServerLevel world, String message, ChatFormatting formatting) {
        world.getPlayers(player -> true).forEach(player -> player.sendSystemMessage(Component.literal(message).withStyle(formatting)));
    }

    static class TestSummaryDisplayer implements GameTestListener {
        private final ServerLevel level;
        private final MultipleTestTracker tracker;

        public TestSummaryDisplayer(ServerLevel world, MultipleTestTracker tests) {
            this.level = world;
            this.tracker = tests;
        }

        @Override
        public void testStructureLoaded(GameTestInfo test) {
        }

        @Override
        public void testPassed(GameTestInfo test) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }

        @Override
        public void testFailed(GameTestInfo test) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }
    }
}
