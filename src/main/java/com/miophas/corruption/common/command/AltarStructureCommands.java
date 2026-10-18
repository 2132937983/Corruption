package com.miophas.corruption.common.command;

import com.miophas.corruption.common.altar.AltarOfferingBlockEntity;
import com.miophas.corruption.common.altar.AltarStructure;
import com.miophas.corruption.core.altar.AltarFacing;
import com.miophas.corruption.core.altar.AltarStructureMap;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * /corruption altar: for those making or trying altar structures. "build" raises a whole altar, its structure
 * round it, facing the way given (or the way whoever runs it looks, as the offering is set by hand); "check"
 * says whether a structure stands round the offering at a place and, if not, which of its blocks are wrong.
 */
public final class AltarStructureCommands {
    /** How many wrong blocks "check" names before saying how many more there are. */
    private static final int NAMED = 8;
    private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType(id -> Component.translatable("commands.corruption.altar.unknown", String.valueOf(id)));
    private static final DynamicCommandExceptionType NO_OFFERING = new DynamicCommandExceptionType(at -> Component.translatable("commands.corruption.altar.no_offering", String.valueOf(at)));
    private static final SuggestionProvider<CommandSourceStack> STRUCTURES = (context, builder) ->
            SharedSuggestionProvider.suggestResource(context.getSource().registryAccess().registryOrThrow(AltarStructure.REGISTRY).keySet(), builder);

    public static LiteralArgumentBuilder<CommandSourceStack> altar() {
        ArgumentBuilder<CommandSourceStack, ?> at = Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(context -> build(context, null));
        for (AltarFacing facing : AltarFacing.values())
            at = at.then(Commands.literal(facing.name().toLowerCase(java.util.Locale.ROOT)).executes(context -> build(context, facing)));
        return Commands.literal("altar")
                .then(Commands.literal("build").then(Commands.argument("structure", ResourceLocationArgument.id()).suggests(STRUCTURES).then(at)))
                .then(Commands.literal("check").then(Commands.argument("structure", ResourceLocationArgument.id()).suggests(STRUCTURES)
                        .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(AltarStructureCommands::check))));
    }

    private static Holder<AltarStructure> structure(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(context, "structure");
        return context.getSource().registryAccess().registryOrThrow(AltarStructure.REGISTRY).getHolder(ResourceKey.create(AltarStructure.REGISTRY, id))
                .orElseThrow(() -> UNKNOWN.create(id));
    }

    private static int build(CommandContext<CommandSourceStack> context, AltarFacing given) throws CommandSyntaxException {
        Holder<AltarStructure> structure = structure(context);
        ServerLevel level = context.getSource().getLevel();
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        Entity entity = context.getSource().getEntity();
        AltarFacing facing = given != null ? given : entity == null ? AltarFacing.NORTH : AltarFacing.valueOf(entity.getDirection().name());
        structure.value().buildWithAltar(level, pos, facing);
        context.getSource().sendSuccess(() -> Component.translatable("commands.corruption.altar.built", AltarStructure.name(structure),
                Component.translatable("corruption.altar.towards." + facing.name().toLowerCase(java.util.Locale.ROOT)), pos.getX(), pos.getY(), pos.getZ()), true);
        return structure.value().cells().size();
    }

    private static int check(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Holder<AltarStructure> structure = structure(context);
        ServerLevel level = context.getSource().getLevel();
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        if (!(level.getBlockEntity(pos) instanceof AltarOfferingBlockEntity offering))
            throw NO_OFFERING.create(pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
        AltarStructure asked = structure.value();
        List<AltarStructureMap.Cell> wrong = asked.wrong(offering.space(level));
        CommandSourceStack source = context.getSource();
        if (wrong.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.corruption.altar.stands", AltarStructure.name(structure), pos.getX(), pos.getY(), pos.getZ()), false);
            return asked.cells().size();
        }
        source.sendFailure(Component.translatable("commands.corruption.altar.wrong", wrong.size(), asked.cells().size(), AltarStructure.name(structure)));
        for (AltarStructureMap.Cell cell : wrong.subList(0, Math.min(NAMED, wrong.size()))) {
            BlockPos at = AltarStructure.place(pos, offering.facing(), cell.east(), cell.up(), cell.north());
            source.sendFailure(Component.translatable("commands.corruption.altar.wrong_at", at.getX(), at.getY(), at.getZ(), asked.part(cell).name()));
        }
        if (wrong.size() > NAMED) source.sendFailure(Component.translatable("commands.corruption.altar.more", wrong.size() - NAMED));
        return 0;
    }

    private AltarStructureCommands() {}
}
