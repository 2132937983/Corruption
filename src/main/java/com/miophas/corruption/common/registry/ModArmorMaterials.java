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

    private ModArmorMaterials() {}
}
