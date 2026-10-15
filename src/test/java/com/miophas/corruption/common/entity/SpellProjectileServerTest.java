package com.miophas.corruption.common.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.miophas.corruption.api.arcane.ArcaneCasting;
import com.miophas.corruption.common.registry.ModEntities;
import com.miophas.corruption.common.registry.ModParticles;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** A spell bolt carries out what it carries where it strikes, not where its caster stands, and shows itself as its spell says. */
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

    @Test void aBoltShowsItselfAsItsSpellSaysAndTellsTheClientsSo(MinecraftServer server) {
        TestWorld.onServer(server, level -> {
            List<SpellAction.Particles> fire = List.of(new SpellAction.Particles("minecraft:flame", 3, .12, .01), new SpellAction.Particles("minecraft:smoke", 1, .1, .02));
            List<SpellAction.Particles> frost = List.of(new SpellAction.Particles("minecraft:snowflake", 20, .5, .05), new SpellAction.Particles("minecraft:dust", 8, .4, 0, 0x88CCFF));
            SpellProjectile bolt = ModEntities.SPELL_BOLT.get().create(level);
            bolt.flying(new SpellAction.LaunchBolt("corruption:spell_bolt", 1.5, 0, List.of(), "minecraft:fire_charge", fire, frost));
            assertTrue(bolt.getItem().is(Items.FIRE_CHARGE));
            assertEquals(fire, bolt.trail());
            assertEquals(frost, bolt.burst());

            RegistryFriendlyByteBuf wire = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            bolt.writeSpawnData(wire);
            SpellProjectile seen = ModEntities.SPELL_BOLT.get().create(level);
            seen.readSpawnData(wire);
            assertTrue(seen.getItem().is(Items.FIRE_CHARGE), "the clients see it drawn as its spell says, not as the corruption's crystal");
            assertEquals(fire, seen.trail(), "and trailing what it trails");

            CompoundTag saved = new CompoundTag();
            bolt.saveWithoutId(saved);
            SpellProjectile loaded = ModEntities.SPELL_BOLT.get().create(level);
            loaded.load(saved);
            assertTrue(loaded.getItem().is(Items.FIRE_CHARGE));
            assertEquals(fire, loaded.trail(), "saved with the world, its look, its trail");
            assertEquals(frost, loaded.burst(), "and its burst");

            SpellProjectile plain = ModEntities.SPELL_BOLT.get().create(level);
            plain.carrying(List.of());
            assertEquals(SpellAction.LaunchBolt.CORRUPTION_TRAIL, plain.trail(), "a bolt its spell says nothing of trails the corruption's motes");
            assertTrue(plain.burst().isEmpty());
        });
    }

    @Test void aParticleOfTheDataIsDrawnAsTheGameKnowsIt() {
        assertSame(ParticleTypes.FLAME, SpellProjectile.options(new SpellAction.Particles("minecraft:flame", 1, 0, 0)));
        assertSame(ModParticles.CORRUPTION_PARTICLE.get(), SpellProjectile.options(new SpellAction.Particles("corruption:corruption_particle", 1, 0, 0)));
        DustParticleOptions dust = (DustParticleOptions) SpellProjectile.options(new SpellAction.Particles("minecraft:dust", 1, 0, 0, 0xFF0000));
        assertEquals(1F, dust.getColor().x(), 1e-6, "dust in the colour asked");
        assertEquals(0F, dust.getColor().y(), 1e-6);
        assertNotNull(SpellProjectile.options(new SpellAction.Particles("minecraft:dust", 1, 0, 0)), "and violet if none is");
        assertNull(SpellProjectile.options(new SpellAction.Particles("minecraft:block", 1, 0, 0)), "one that needs more than an id and a colour is passed over");
        assertNull(SpellProjectile.options(new SpellAction.Particles("corruption:no_such_particle", 1, 0, 0)));
    }
}

