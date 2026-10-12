package com.miophas.corruption.common.command;

import com.miophas.corruption.api.infection.InfectionSettings;
import com.miophas.corruption.common.config.InfectionConfig;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.function.UnaryOperator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class CorruptionCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("corruption")
                .requires(source -> source.hasPermission(2))
                .executes(context -> show(context.getSource()))
                .then(Commands.literal("enabled")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(context -> change(context.getSource(), settings ->
                                        settings.withEnabled(BoolArgumentType.getBool(context, "value"))))))
                .then(Commands.literal("speed")
                        .executes(context -> show(context.getSource()))
                        .then(Commands.argument("ticks", IntegerArgumentType.integer(
                                InfectionConfig.MIN_SPREAD_INTERVAL, InfectionConfig.MAX_SPREAD_INTERVAL))
                                .executes(context -> change(context.getSource(), settings -> new InfectionSettings(
                                        IntegerArgumentType.getInteger(context, "ticks"), settings.sourcesPerBatch(),
                                        settings.conversionsPerBatch(), settings.spreadChance(),
                                        settings.scanIntervalTicks(), settings.scanBlocksPerBatch(), settings.baseCorruptionChance(), settings.maxSpreadMultiplier(), settings.enabled())))))
                .then(Commands.literal("chance")
                        .then(Commands.argument("probability", DoubleArgumentType.doubleArg(0, 1))
                                .executes(context -> change(context.getSource(), settings -> new InfectionSettings(
                                        settings.spreadIntervalTicks(), settings.sourcesPerBatch(), settings.conversionsPerBatch(),
                                        DoubleArgumentType.getDouble(context, "probability"),
                                        settings.scanIntervalTicks(), settings.scanBlocksPerBatch(), settings.baseCorruptionChance(), settings.maxSpreadMultiplier(), settings.enabled())))))
                .then(Commands.literal("basechance")
                        .then(Commands.argument("probability", DoubleArgumentType.doubleArg(0, 1))
                                .executes(context -> change(context.getSource(), settings -> new InfectionSettings(
                                        settings.spreadIntervalTicks(), settings.sourcesPerBatch(), settings.conversionsPerBatch(),
                                        settings.spreadChance(), settings.scanIntervalTicks(), settings.scanBlocksPerBatch(),
                                        DoubleArgumentType.getDouble(context, "probability"), settings.maxSpreadMultiplier(), settings.enabled())))))
                .then(Commands.literal("limit")
                        .then(Commands.argument("blocks", IntegerArgumentType.integer(1, InfectionConfig.MAX_CONVERSIONS_PER_BATCH))
                                .executes(context -> change(context.getSource(), settings -> new InfectionSettings(
                                        settings.spreadIntervalTicks(), settings.sourcesPerBatch(),
                                        IntegerArgumentType.getInteger(context, "blocks"), settings.spreadChance(),
                                        settings.scanIntervalTicks(), settings.scanBlocksPerBatch(), settings.baseCorruptionChance(), settings.maxSpreadMultiplier(), settings.enabled())))))
                .then(Commands.literal("sources")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, InfectionConfig.MAX_SOURCES_PER_BATCH))
                                .executes(context -> change(context.getSource(), settings -> new InfectionSettings(
                                        settings.spreadIntervalTicks(), IntegerArgumentType.getInteger(context, "count"),
                                        settings.conversionsPerBatch(), settings.spreadChance(),
                                        settings.scanIntervalTicks(), settings.scanBlocksPerBatch(), settings.baseCorruptionChance(), settings.maxSpreadMultiplier(), settings.enabled())))))
                .then(Commands.literal("capacity")
                        .then(Commands.argument("multiplier", IntegerArgumentType.integer(1, 16))
                                .executes(context -> change(context.getSource(), settings -> new InfectionSettings(
                                        settings.spreadIntervalTicks(), settings.sourcesPerBatch(), settings.conversionsPerBatch(),
                                        settings.spreadChance(), settings.scanIntervalTicks(), settings.scanBlocksPerBatch(),
                                        settings.baseCorruptionChance(), IntegerArgumentType.getInteger(context, "multiplier"), settings.enabled())))))
                .then(Commands.literal("reset")
                        .executes(context -> change(context.getSource(), ignored -> InfectionConfig.DEFAULT))));
    }

    private static int change(CommandSourceStack source, UnaryOperator<InfectionSettings> update) {
        InfectionSettings settings = update.apply(InfectionConfig.settings());
        InfectionConfig.saveSettings(source.getServer(), settings);
        source.sendSuccess(() -> Component.translatable("commands.corruption.saved"), true);
        return show(source);
    }

    private static int show(CommandSourceStack source) {
        InfectionSettings settings = InfectionConfig.settings();
        source.sendSuccess(() -> Component.translatable("commands.corruption.settings",
                settings.spreadIntervalTicks(), settings.spreadIntervalTicks() / 20.0,
                settings.spreadChance() * 100.0, settings.sourcesPerBatch(), settings.conversionsPerBatch(),
                settings.baseCorruptionChance() * 100.0, settings.maxSpreadMultiplier(),
                settings.sourcesPerBatch() * settings.maxSpreadMultiplier(),
                settings.conversionsPerBatch() * settings.maxSpreadMultiplier()), false);
        source.sendSuccess(() -> Component.translatable("commands.corruption.enabled",
                Component.translatable(settings.enabled() ? "options.on" : "options.off")), false);
        return settings.spreadIntervalTicks();
    }

    private CorruptionCommands() {}
}
