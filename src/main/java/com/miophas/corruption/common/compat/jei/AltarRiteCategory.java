package com.miophas.corruption.common.compat.jei;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.altar.AltarRecipe;
import com.miophas.corruption.common.altar.GroundBlock;
import com.miophas.corruption.common.client.ArcaneTransmuterScreen;
import com.miophas.corruption.common.client.GuiTiles;
import com.miophas.corruption.common.registry.ModBlocks;
import com.miophas.corruption.core.altar.AltarLayout;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Shows an altar rite laid out as the altar is: the six pillars round the offering, slot 1 at the top (the way
 * the altar faces), the rest clockwise, with the creature to be bound on the offering standing in the middle; and what the rite
 * brings forth, an item, or a creature standing there. Below, the altar's ground is mapped, the way it faces up: the
 * offering, its pillars, and the blocks the rite asks for, which are shown beside it, lighting up on the map
 * when looked at; then how the pillars are filled, that the rest of the ground is clear, and the corruption the
 * rite asks of a player there. Shaped, each ingredient sits on its own pillar; shapeless, they fill the first
 * pillars, as they may lie on any. Creatures are drawn as they are, slowly turning; JEI finds the rite by their
 * spawn eggs, unseen.
 */
public final class AltarRiteCategory implements IRecipeCategory<RecipeHolder<AltarRecipe>> {
    public static final RecipeType<RecipeHolder<AltarRecipe>> TYPE = RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "altar"));
    private static final int WIDTH = 176, HEIGHT = 128;
    /** The pillars round the offering, six pixels a block, and the box the creature bound on it stands in. */
    private static final int MIDDLE_X = 25, MIDDLE_Y = 25, SPREAD = 6, BOUND_X = MIDDLE_X - 4, BOUND_Y = MIDDLE_Y - 4, BOUND_SIZE = 24;
    private static final int ARROW_X = 72, ARROW_Y = 25, OUTPUT_X = 100, OUTPUT_Y = 20;
    /** The box a creature brought forth stands in. */
    private static final int SUMMON_X = 100, SUMMON_Y = 2, SUMMON_WIDTH = 40, SUMMON_HEIGHT = 64;
    /** The map of the ground, a cell to a block, the way the altar faces up. */
    private static final int MAP_X = 3, MAP_Y = 73, CELL = 6, GROUND = AltarLayout.REACH * 2 + 1;
    /** The blocks asked for, right of the map, and the lines of text below them. */
    private static final int BLOCKS_X = 64, BLOCKS_Y = 74, BLOCKS_PER_ROW = 6, LINE = 10;
    private static final int GREY = 0xFF808080, PURPLE = 0xFF8B2FA8, SUNK = 0xFF8B8B8B, SHADE = 0xFF373737, LIGHT = 0xFFFFFFFF;
    private static final int OFFERING = 0xFFB0303A, PILLAR = 0xFF4E4A5C, ASKED = 0xFFD8A936, LIT = 0xFFFFF0A0, CLEAR = 0xFFA8A8A8;
    /** How large a creature may be drawn, in pixels a block. */
    private static final float MOST_SCALE = 32;

    private final IDrawable icon, slot, output;
    /** Creatures made to be drawn, once for each kind, in the level they were made in. */
    private final Map<EntityType<?>, Optional<Entity>> creatures = new HashMap<>();
    private Level creaturesIn;

    public AltarRiteCategory(IGuiHelper gui) {
        icon = gui.createDrawableItemLike(ModBlocks.ALTAR_OFFERING.get());
        slot = gui.getSlotDrawable();
        output = gui.getOutputSlot();
    }

    /** Where a pillar's slot lies: its place round the offering, six pixels a block. */
    private static int[] pillarAt(int slot) {
        return new int[]{MIDDLE_X + AltarLayout.east(slot) * SPREAD, MIDDLE_Y - AltarLayout.north(slot) * SPREAD};
    }

    /** A creature, as an item: its spawn egg, or a name tag bearing its name if it has none. */
    public static ItemStack shown(EntityType<?> type) {
        SpawnEggItem egg = SpawnEggItem.byId(type);
        if (egg != null) return new ItemStack(egg);
        ItemStack tag = new ItemStack(Items.NAME_TAG);
        tag.set(DataComponents.CUSTOM_NAME, type.getDescription());
        return tag;
    }

    /** A creature's spawn egg, if it has one, by which JEI finds the rites it is in. */
    private static Optional<ItemStack> egg(EntityType<?> type) { return Optional.ofNullable(SpawnEggItem.byId(type)).map(ItemStack::new); }

    /** The blocks a rite asks for, those asking for the same together, in its order. */
    private static List<List<GroundBlock>> kinds(AltarRecipe recipe) {
        Map<Either<TagKey<Block>, Block>, List<GroundBlock>> kinds = new LinkedHashMap<>();
        for (GroundBlock block : recipe.blocks()) kinds.computeIfAbsent(block.block(), kind -> new ArrayList<>()).add(block);
        return List.copyOf(kinds.values());
    }

    /** Where the slot of a kind of block asked for lies. */
    private static int[] kindAt(int index) { return new int[]{BLOCKS_X + index % BLOCKS_PER_ROW * 18, BLOCKS_Y + index / BLOCKS_PER_ROW * 18}; }

    /** A line of text below the blocks asked for, and what hovering over it tells, if anything. */
    private record Line(Component text, int colour, Component tip) {}

    private static List<Line> lines(AltarRecipe recipe) {
        List<Line> lines = new ArrayList<>();
        lines.add(recipe.shaped() ? new Line(Component.translatable("corruption.jei.altar_shaped"), GREY, Component.translatable("corruption.jei.altar_numbering"))
                : new Line(Component.translatable("corruption.jei.altar_shapeless"), GREY, null));
        lines.add(new Line(Component.translatable(recipe.blocks().isEmpty() ? "corruption.jei.altar_ground_clear" : "corruption.jei.altar_ground_rest"), GREY,
                Component.translatable("corruption.jei.altar_ground_range", -AltarLayout.REACH, AltarLayout.REACH)));
        if (recipe.corruption() > 0)
            lines.add(new Line(Component.translatable("corruption.jei.altar_corruption", recipe.corruption()), PURPLE, Component.translatable("corruption.jei.altar_corruption_tip")));
        return lines;
    }

    /** Where the lines of text begin: below the blocks asked for, if any are. */
    private static int linesTop(AltarRecipe recipe) {
        int kinds = recipe.blocks().isEmpty() ? 0 : kinds(recipe).size();
        return kinds == 0 ? BLOCKS_Y + 2 : BLOCKS_Y + (kinds + BLOCKS_PER_ROW - 1) / BLOCKS_PER_ROW * 18 + 4;
    }

    /** The place on the ground under the mouse, as {east, north}, or null. */
    private static int[] groundAt(double mouseX, double mouseY) {
        int column = Mth.floor((mouseX - MAP_X) / CELL), row = Mth.floor((mouseY - MAP_Y) / CELL);
        if (column < 0 || row < 0 || column >= GROUND || row >= GROUND) return null;
        return new int[]{column - AltarLayout.REACH, AltarLayout.REACH - row};
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    @Override public RecipeType<RecipeHolder<AltarRecipe>> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("corruption.jei.altar"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AltarRecipe> holder, IFocusGroup focuses) {
        AltarRecipe recipe = holder.value();
        int next = 0;
        for (int index = 0; index < recipe.pillars().size(); index++) {
            if (recipe.pillars().get(index).isEmpty()) continue;
            int pillar = recipe.shaped() ? index : next++;
            int[] at = pillarAt(pillar);
            var slotBuilder = builder.addInputSlot(at[0], at[1]).addItemStacks(List.of(recipe.pillars().get(index).getItems()));
            if (recipe.shaped()) slotBuilder.addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("corruption.jei.altar_slot", pillar + 1).withStyle(ChatFormatting.GRAY)));
        }
        recipe.offering().flatMap(AltarRiteCategory::egg).ifPresent(egg -> builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(egg));
        recipe.result().item().ifPresent(item -> builder.addOutputSlot(OUTPUT_X + 5, OUTPUT_Y + 5).addItemStack(item));
        recipe.result().creature().flatMap(AltarRiteCategory::egg).ifPresent(egg -> builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(egg));
        List<List<GroundBlock>> kinds = kinds(recipe);
        for (int index = 0; index < kinds.size(); index++) {
            List<GroundBlock> kind = kinds.get(index);
            int[] at = kindAt(index);
            builder.addSlot(RecipeIngredientRole.CATALYST, at[0] + 1, at[1] + 1).addItemStacks(kind.get(0).shown())
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("corruption.jei.altar_ground_count", kind.size()).withStyle(ChatFormatting.GOLD)));
        }
        if (!recipe.shaped()) builder.setShapeless();
    }

    @Override public void draw(RecipeHolder<AltarRecipe> holder, IRecipeSlotsView slots, GuiGraphics gui, double mouseX, double mouseY) {
        AltarRecipe recipe = holder.value();
        var font = Minecraft.getInstance().font;
        for (int pillar = 0; pillar < AltarLayout.SLOTS; pillar++) {
            int[] at = pillarAt(pillar);
            slot.draw(gui, at[0] - 1, at[1] - 1);
        }
        sunken(gui, BOUND_X, BOUND_Y, BOUND_SIZE, BOUND_SIZE);
        gui.fill(BOUND_X + 1, BOUND_Y + 1, BOUND_X + BOUND_SIZE - 1, BOUND_Y + BOUND_SIZE - 1, 0x40B05A77);
        recipe.offering().ifPresent(type -> drawCreature(gui, type, BOUND_X + 1, BOUND_Y + 1, BOUND_SIZE - 2, BOUND_SIZE - 2));

        GuiTiles.GUI.bar(gui, ArcaneTransmuterScreen.PROGRESS, ARROW_X, ARROW_Y, (System.currentTimeMillis() / 50 % recipe.time()) / (float) recipe.time(), GuiTiles.Fill.LEFT_TO_RIGHT);
        Component time = Component.translatable("corruption.jei.time", recipe.time() / 20F);
        gui.drawString(font, time, ARROW_X + 11 - font.width(time) / 2, ARROW_Y + 20, GREY, false);
        recipe.result().item().ifPresent(item -> output.draw(gui, OUTPUT_X, OUTPUT_Y));
        recipe.result().creature().ifPresent(type -> {
            sunken(gui, SUMMON_X, SUMMON_Y, SUMMON_WIDTH, SUMMON_HEIGHT);
            drawCreature(gui, type, SUMMON_X + 1, SUMMON_Y + 1, SUMMON_WIDTH - 2, SUMMON_HEIGHT - 2);
        });

        List<List<GroundBlock>> kinds = kinds(recipe);
        List<GroundBlock> lit = List.of();
        for (int index = 0; index < kinds.size(); index++) {
            int[] at = kindAt(index);
            slot.draw(gui, at[0], at[1]);
            if (inside(mouseX, mouseY, at[0], at[1], 18, 18)) lit = kinds.get(index);
        }
        int[] hovered = groundAt(mouseX, mouseY);
        GroundBlock under = hovered == null ? null : recipe.blockAt(hovered[0], hovered[1]);
        if (under != null) for (List<GroundBlock> kind : kinds) if (kind.get(0).block().equals(under.block())) lit = kind;
        drawGround(gui, recipe, lit, hovered);

        List<Line> lines = lines(recipe);
        int top = linesTop(recipe);
        for (int index = 0; index < lines.size(); index++) gui.drawString(font, lines.get(index).text(), BLOCKS_X, top + index * LINE, lines.get(index).colour(), false);
    }

    /** The ground, a cell to a block: the offering, its pillars, the blocks asked for (those lit, brighter), and clear ground. */
    private static void drawGround(GuiGraphics gui, AltarRecipe recipe, List<GroundBlock> lit, int[] hovered) {
        gui.fill(MAP_X - 1, MAP_Y - 1, MAP_X + GROUND * CELL, MAP_Y + GROUND * CELL, SHADE);
        for (int east = -AltarLayout.REACH; east <= AltarLayout.REACH; east++)
            for (int north = -AltarLayout.REACH; north <= AltarLayout.REACH; north++) {
                GroundBlock asked = recipe.blockAt(east, north);
                int colour = east == 0 && north == 0 ? OFFERING : AltarLayout.slotAt(east, north) >= 0 ? PILLAR : asked == null ? CLEAR : lit.contains(asked) ? LIT : ASKED;
                int x = MAP_X + (east + AltarLayout.REACH) * CELL, y = MAP_Y + (AltarLayout.REACH - north) * CELL;
                gui.fill(x, y, x + CELL - 1, y + CELL - 1, colour);
                if (hovered != null && hovered[0] == east && hovered[1] == north) gui.fill(x, y, x + CELL - 1, y + CELL - 1, 0x80FFFFFF);
            }
    }

    /** A box sunk into the page, as slots are drawn. */
    private static void sunken(GuiGraphics gui, int x, int y, int width, int height) {
        gui.fill(x, y, x + width, y + height, SUNK);
        gui.fill(x, y, x + width - 1, y + 1, SHADE);
        gui.fill(x, y, x + 1, y + height - 1, SHADE);
        gui.fill(x + 1, y + height - 1, x + width, y + height, LIGHT);
        gui.fill(x + width - 1, y + 1, x + width, y + height, LIGHT);
    }

    /** A creature of a kind to draw, made once; empty if it cannot be made here. */
    private Optional<Entity> creature(EntityType<?> type) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return Optional.empty();
        if (level != creaturesIn) {
            creatures.clear();
            creaturesIn = level;
        }
        return creatures.computeIfAbsent(type, kind -> {
            try { return Optional.ofNullable(kind.create(level)); }
            catch (RuntimeException cannotBeMade) { return Optional.empty(); }
        });
    }

    /**
     * Draws a creature standing in a box, as large as fits, looked at a little from above and slowly turning; if
     * it cannot be made, its egg instead. As the game draws creatures in its inventory, but cut off by the box
     * wherever the page lies on the screen.
     */
    private void drawCreature(GuiGraphics gui, EntityType<?> type, int x, int y, int width, int height) {
        Entity creature = creature(type).orElse(null);
        if (creature == null) {
            gui.renderItem(shown(type), x + (width - 16) / 2, y + (height - 16) / 2);
            return;
        }
        float scale = Math.min(MOST_SCALE, Math.min((height - 3) / Math.max(creature.getBbHeight(), .1F), (width - 2) / Math.max(creature.getBbWidth() * Mth.SQRT_OF_TWO, .1F)));
        Quaternionf tilt = new Quaternionf().rotateX(15 * Mth.DEG_TO_RAD);
        Quaternionf pose = new Quaternionf().rotateZ(Mth.PI).mul(tilt).rotateY(System.currentTimeMillis() % 9000 / 9000F * Mth.TWO_PI);
        Matrix4f page = gui.pose().last().pose();
        Vector3f from = page.transformPosition(new Vector3f(x, y, 0)), to = page.transformPosition(new Vector3f(x + width, y + height, 0));
        gui.enableScissor(Mth.floor(from.x), Mth.floor(from.y), Mth.ceil(to.x), Mth.ceil(to.y));
        gui.pose().pushPose();
        gui.pose().translate(x + width / 2F, y + height - 2, 50);
        gui.pose().scale(scale, scale, -scale);
        gui.pose().mulPose(pose);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.overrideCameraOrientation(tilt.conjugate(new Quaternionf()).rotateY(Mth.PI));
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(creature, 0, 0, 0, 0, 1, gui.pose(), gui.bufferSource(), LightTexture.FULL_BRIGHT));
        gui.flush();
        dispatcher.setRenderShadow(true);
        gui.pose().popPose();
        Lighting.setupFor3DItems();
        gui.disableScissor();
    }

    /** What lies under the mouse: a creature, a place on the ground, or a line of text that tells more. */
    @Override public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<AltarRecipe> holder, IRecipeSlotsView slots, double mouseX, double mouseY) {
        AltarRecipe recipe = holder.value();
        if (recipe.offering().isPresent() && inside(mouseX, mouseY, BOUND_X, BOUND_Y, BOUND_SIZE, BOUND_SIZE)) {
            tooltip.add(recipe.offering().get().getDescription());
            tooltip.add(Component.translatable("corruption.jei.altar_offering").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }
        if (recipe.result().creature().isPresent() && inside(mouseX, mouseY, SUMMON_X, SUMMON_Y, SUMMON_WIDTH, SUMMON_HEIGHT)) {
            tooltip.add(recipe.result().creature().get().getDescription());
            tooltip.add(Component.translatable("corruption.jei.altar_summons").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }
        int[] place = groundAt(mouseX, mouseY);
        if (place != null) {
            tooltip.add(standing(recipe, place[0], place[1]));
            tooltip.add(Component.literal("(" + place[0] + ", " + place[1] + ")").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        var font = Minecraft.getInstance().font;
        List<Line> lines = lines(recipe);
        int top = linesTop(recipe);
        for (int index = 0; index < lines.size(); index++) {
            Line line = lines.get(index);
            if (line.tip() != null && inside(mouseX, mouseY, BLOCKS_X, top + index * LINE - 1, font.width(line.text()), LINE)) tooltip.add(line.tip());
        }
    }

    /** What stands at a place on the altar's ground, as the rite asks. */
    private static Component standing(AltarRecipe recipe, int east, int north) {
        if (east == 0 && north == 0) return ModBlocks.ALTAR_OFFERING.get().getName().withStyle(ChatFormatting.RED);
        int pillar = AltarLayout.slotAt(east, north);
        if (pillar >= 0) return Component.translatable("corruption.jei.altar_slot", pillar + 1);
        GroundBlock asked = recipe.blockAt(east, north);
        return asked != null ? asked.name().copy().withStyle(ChatFormatting.GOLD) : Component.translatable("corruption.jei.altar_clear").withStyle(ChatFormatting.GRAY);
    }
}
