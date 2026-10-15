package com.miophas.corruption.common.registry;

import com.miophas.corruption.Corruption;
import com.miophas.corruption.common.alchemy.CorruptionBenchMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Corruption.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<CorruptionBenchMenu>> CORRUPTION_BENCH = MENUS.register("corruption_bench",
            () -> new MenuType<>(CorruptionBenchMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<com.miophas.corruption.common.transmutation.ArcaneTransmuterMenu>> ARCANE_TRANSMUTER = MENUS.register("corruption_arcane_transmuter",
            () -> new MenuType<>(com.miophas.corruption.common.transmutation.ArcaneTransmuterMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<com.miophas.corruption.common.arcane.ArcaneLecternMenu>> ARCANE_LECTERN = MENUS.register("corruption_arcane_lectern",
            () -> new MenuType<>(com.miophas.corruption.common.arcane.ArcaneLecternMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<com.miophas.corruption.common.cooking.BrassCauldronMenu>> BRASS_CAULDRON = MENUS.register("brass_cauldron",
            () -> new MenuType<>(com.miophas.corruption.common.cooking.BrassCauldronMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<com.miophas.corruption.common.arcane.ArcanePedestalMenu>> ARCANE_PEDESTAL = MENUS.register("corruption_arcane_pedestal",
            () -> new MenuType<>(com.miophas.corruption.common.arcane.ArcanePedestalMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {}
}
