package org.bukkit.craftbukkit.v1_20_R3.util;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import javax.annotation.Nonnull;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.bukkit.Material;
import org.bukkit.plugin.AuthorNagException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

public class Commodore {

    private static final Set<String> EVIL = new HashSet<>(Arrays.asList(
            "org/bukkit/World (III)I getBlockTypeIdAt",
            "org/bukkit/World (Lorg/bukkit/Location;)I getBlockTypeIdAt",
            "org/bukkit/block/Block ()I getTypeId",
            "org/bukkit/block/Block (I)Z setTypeId",
            "org/bukkit/block/Block (IZ)Z setTypeId",
            "org/bukkit/block/Block (IBZ)Z setTypeIdAndData",
            "org/bukkit/block/Block (B)V setData",
            "org/bukkit/block/Block (BZ)V setData",
            "org/bukkit/inventory/ItemStack ()I getTypeId",
            "org/bukkit/inventory/ItemStack (I)V setTypeId"
    ));

    private static final Map<String, String> RENAMES = Map.of(
            "org/bukkit/entity/TextDisplay$TextAligment", "org/bukkit/entity/TextDisplay$TextAlignment", // SPIGOT-7335
            "org/spigotmc/event/entity/EntityMountEvent", "org/bukkit/event/entity/EntityMountEvent",
            "org/spigotmc/event/entity/EntityDismountEvent", "org/bukkit/event/entity/EntityDismountEvent"
    );

    // Paper start - Plugin rewrites
    private static final String CB_PACKAGE = org.bukkit.Bukkit.getServer().getClass().getPackageName().replace('.', '/');
    private static final Map<String, String> SEARCH_AND_REMOVE = initReplacementsMap();
    private static Map<String, String> initReplacementsMap() {
        Map<String, String> getAndRemove = new HashMap<>();
        // Be wary of maven shade's relocations

        final java.util.jar.Manifest manifest = io.papermc.paper.util.JarManifests.manifest(Commodore.class);
        if (Boolean.getBoolean( "debug.rewriteForIde") && manifest != null)
        {
            // unversion incoming calls for pre-relocate debug work
            final String NMS_REVISION_PACKAGE = "v" + manifest.getMainAttributes().getValue("CraftBukkit-Package-Version") + "/";

            getAndRemove.put("org/bukkit/".concat("craftbukkit/" + NMS_REVISION_PACKAGE), NMS_REVISION_PACKAGE);
        }

        return getAndRemove;
    }

    @Nonnull
    private static String getOriginalOrRewrite(@Nonnull String original)
    {
        String rewrite = null;
        for ( Map.Entry<String, String> entry : SEARCH_AND_REMOVE.entrySet() )
        {
            if ( original.contains( entry.getKey() ) )
            {
                rewrite = original.replace( entry.getValue(), "" );
            }
        }

        return rewrite != null ? rewrite : original;
    }
    // Paper end - Plugin rewrites

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<File> inputFlag = parser.acceptsAll(Arrays.asList("i", "input")).withRequiredArg().ofType(File.class).required();
        OptionSpec<File> outputFlag = parser.acceptsAll(Arrays.asList("o", "output")).withRequiredArg().ofType(File.class).required();

        OptionSet options = parser.parse(args);

        File input = options.valueOf(inputFlag);
        File output = options.valueOf(outputFlag);

