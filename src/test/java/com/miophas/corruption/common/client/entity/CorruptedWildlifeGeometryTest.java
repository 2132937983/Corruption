package com.miophas.corruption.common.client.entity;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Modifier;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.junit.jupiter.api.Test;

class CorruptedWildlifeGeometryTest {
    @Test void allExportedMeshesBakeWithTheirAnimationRig() throws Exception {
        int species=0;
        for(var method:CorruptedWildlifeGeometry.class.getDeclaredMethods()){
            if(!Modifier.isStatic(method.getModifiers())||method.getReturnType()!=LayerDefinition.class)continue;
            var root=((LayerDefinition)method.invoke(null)).bakeRoot();
            assertTrue(root.hasChild("corruption_root"),method.getName());
            var parts=CorruptedWildlifeGeometry.parts(root,method.getName());
            assertFalse(parts.isEmpty(),method.getName());
            assertSame(root.getChild("corruption_root"),parts.get("corruption_root"));species++;
        }
        assertEquals(49,species);
    }
    @Test void modifiedAnatomyKeepsMissingLimbsLargeArmsAndJawHinge() {
        var trader=CorruptedWildlifeGeometry.wandering_trader().bakeRoot().getChild("corruption_root");
        assertFalse(trader.hasChild("arms"));assertFalse(trader.hasChild("right_arm"));assertFalse(trader.hasChild("left_arm"));
        var wolf=CorruptedWildlifeGeometry.wolf().bakeRoot().getChild("corruption_root").getChild("head");
        var jaw=wolf.getChild("wolf_upper_jaw").getInitialPose().xRot;assertTrue(jaw>-Math.PI/2&&jaw<0,"upper jaw must tilt upward, not past vertical downward");
        assertTrue(wolf.hasChild("mouth_right_claw")&&wolf.hasChild("mouth_left_claw"));
        var warden=CorruptedWildlifeGeometry.parts(CorruptedWildlifeGeometry.warden().bakeRoot(),"warden");
        assertFalse(warden.containsKey("head_jaw"));
        for(String side:new String[]{"right","left"})assertTrue(warden.containsKey(side+"_arm_lower")&&warden.containsKey(side+"_arm_hand")&&warden.containsKey(side+"_dragging_hand_claw"));
        var snow=CorruptedWildlifeGeometry.parts(CorruptedWildlifeGeometry.snow_golem().bakeRoot(),"snow_golem");
        assertTrue(snow.containsKey("pumpkin_left_half")&&snow.containsKey("pumpkin_right_half"));
        var fish=CorruptedWildlifeGeometry.tropical_fish().bakeRoot().getChild("corruption_root");
        assertTrue(fish.hasChild("fish_state_0")&&fish.hasChild("fish_state_1"));
    }
}
