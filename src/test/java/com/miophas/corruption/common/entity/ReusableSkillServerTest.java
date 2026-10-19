package com.miophas.corruption.common.entity;

import com.miophas.corruption.api.demon.skill.*;
import com.miophas.corruption.core.demon.SolomonRules;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EphemeralTestServerProvider.class)
class ReusableSkillServerTest {
    /** An unrelated host, deliberately sharing no superclass or implementation with SolomonWyrm. */
    private static final class Host extends Zombie implements BurrowingCaster, BlessingCaster {
        Vec3 goal;double speed;List<LivingEntity> allies=List.of();
        Host(ServerLevel level){super(EntityType.ZOMBIE,level);setNoAi(true);setNoGravity(true);}
        public Vec3 headCentre(){return position().add(0,getBbHeight()/2,0);}
        public Vec3 forward(){return new Vec3(0,0,1);}
        public void face(Vec3 point){}
        public void steer(Vec3 point,double speed,double turn){goal=point;this.speed=speed;}
        public Iterable<? extends LivingEntity> blessingTargets(double radius){return allies;}
        public Vec3 blessingOrigin(){return headCentre();}
        void moveSkill(){if(goal!=null){Vec3 delta=goal.subtract(headCentre());setPos(position().add(delta.normalize().scale(Math.min(speed,delta.length()))));goal=null;}}
    }
    @Test void unrelatedHostUsesThePublicFactoriesForBurrowingRammingAndBlessing(MinecraftServer server) {
        TestWorld.onServer(server,level->{
            int x=650_000,z=12_000;TestWorld.load(level,x-8,x+48,z-8,z+48);
            var host=new Host(level);host.setPos(x,-46,z);level.addFreshEntity(host);
            var ally=EntityType.COW.create(level);ally.setPos(x+2,-60,z);level.addFreshEntity(ally);
            var target=EntityType.VILLAGER.create(level);target.setPos(x,-60,z+24);level.addFreshEntity(target);
            try {
                var burrow=BossSkillApi.burrow(SolomonRules.BURROW,0,0);
                assertTrue(burrow.usable(host,target));burrow.begin(host,target);
                for(int tick=0;tick<400&&!burrow.finished(host);tick++){burrow.tick(host,target,tick);host.moveSkill();}
                assertTrue(burrow.finished(host),"Unrelated host completed the burrowing route");burrow.end(host,false);
                var ram=BossSkillApi.ram(SolomonRules.RAM,0,0);ram.begin(host,target);
                for(int tick=0;tick<400&&!ram.finished(host);tick++){ram.tick(host,target,tick);host.moveSkill();}
                assertTrue(ram.finished(host),"Unrelated host completed the ramming route");ram.end(host,false);
                host.setPos(x,-60,z);host.allies=List.of(host,ally);host.setHealth(5);ally.setHealth(5);
                float targetHealth=target.getHealth();
                var blessing=BossSkillApi.venus(SolomonRules.VENUS,0,0);assertTrue(blessing.usable(host,target));
                blessing.begin(host,target);blessing.tick(host,target,blessing.timing().windup());
                assertTrue(host.getHealth()>5);assertTrue(ally.getHealth()>5,"Blessing host supplies other creature kinds");
                assertEquals(targetHealth,target.getHealth(),"Unselected creatures remain unchanged");
                var unsupported=EntityType.ZOMBIE.create(level);
                assertFalse(burrow.usable(unsupported,target));assertFalse(ram.usable(unsupported,target));
            }finally{host.discard();ally.discard();target.discard();}
        });
    }
}