        if (input.isDirectory()) {
            if (!output.isDirectory()) {
                System.err.println("If input directory specified, output directory required too");
                return;
            }

            for (File in : input.listFiles()) {
                if (in.getName().endsWith(".jar")) {
                    Commodore.convert(in, new File(output, in.getName()));
                }
            }
        } else {
            Commodore.convert(input, output);
        }
    }

    private static void convert(File in, File out) {
        System.out.println("Attempting to convert " + in + " to " + out);

        try {
            try (JarFile inJar = new JarFile(in, false)) {
                JarEntry entry = inJar.getJarEntry(".commodore");
                if (entry != null) {
                    return;
                }

                try (JarOutputStream outJar = new JarOutputStream(new FileOutputStream(out))) {
                    for (Enumeration<JarEntry> entries = inJar.entries(); entries.hasMoreElements();) {
                        entry = entries.nextElement();

                        try (InputStream is = inJar.getInputStream(entry)) {
                            byte[] b = ByteStreams.toByteArray(is);

                            if (entry.getName().endsWith(".class")) {
                                b = Commodore.convert(b, false);
                                entry = new JarEntry(entry.getName());
                            }

                            outJar.putNextEntry(entry);
                            outJar.write(b);
                        }
                    }

                    outJar.putNextEntry(new ZipEntry(".commodore"));
                }
            }
        } catch (Exception ex) {
            System.err.println("Fatal error trying to convert " + in);
            ex.printStackTrace();
        }
    }

    public static byte[] convert(byte[] b, final boolean modern) {
        ClassReader cr = new ClassReader(b);
        ClassWriter cw = new ClassWriter(cr, 0);

        cr.accept(new ClassRemapper(new ClassVisitor(Opcodes.ASM9, cw) {

            // Paper start - Rewrite plugins
            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
            {
                desc = getOriginalOrRewrite(desc);
                if ( signature != null ) {
                    signature = getOriginalOrRewrite(signature);
                }

                return super.visitField( access, name, desc, signature, value) ;
            }
            // Paper end

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new MethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions)) {

                    // Paper start - Plugin rewrites
                    @Override
                    public void visitTypeInsn(int opcode, String type) {
                        type = getOriginalOrRewrite(type);

                        super.visitTypeInsn(opcode, type);
                    }

                    @Override
                    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
                        for (int i = 0; i < local.length; i++)
                        {
                            if (!(local[i] instanceof String)) { continue; }

                            local[i] = getOriginalOrRewrite((String) local[i]);
                        }

                        for (int i = 0; i < stack.length; i++)
                        {
                            if (!(stack[i] instanceof String)) { continue; }

                            stack[i] = getOriginalOrRewrite((String) stack[i]);
                        }

                        super.visitFrame(type, nLocal, local, nStack, stack);
                    }

                    @Override
                    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                        descriptor = getOriginalOrRewrite(descriptor);

                        super.visitLocalVariable(name, descriptor, signature, start, end, index);
                    }
                    // Paper end

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                        // Paper start - Rewrite plugins
                        owner = getOriginalOrRewrite(owner);
                        if (desc != null) {
                            desc = getOriginalOrRewrite(desc);
                        }
                        // Paper end

                        // Paper start - DisplaySlot
                        if (owner.equals("org/bukkit/scoreboard/DisplaySlot")) {
                            if (name.startsWith("SIDEBAR_") && !name.startsWith("SIDEBAR_TEAM_")) {
                                super.visitFieldInsn(opcode, owner, name.replace("SIDEBAR_", "SIDEBAR_TEAM_"), desc);
                                return;
                            }
                        }
                        // Paper end - DisplaySlot
                        if (owner.equals("org/bukkit/block/Biome")) {
                            switch (name) {
                                case "NETHER":
                                    super.visitFieldInsn(opcode, owner, "NETHER_WASTES", desc);
                                    return;
                                case "TALL_BIRCH_FOREST":
                                    super.visitFieldInsn(opcode, owner, "OLD_GROWTH_BIRCH_FOREST", desc);
                                    return;
                                case "GIANT_TREE_TAIGA":
                                    super.visitFieldInsn(opcode, owner, "OLD_GROWTH_PINE_TAIGA", desc);
                                    return;
                                case "GIANT_SPRUCE_TAIGA":
                                    super.visitFieldInsn(opcode, owner, "OLD_GROWTH_SPRUCE_TAIGA", desc);
                                    return;
                                case "SNOWY_TUNDRA":
                                    super.visitFieldInsn(opcode, owner, "SNOWY_PLAINS", desc);
                                    return;
                                case "JUNGLE_EDGE":
                                    super.visitFieldInsn(opcode, owner, "SPARSE_JUNGLE", desc);
                                    return;
                                case "STONE_SHORE":
                                    super.visitFieldInsn(opcode, owner, "STONY_SHORE", desc);
                                    return;
                                case "MOUNTAINS":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_HILLS", desc);
                                    return;
                                case "WOODED_MOUNTAINS":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_FOREST", desc);
                                    return;
                                case "GRAVELLY_MOUNTAINS":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_GRAVELLY_HILLS", desc);
                                    return;
                                case "SHATTERED_SAVANNA":
                                    super.visitFieldInsn(opcode, owner, "WINDSWEPT_SAVANNA", desc);
                                    return;
                                case "WOODED_BADLANDS_PLATEAU":
                                    super.visitFieldInsn(opcode, owner, "WOODED_BADLANDS", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/entity/EntityType")) {
                            switch (name) {
                                case "PIG_ZOMBIE":
                                    super.visitFieldInsn(opcode, owner, "ZOMBIFIED_PIGLIN", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/loot/LootTables")) {
                            switch (name) {
                                case "ZOMBIE_PIGMAN":
                                    super.visitFieldInsn(opcode, owner, "ZOMBIFIED_PIGLIN", desc);
                                    return;
                            }
                        }

                        if (modern) {
                            if (owner.equals("org/bukkit/Material")) {
                                switch (name) {
                                    case "CACTUS_GREEN":
                                        name = "GREEN_DYE";
                                        break;
                                    case "DANDELION_YELLOW":
                                        name = "YELLOW_DYE";
                                        break;
                                    case "ROSE_RED":
                                        name = "RED_DYE";
                                        break;
                                    case "SIGN":
                                        name = "OAK_SIGN";
                                        break;
                                    case "WALL_SIGN":
                                        name = "OAK_WALL_SIGN";
                                        break;
                                    case "ZOMBIE_PIGMAN_SPAWN_EGG":
                                        name = "ZOMBIFIED_PIGLIN_SPAWN_EGG";
                                        break;
                                    case "GRASS_PATH":
                                        name = "DIRT_PATH";
                                        break;
                                    case "GRASS":
                                        name = "SHORT_GRASS";
                                        break;
                                }
                            }

                            super.visitFieldInsn(opcode, owner, name, desc);
                            return;
                        }

                        if (owner.equals("org/bukkit/Material")) {
                            try {
                                Material.valueOf("LEGACY_" + name);
                            } catch (IllegalArgumentException ex) {
                                throw new AuthorNagException("No legacy enum constant for " + name + ". Did you forget to define a modern (1.13+) api-version in your plugin.yml?");
                            }

                            super.visitFieldInsn(opcode, owner, "LEGACY_" + name, desc);
                            return;
                        }

                        if (owner.equals("org/bukkit/Art")) {
                            switch (name) {
                                case "BURNINGSKULL":
                                    super.visitFieldInsn(opcode, owner, "BURNING_SKULL", desc);
                                    return;
                                case "DONKEYKONG":
                                    super.visitFieldInsn(opcode, owner, "DONKEY_KONG", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/DyeColor")) {
                            switch (name) {
                                case "SILVER":
                                    super.visitFieldInsn(opcode, owner, "LIGHT_GRAY", desc);
                                    return;
                            }
                        }

                        if (owner.equals("org/bukkit/Particle")) {
                            switch (name) {
                                case "BLOCK_CRACK":
                                case "BLOCK_DUST":
                                case "FALLING_DUST":
                                    super.visitFieldInsn(opcode, owner, "LEGACY_" + name, desc);
                                    return;
                            }
                        }

                        super.visitFieldInsn(opcode, owner, name, desc);
                    }

                    private void handleMethod(MethodPrinter visitor, int opcode, String owner, String name, String desc, boolean itf, Type samMethodType, Type instantiatedMethodType) {
                        // SPIGOT-4496
                        if (owner.equals("org/bukkit/map/MapView") && name.equals("getId") && desc.equals("()S")) {
                            // Should be same size on stack so just call other method
                            visitor.visit(opcode, owner, name, "()I", itf, samMethodType, Type.getMethodType("(Lorg/bukkit/map/MapView;)Ljava/lang/Integer;"));
                            return;
                        }
                        // SPIGOT-4608
                        if ((owner.equals("org/bukkit/Bukkit") || owner.equals("org/bukkit/Server")) && name.equals("getMap") && desc.equals("(S)Lorg/bukkit/map/MapView;")) {
                            // Should be same size on stack so just call other method
                            visitor.visit(opcode, owner, name, "(I)Lorg/bukkit/map/MapView;", itf, samMethodType, instantiatedMethodType);
                            return;
                        }

                        if (owner.startsWith("org/bukkit") && desc.contains("org/bukkit/util/Consumer")) {
                            visitor.visit(opcode, owner, name, desc.replace("org/bukkit/util/Consumer", "java/util/function/Consumer"), itf, samMethodType, instantiatedMethodType);
                            return;
                        }

                        // Paper start - Rewrite plugins
                        owner = getOriginalOrRewrite(owner) ;
                        if (desc != null) {
                            desc = getOriginalOrRewrite(desc);
                        }
                        if ((owner.equals("org/bukkit/OfflinePlayer") || owner.equals("org/bukkit/entity/Player")) && name.equals("getPlayerProfile") && desc.equals("()Lorg/bukkit/profile/PlayerProfile;")) {
                            super.visitMethodInsn(opcode, owner, name, "()Lcom/destroystokyo/paper/profile/PlayerProfile;", itf);
                            return;
                        }
                        if (owner.equals("org/bukkit/advancement/Advancement") && name.equals("getDisplay") && desc.endsWith(")Lorg/bukkit/advancement/AdvancementDisplay;")) {
                            super.visitTypeInsn(Opcodes.CHECKCAST, CB_PACKAGE + "/advancement/CraftAdvancement");
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CB_PACKAGE + "/advancement/CraftAdvancement", "getDisplay0", desc, false);
                            return;
                        }
                        if (owner.equals("org/bukkit/WorldCreator") && name.equals("keepSpawnLoaded") && desc.equals("(Lnet/kyori/adventure/util/TriState;)V")) {
                            super.visitMethodInsn(opcode, owner, name, "(Lnet/kyori/adventure/util/TriState;)Lorg/bukkit/WorldCreator;", itf);
                            // new method has a return, so, make sure we pop it
                            super.visitInsn(Opcodes.POP);
                            return;
                        }
                        // Paper end

                        // Paper start - ItemFactory#getSpawnEgg (paper had original method that returned ItemStack, upstream added identical but returned Material)
                        if (owner.equals("org/bukkit/inventory/ItemFactory") && name.equals("getSpawnEgg") && desc.equals("(Lorg/bukkit/entity/EntityType;)Lorg/bukkit/inventory/ItemStack;")) {
                            super.visitInsn(Opcodes.SWAP); // has 1 param, this moves the owner instance to the top for the checkcast
                            super.visitTypeInsn(Opcodes.CHECKCAST, CB_PACKAGE + "/inventory/CraftItemFactory");
                            super.visitInsn(Opcodes.SWAP); // moves param back to the the top of stack
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, CB_PACKAGE + "/inventory/CraftItemFactory", "getSpawnEgg0", desc, false);
                            return;
                        }
                        // Paper end - ItemFactory#getSpawnEgg
                        if (modern) {
                            if (owner.equals("org/bukkit/Material") || (instantiatedMethodType != null && instantiatedMethodType.getDescriptor().startsWith("(Lorg/bukkit/Material;)"))) {
                                switch (name) {
                                    case "values":
                                        visitor.visit(opcode, "org/bukkit/craftbukkit/v1_20_R3/util/CraftLegacy", "modern_" + name, desc, itf, samMethodType, instantiatedMethodType);
                                        return;
                                    case "ordinal":
                                        visitor.visit(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/v1_20_R3/util/CraftLegacy", "modern_" + name, "(Lorg/bukkit/Material;)I", false, samMethodType, instantiatedMethodType);
                                        return;
                                }
                            }

                            visitor.visit(opcode, owner, name, desc, itf, samMethodType, instantiatedMethodType);
                            return;
                        }

                        if (owner.equals("org/bukkit/ChunkSnapshot") && name.equals("getBlockData") && desc.equals("(III)I")) {
                            visitor.visit(opcode, owner, "getData", desc, itf, samMethodType, instantiatedMethodType);
                            return;
                        }

                        Type retType = Type.getReturnType(desc);

                        if (Commodore.EVIL.contains(owner + " " + desc + " " + name)
                                || (owner.startsWith("org/bukkit/block/") && (desc + " " + name).equals("()I getTypeId"))
                                || (owner.startsWith("org/bukkit/block/") && (desc + " " + name).equals("(I)Z setTypeId"))
                                || (owner.startsWith("org/bukkit/block/") && (desc + " " + name).equals("()Lorg/bukkit/Material; getType"))) {
                            Type[] args = Type.getArgumentTypes(desc);
                            Type[] newArgs = new Type[args.length + 1];
                            newArgs[0] = Type.getObjectType(owner);
                            System.arraycopy(args, 0, newArgs, 1, args.length);

                            visitor.visit(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/v1_20_R3/legacy/CraftEvil", name, Type.getMethodDescriptor(retType, newArgs), false, samMethodType, instantiatedMethodType);
                            return;
                        }

                        if (owner.equals("org/bukkit/DyeColor")) {
                            if (name.equals("valueOf") && desc.equals("(Ljava/lang/String;)Lorg/bukkit/DyeColor;")) {
                                visitor.visit(opcode, owner, "legacyValueOf", desc, itf, samMethodType, instantiatedMethodType);
                                return;
                            }
                        }

                        if (owner.equals("org/bukkit/Material") || (instantiatedMethodType != null && instantiatedMethodType.getDescriptor().startsWith("(Lorg/bukkit/Material;)"))) {
                            if (name.equals("getMaterial") && desc.equals("(I)Lorg/bukkit/Material;")) {
                                visitor.visit(opcode, "org/bukkit/craftbukkit/v1_20_R3/legacy/CraftEvil", name, desc, itf, samMethodType, instantiatedMethodType);
                                return;
                            }

                            switch (name) {
                                case "values":
                                case "valueOf":
                                case "getMaterial":
                                case "matchMaterial":
                                    visitor.visit(opcode, "org/bukkit/craftbukkit/v1_20_R3/legacy/CraftLegacy", name, desc, itf, samMethodType, instantiatedMethodType);
                                    return;
                                case "ordinal":
                                    visitor.visit(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/v1_20_R3/legacy/CraftLegacy", "ordinal", "(Lorg/bukkit/Material;)I", false, samMethodType, instantiatedMethodType);
                                    return;
                                case "name":
                                case "toString":
                                    visitor.visit(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/v1_20_R3/legacy/CraftLegacy", name, "(Lorg/bukkit/Material;)Ljava/lang/String;", false, samMethodType, instantiatedMethodType);
                                    return;
                            }
                        }

                        // TODO: 4/23/23 Handle for InvokeDynamicInsn, does not directly work, since it adds a new method call which InvokeDynamicInsn does not like
                        // The time required to fixe this is probably higher than the return,
                        // One possible way could be to write a custom method and delegate the dynamic call to it,
                        // the method would be needed to be written with asm, to account for different amount of arguments and which normally should be visited
                        // Or a custom factory is created, this would be a very fancy (but probably overkill) solution
                        // Anyway, I encourage everyone who is reading this to to give it a shot
                        if (instantiatedMethodType == null && retType.getSort() == Type.OBJECT && retType.getInternalName().equals("org/bukkit/Material") && owner.startsWith("org/bukkit")) {
                            visitor.visit(opcode, owner, name, desc, itf, samMethodType, instantiatedMethodType);
                            visitor.visit(Opcodes.INVOKESTATIC, "org/bukkit/craftbukkit/v1_20_R3/legacy/CraftLegacy", "toLegacy", "(Lorg/bukkit/Material;)Lorg/bukkit/Material;", false, samMethodType, instantiatedMethodType);
                            return;
                        }

                        visitor.visit(opcode, owner, name, desc, itf, samMethodType, instantiatedMethodType);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        this.handleMethod((newOpcode, newOwner, newName, newDescription, newItf, newSam, newInstantiated) -> {
                            super.visitMethodInsn(newOpcode, newOwner, newName, newDescription, newItf);
                        }, opcode, owner, name, desc, itf, null, null);
                    }

                    @Override
                    public void visitLdcInsn(Object value) {
                        // Paper start
                        if (value instanceof Type type) {
                            if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
                                value = Type.getType(getOriginalOrRewrite(type.getDescriptor()));
                            }
                        }
                        // Paper end
                        if (value instanceof String && ((String) value).equals("com.mysql.jdbc.Driver")) {
                            super.visitLdcInsn("com.mysql.cj.jdbc.Driver");
                            return;
                        }

                        super.visitLdcInsn(value);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                        // Paper start - Rewrite plugins
                        name = getOriginalOrRewrite(name);
                        if (descriptor != null) {
                            descriptor = getOriginalOrRewrite(descriptor);
                        }
                        final String fName = name;
                        final String fDescriptor = descriptor;
                        // Paper end - Rewrite plugins
                        if (bootstrapMethodHandle.getOwner().equals("java/lang/invoke/LambdaMetafactory")
                                && bootstrapMethodHandle.getName().equals("metafactory") && bootstrapMethodArguments.length == 3) {
                            Type samMethodType = (Type) bootstrapMethodArguments[0];
                            Handle implMethod = (Handle) bootstrapMethodArguments[1];
                            Type instantiatedMethodType = (Type) bootstrapMethodArguments[2];

                            this.handleMethod((newOpcode, newOwner, newName, newDescription, newItf, newSam, newInstantiated) -> {
                                if (newOpcode == Opcodes.INVOKESTATIC) {
                                    newOpcode = Opcodes.H_INVOKESTATIC;
                                }

                                List<Object> methodArgs = new ArrayList<>();
                                methodArgs.add(newSam);
                                methodArgs.add(new Handle(newOpcode, newOwner, newName, newDescription, newItf));
                                methodArgs.add(newInstantiated);

                                super.visitInvokeDynamicInsn(fName, fDescriptor, bootstrapMethodHandle, methodArgs.toArray(Object[]::new)); // Paper - use final local vars
                            }, implMethod.getTag(), implMethod.getOwner(), implMethod.getName(), implMethod.getDesc(), implMethod.isInterface(), samMethodType, instantiatedMethodType);
                            return;
                        }

                        // TODO: 4/24/23 Handle other factories, other than LambdaMetafactory
                        // for example the String StringConcatFactory, which handles string concatenation
                        // -> System.out.println("Some" + hello);
                        // But as with the todo above, I encourage everyone who is reading this to to give it a shot
                        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                    }
                };
            }
        }, new SimpleRemapper(Commodore.RENAMES)), 0);

        return cw.toByteArray();
    }

    @FunctionalInterface
    private interface MethodPrinter {

        void visit(int opcode, String owner, String name, String description, boolean itf, Type samMethodType, Type instantiatedMethodType);
    }
}