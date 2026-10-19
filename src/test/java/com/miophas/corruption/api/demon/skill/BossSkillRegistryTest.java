package com.miophas.corruption.api.demon.skill;

import com.miophas.corruption.core.demon.skill.SkillTiming;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BossSkillRegistryTest {
    private static final class Skill implements BossSkill<Mob> {
        final int value;Skill(int value){this.value=value;}
        public SkillTiming timing(){return new SkillTiming(0,1,0);}
        public boolean usable(Mob mob,LivingEntity target){return true;}
        public void tick(Mob mob,LivingEntity target,int elapsed){}
    }
    @Test void addOnRegistrationBeforeDefaultSetupSurvivesWithoutChangingOtherSkills() {
        var type=new BossSkillRegistry.Type<>(ResourceLocation.parse("fixture:registry_early"),Integer.class);
        var other=new BossSkillRegistry.Type<>(ResourceLocation.parse("fixture:registry_other"),Integer.class);
        var options=new BossSkillRegistry.Options(0,0);
        BossSkillRegistry.register(type,(rules,shown)->new Skill(rules+100));
        try {
            BossSkillRegistry.registerDefault(type,(rules,shown)->new Skill(rules));
            BossSkillRegistry.registerDefault(other,(rules,shown)->new Skill(rules));
            assertEquals(105,((Skill)BossSkillRegistry.create(type,5,options)).value);
            assertEquals(5,((Skill)BossSkillRegistry.create(other,5,options)).value);
            assertNotSame(BossSkillRegistry.create(type,5,options),BossSkillRegistry.create(type,5,options));
            assertThrows(IllegalStateException.class,()->BossSkillRegistry.register(type,(r,o)->new Skill(r)));
            BossSkillRegistry.replace(type,(r,o)->new Skill(r+20));assertEquals(25,((Skill)BossSkillRegistry.create(type,5,options)).value);
        }finally{BossSkillRegistry.unregister(type.id());}
        assertEquals(5,((Skill)BossSkillRegistry.create(type,5,options)).value);
    }
    @Test void keysCannotBeReusedWithDifferentRuleTypesAndMissingFactoriesFailClearly() {
        var type=new BossSkillRegistry.Type<>(ResourceLocation.parse("fixture:registry_type"),Integer.class);
        var wrong=new BossSkillRegistry.Type<>(type.id(),String.class);
        assertThrows(IllegalStateException.class,()->BossSkillRegistry.create(type,1,new BossSkillRegistry.Options(0,0)));
        BossSkillRegistry.register(type,(r,o)->new Skill(r));
        try{assertThrows(IllegalArgumentException.class,()->BossSkillRegistry.register(wrong,(r,o)->new Skill(0)));}
        finally{BossSkillRegistry.unregister(type.id());}
    }
}
