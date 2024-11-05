package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
        name -> Component.translatableEscape("commands.attribute.failed.entity", name)
    );
    private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType(
        (entityName, attributeName) -> Component.translatableEscape("commands.attribute.failed.no_attribute", entityName, attributeName)
    );
    private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType(
        (entityName, attributeName, uuid) -> Component.translatableEscape("commands.attribute.failed.no_modifier", attributeName, entityName, uuid)
    );
    private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType(
        (entityName, attributeName, uuid) -> Component.translatableEscape("commands.attribute.failed.modifier_already_present", uuid, attributeName, entityName)
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
            Commands.literal("attribute")
                .requires(source -> source.hasPermission(2))
                .then(
                    Commands.argument("target", EntityArgument.entity())
                        .then(
                            Commands.argument("attribute", ResourceArgument.resource(registryAccess, Registries.ATTRIBUTE))
                                .then(
                                    Commands.literal("get")
                                        .executes(
                                            context -> getAttributeValue(
                                                    context.getSource(),
                                                    EntityArgument.getEntity(context, "target"),
                                                    ResourceArgument.getAttribute(context, "attribute"),
                                                    1.0
                                                )
                                        )
                                        .then(
                                            Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                .executes(
                                                    context -> getAttributeValue(
                                                            context.getSource(),
                                                            EntityArgument.getEntity(context, "target"),
                                                            ResourceArgument.getAttribute(context, "attribute"),
                                                            DoubleArgumentType.getDouble(context, "scale")
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("base")
                                        .then(
                                            Commands.literal("set")
                                                .then(
                                                    Commands.argument("value", DoubleArgumentType.doubleArg())
                                                        .executes(
                                                            context -> setAttributeBase(
                                                                    context.getSource(),
                                                                    EntityArgument.getEntity(context, "target"),
                                                                    ResourceArgument.getAttribute(context, "attribute"),
                                                                    DoubleArgumentType.getDouble(context, "value")
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("get")
                                                .executes(
                                                    context -> getAttributeBase(
                                                            context.getSource(),
                                                            EntityArgument.getEntity(context, "target"),
                                                            ResourceArgument.getAttribute(context, "attribute"),
                                                            1.0
                                                        )
                                                )
                                                .then(
                                                    Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                        .executes(
                                                            context -> getAttributeBase(
                                                                    context.getSource(),
                                                                    EntityArgument.getEntity(context, "target"),
                                                                    ResourceArgument.getAttribute(context, "attribute"),
                                                                    DoubleArgumentType.getDouble(context, "scale")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("modifier")
                                        .then(
                                            Commands.literal("add")
                                                .then(
                                                    Commands.argument("uuid", UuidArgument.uuid())
                                                        .then(
                                                            Commands.argument("name", StringArgumentType.string())
                                                                .then(
                                                                    Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                        .then(
                                                                            Commands.literal("add")
                                                                                .executes(
                                                                                    context -> addModifier(
                                                                                            context.getSource(),
                                                                                            EntityArgument.getEntity(context, "target"),
                                                                                            ResourceArgument.getAttribute(context, "attribute"),
                                                                                            UuidArgument.getUuid(context, "uuid"),
                                                                                            StringArgumentType.getString(context, "name"),
                                                                                            DoubleArgumentType.getDouble(context, "value"),
                                                                                            AttributeModifier.Operation.ADDITION
                                                                                        )
                                                                                )
                                                                        )
                                                                        .then(
                                                                            Commands.literal("multiply")
                                                                                .executes(
                                                                                    context -> addModifier(
                                                                                            context.getSource(),
                                                                                            EntityArgument.getEntity(context, "target"),
                                                                                            ResourceArgument.getAttribute(context, "attribute"),
                                                                                            UuidArgument.getUuid(context, "uuid"),
                                                                                            StringArgumentType.getString(context, "name"),
                                                                                            DoubleArgumentType.getDouble(context, "value"),
                                                                                            AttributeModifier.Operation.MULTIPLY_TOTAL
                                                                                        )
                                                                                )
                                                                        )
                                                                        .then(
                                                                            Commands.literal("multiply_base")
                                                                                .executes(
                                                                                    context -> addModifier(
                                                                                            context.getSource(),
                                                                                            EntityArgument.getEntity(context, "target"),
                                                                                            ResourceArgument.getAttribute(context, "attribute"),
                                                                                            UuidArgument.getUuid(context, "uuid"),
                                                                                            StringArgumentType.getString(context, "name"),
                                                                                            DoubleArgumentType.getDouble(context, "value"),
                                                                                            AttributeModifier.Operation.MULTIPLY_BASE
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("remove")
                                                .then(
                                                    Commands.argument("uuid", UuidArgument.uuid())
                                                        .executes(
                                                            context -> removeModifier(
                                                                    context.getSource(),
                                                                    EntityArgument.getEntity(context, "target"),
                                                                    ResourceArgument.getAttribute(context, "attribute"),
                                                                    UuidArgument.getUuid(context, "uuid")
                                                                )
                                                        )
                                                )
                                        )
                                        .then(
                                            Commands.literal("value")
                                                .then(
                                                    Commands.literal("get")
                                                        .then(
                                                            Commands.argument("uuid", UuidArgument.uuid())
                                                                .executes(
                                                                    context -> getAttributeModifier(
                                                                            context.getSource(),
                                                                            EntityArgument.getEntity(context, "target"),
                                                                            ResourceArgument.getAttribute(context, "attribute"),
                                                                            UuidArgument.getUuid(context, "uuid"),
                                                                            1.0
                                                                        )
                                                                )
                                                                .then(
                                                                    Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                                        .executes(
                                                                            context -> getAttributeModifier(
                                                                                    context.getSource(),
                                                                                    EntityArgument.getEntity(context, "target"),
                                                                                    ResourceArgument.getAttribute(context, "attribute"),
                                                                                    UuidArgument.getUuid(context, "uuid"),
                                                                                    DoubleArgumentType.getDouble(context, "scale")
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static AttributeInstance getAttributeInstance(Entity entity, Holder<Attribute> attribute) throws CommandSyntaxException {
        AttributeInstance attributeInstance = getLivingEntity(entity).getAttributes().getInstance(attribute);
        if (attributeInstance == null) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create(entity.getName(), getAttributeDescription(attribute));
        } else {
            return attributeInstance;
        }
    }

    private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ERROR_NOT_LIVING_ENTITY.create(entity.getName());
        } else {
            return (LivingEntity)entity;
        }
    }

    private static LivingEntity getEntityWithAttribute(Entity entity, Holder<Attribute> attribute) throws CommandSyntaxException {
        LivingEntity livingEntity = getLivingEntity(entity);
        if (!livingEntity.getAttributes().hasAttribute(attribute)) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create(entity.getName(), getAttributeDescription(attribute));
        } else {
            return livingEntity;
        }
    }

    private static int getAttributeValue(CommandSourceStack source, Entity target, Holder<Attribute> attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = getEntityWithAttribute(target, attribute);
        double d = livingEntity.getAttributeValue(attribute);
        source.sendSuccess(() -> Component.translatable("commands.attribute.value.get.success", getAttributeDescription(attribute), target.getName(), d), false);
        return (int)(d * multiplier);
    }

    private static int getAttributeBase(CommandSourceStack source, Entity target, Holder<Attribute> attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = getEntityWithAttribute(target, attribute);
        double d = livingEntity.getAttributeBaseValue(attribute);
        source.sendSuccess(
            () -> Component.translatable("commands.attribute.base_value.get.success", getAttributeDescription(attribute), target.getName(), d), false
        );
        return (int)(d * multiplier);
    }

    private static int getAttributeModifier(CommandSourceStack source, Entity target, Holder<Attribute> attribute, UUID uuid, double multiplier) throws CommandSyntaxException {
        LivingEntity livingEntity = getEntityWithAttribute(target, attribute);
        AttributeMap attributeMap = livingEntity.getAttributes();
        if (!attributeMap.hasModifier(attribute, uuid)) {
            throw ERROR_NO_SUCH_MODIFIER.create(target.getName(), getAttributeDescription(attribute), uuid);
        } else {
            double d = attributeMap.getModifierValue(attribute, uuid);
            source.sendSuccess(
                () -> Component.translatable(
                        "commands.attribute.modifier.value.get.success",
                        Component.translationArg(uuid),
                        getAttributeDescription(attribute),
                        target.getName(),
                        d
                    ),
                false
            );
            return (int)(d * multiplier);
        }
    }

    private static int setAttributeBase(CommandSourceStack source, Entity target, Holder<Attribute> attribute, double value) throws CommandSyntaxException {
        getAttributeInstance(target, attribute).setBaseValue(value);
        source.sendSuccess(
            () -> Component.translatable("commands.attribute.base_value.set.success", getAttributeDescription(attribute), target.getName(), value), false
        );
        return 1;
    }

    private static int addModifier(
        CommandSourceStack source, Entity target, Holder<Attribute> attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation
    ) throws CommandSyntaxException {
        AttributeInstance attributeInstance = getAttributeInstance(target, attribute);
        AttributeModifier attributeModifier = new AttributeModifier(uuid, name, value, operation);
        if (attributeInstance.hasModifier(attributeModifier)) {
            throw ERROR_MODIFIER_ALREADY_PRESENT.create(target.getName(), getAttributeDescription(attribute), uuid);
        } else {
            attributeInstance.addPermanentModifier(attributeModifier);
            source.sendSuccess(
                () -> Component.translatable(
                        "commands.attribute.modifier.add.success", Component.translationArg(uuid), getAttributeDescription(attribute), target.getName()
                    ),
                false
            );
            return 1;
        }
    }

    private static int removeModifier(CommandSourceStack source, Entity target, Holder<Attribute> attribute, UUID uuid) throws CommandSyntaxException {
        AttributeInstance attributeInstance = getAttributeInstance(target, attribute);
        if (attributeInstance.removePermanentModifier(uuid)) {
            source.sendSuccess(
                () -> Component.translatable(
                        "commands.attribute.modifier.remove.success", Component.translationArg(uuid), getAttributeDescription(attribute), target.getName()
                    ),
                false
            );
            return 1;
        } else {
            throw ERROR_NO_SUCH_MODIFIER.create(target.getName(), getAttributeDescription(attribute), uuid);
        }
    }

    private static Component getAttributeDescription(Holder<Attribute> attribute) {
        return Component.translatable(attribute.value().getDescriptionId());
    }
}
