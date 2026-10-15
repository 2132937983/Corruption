package com.miophas.corruption.common.compat;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.common.entity.TestWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * A creature marked as swinging with no arm, as Spore's creatures mark themselves when they strike, loosing a
 * projectile: whatever reads the arm as Goety does no longer asks for the item in a hand of null.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class SwingingArmGuardServerTest {
    @Test void aCreatureSwingingWithNoArmIsGivenOneBeforeItsProjectileIsSeen(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            Pig striker = EntityType.PIG.create(level);
            striker.swinging = true;
            striker.swingTime = 0;
            assertNull(striker.swingingArm, "as Spore's doHurtTarget leaves it");
            assertThrows(IllegalArgumentException.class, () -> striker.getItemInHand(striker.swingingArm), "the crash: Invalid hand null");

            List<ItemStack> read = new ArrayList<>();
            Consumer<EntityJoinLevelEvent> goety = event -> {
                if (event.getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity owner && owner.swinging)
                    read.add(owner.getItemInHand(owner.swingingArm));
            };
            NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, EntityJoinLevelEvent.class, goety);
            try {
                Snowball loosed = new Snowball(level, striker);
                assertDoesNotThrow(() -> NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(loosed, level)));
                assertEquals(1, read.size(), "Goety's read went ahead");
                assertEquals(InteractionHand.MAIN_HAND, striker.swingingArm, "with the main arm, as the game's own swing would have named");
                assertTrue(striker.swinging, "and the creature still swinging as it was");
            } finally {
                NeoForge.EVENT_BUS.unregister(goety);
            }

            Pig still = EntityType.PIG.create(level);
            SwingingArmGuard.mend(still);
            assertNull(still.swingingArm, "a creature not swinging is left alone");
            still.swing(InteractionHand.OFF_HAND);
            SwingingArmGuard.mend(still);
            assertEquals(InteractionHand.OFF_HAND, still.swingingArm, "and so is one that swings as the game has it");
        });
    }
}
