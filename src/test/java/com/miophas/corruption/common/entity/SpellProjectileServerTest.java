package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** A spell bolt carries out what it carries where it strikes, not where its caster stands. */
@ExtendWith(EphemeralTestServerProvider.class)
class SpellProjectileServerTest {
    @Test void whatABoltCarriesIsDoneWhereItStrikes(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            TestWorld.load(level, 46_100, 46_120, 3000, 3000);
            FakePlayer caster = FakePlayerFactory.get(level, new GameProfile(UUID.nameUUIDFromBytes("bolt_caster".getBytes()), "bolt_caster"));
            caster.moveTo(46_100.5, -60, 3000.5, 0, 0);
            record Done(ServerPlayer caster, ArcaneCasting.Origin origin, SpellAction action) {}
            List<Done> done = new ArrayList<>();
            ArcaneCasting.World before = ArcaneCasting.world();
            ArcaneCasting.install(new ArcaneCasting.World() {
                @Override public void perform(ServerPlayer who, ArcaneCasting.Origin origin, SpellAction action) { done.add(new Done(who, origin, action)); }
                @Override public int corruption(ServerPlayer who) { return 0; }
                @Override public void corrupt(ServerPlayer who, int gain) {}
            });
            try {
                SpellProjectile bolt = ModEntities.SPELL_BOLT.get().create(level);
                List<SpellAction> payload = List.of(new SpellAction.Damage(6, 2.5), new SpellAction.Corrupt(2, .5));
                bolt.carrying(payload);
                bolt.setOwner(caster);
                bolt.setPos(46_110.5, -59, 3000.5);
                bolt.setDeltaMovement(1.2, 0, 0);
                Vec3 struck = new Vec3(46_118, -59, 3000.5);
                bolt.onHit(new BlockHitResult(struck, Direction.WEST, BlockPos.containing(struck), false));
                assertEquals(payload, done.stream().map(Done::action).toList(), "all it carried, in order");
                for (Done one : done) {
                    assertSame(caster, one.caster(), "on behalf of its caster");
                    assertEquals(struck, one.origin().at(), "round where it struck, eighteen blocks from the caster");
                    assertEquals(new Vec3(1, 0, 0), one.origin().direction(), "facing the way it flew");
                    assertTrue(one.origin().launch().x < struck.x, "a bolt launched there sets off short of the wall");
                }
                assertTrue(bolt.isRemoved(), "and it is spent");
            } finally {
                ArcaneCasting.install(before);
            }
        });
    }
}
