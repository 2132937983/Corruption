package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Corruption.MOD_ID);

    /**
     * The robe's cloth: as light as leather against blows (it is no armour to speak of), easily enchanted, mended
     * with pure leaves. Worn, it looks as textures/models/armor/robe_layer_1.png and robe_layer_2.png draw it.
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ROBE = MATERIALS.register("robe", () -> new ArmorMaterial(
            new EnumMap<>(java.util.Map.of(ArmorItem.Type.HELMET, 1, ArmorItem.Type.CHESTPLATE, 3, ArmorItem.Type.LEGGINGS, 2, ArmorItem.Type.BOOTS, 1,
                    ArmorItem.Type.BODY, 3)),
            22, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ModItems.PURE_LEAF.get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "robe"))), 0, 0));

    /**
     * The corrupted robes' cloth, in three grades: the novice's a little stouter than the robe it was made from, the
     * intermediate's as iron, the advanced's as diamond and as tough; each more easily enchanted than the last, and each
     * mended with the material its grade is made with. Worn, each looks as textures/models/armor/&lt;grade&gt;_corrupted_robe_layer_1
     * and _layer_2 draw it.
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NOVICE_CORRUPTED_ROBE = corruptedRobe("novice_corrupted_robe",
            new int[]{2, 4, 3, 1}, 20, 0, () -> Ingredient.of(ModItems.SHADOW_SILK.get()));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> INTERMEDIATE_CORRUPTED_ROBE = corruptedRobe("intermediate_corrupted_robe",
            new int[]{2, 6, 5, 2}, 22, 1, () -> Ingredient.of(ModItems.MALICE_TENDRIL.get()));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ADVANCED_CORRUPTED_ROBE = corruptedRobe("advanced_corrupted_robe",
            new int[]{3, 8, 6, 3}, 25, 2, () -> Ingredient.of(ModItems.GREED_HEARTWOOD.get()));

    /**
     * The corruption echo alloy (EchoAlloyRules): netherite's defence and toughness, a little more easily enchanted, mended
     * with the alloy; what it does against the curse is the pieces' own (EchoAlloyArmorItem). Worn, it looks as
     * textures/models/armor/corruption_echo_alloy_layer_1 and _layer_2 draw it.
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ECHO_ALLOY = MATERIALS.register("corruption_echo_alloy", () -> new ArmorMaterial(
            new EnumMap<>(java.util.Map.of(ArmorItem.Type.HELMET, com.miophas.corruption.core.alloy.EchoAlloyRules.DEFENCE[0],
                    ArmorItem.Type.CHESTPLATE, com.miophas.corruption.core.alloy.EchoAlloyRules.DEFENCE[1],
                    ArmorItem.Type.LEGGINGS, com.miophas.corruption.core.alloy.EchoAlloyRules.DEFENCE[2],
                    ArmorItem.Type.BOOTS, com.miophas.corruption.core.alloy.EchoAlloyRules.DEFENCE[3],
                    ArmorItem.Type.BODY, com.miophas.corruption.core.alloy.EchoAlloyRules.DEFENCE[1])),
            com.miophas.corruption.core.alloy.EchoAlloyRules.ARMOUR_ENCHANTABILITY, SoundEvents.ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.of(ModItems.CORRUPTION_ECHO_ALLOY.get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, "corruption_echo_alloy"))),
            com.miophas.corruption.core.alloy.EchoAlloyRules.TOUGHNESS, com.miophas.corruption.core.alloy.EchoAlloyRules.KNOCKBACK_RESISTANCE));

    /** @param defence hood, robe, leggings, boots */
    private static DeferredHolder<ArmorMaterial, ArmorMaterial> corruptedRobe(String name, int[] defence, int enchantability, float toughness,
                                                                             java.util.function.Supplier<Ingredient> repair) {
        return MATERIALS.register(name, () -> new ArmorMaterial(
                new EnumMap<>(java.util.Map.of(ArmorItem.Type.HELMET, defence[0], ArmorItem.Type.CHESTPLATE, defence[1], ArmorItem.Type.LEGGINGS, defence[2],
                        ArmorItem.Type.BOOTS, defence[3], ArmorItem.Type.BODY, defence[1])),
                enchantability, SoundEvents.ARMOR_EQUIP_LEATHER, repair,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Corruption.MOD_ID, name))), toughness, 0));
    }

    private ModArmorMaterials() {}
}
