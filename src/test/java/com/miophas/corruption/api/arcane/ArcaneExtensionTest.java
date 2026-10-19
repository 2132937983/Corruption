package com.miophas.corruption.api.arcane;

import com.google.gson.JsonParser;
import com.miophas.corruption.core.arcane.SpellAction;
import com.mojang.serialization.JsonOps;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArcaneExtensionTest {
    @Test void anUninitializedCastingBridgeFailsInsteadOfSilentlyAcceptingPaidSpells() throws Exception {
        ClassLoader isolated=new ClassLoader(getClass().getClassLoader()){
            @Override protected Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException {
                if(!name.equals(ArcaneCasting.class.getName())&&!name.startsWith(ArcaneCasting.class.getName()+"$"))return super.loadClass(name,resolve);
                synchronized(getClassLoadingLock(name)){
                    Class<?> found=findLoadedClass(name);
                    if(found==null)try(var input=getParent().getResourceAsStream(name.replace('.','/')+".class")){
                        byte[] bytes=input.readAllBytes();found=defineClass(name,bytes,0,bytes.length);
                    }catch(java.io.IOException failure){throw new ClassNotFoundException(name,failure);}
                    if(resolve)resolveClass(found);return found;
                }
            }
        };
        var bridge=Class.forName(ArcaneCasting.class.getName(),true,isolated);
        var failure=assertThrows(java.lang.reflect.InvocationTargetException.class,()->bridge.getMethod("world").invoke(null));
        assertInstanceOf(IllegalStateException.class,failure.getCause());
    }
    @Test void anAddOnActionRoundTripsAndCoexistsWithNormalActionsAndCorruptionBookkeeping() {
        var id=ResourceLocation.parse("fixture:extension");List<String> calls=new ArrayList<>();
        var parsed=SpellCodecs.ACTION.parse(JsonOps.INSTANCE,JsonParser.parseString("{\"type\":\"custom\",\"id\":\"fixture:extension\",\"parameters\":{\"power\":\"9\"}}")).getOrThrow();
        assertEquals(new SpellAction.Custom(id.toString(),Map.of("power","9")),parsed);
        assertEquals(parsed,SpellCodecs.ACTION.parse(JsonOps.INSTANCE,SpellCodecs.ACTION.encodeStart(JsonOps.INSTANCE,parsed).getOrThrow()).getOrThrow());
        var before=ArcaneCasting.world();
        ArcaneCasting.install(new ArcaneCasting.World(){
            public void perform(ServerPlayer player,ArcaneCasting.Origin origin,SpellAction action){calls.add("builtin");}
            public int corruption(ServerPlayer player){return 7;}
            public void corrupt(ServerPlayer player,int gain){calls.add("gain:"+gain);}
        });
        ArcaneCasting.registerAction(id,(player,origin,action)->calls.add(action.parameters().get("power")));
        try {
            assertThrows(IllegalStateException.class,()->ArcaneCasting.registerAction(id,(p,o,a)->{}));
            var world=ArcaneCasting.world();world.perform(null,null,parsed);world.perform(null,null,new SpellAction.Damage(1,2));
            assertEquals(7,world.corruption(null));world.corrupt(null,2);assertEquals(List.of("9","builtin","gain:2"),calls);
            ArcaneCasting.replaceAction(id,(player,origin,action)->calls.add("replacement"));world.perform(null,null,parsed);
            assertEquals("replacement",calls.getLast());
        } finally { ArcaneCasting.unregisterAction(id);ArcaneCasting.install(before); }
    }
    @Test void overridingOneBuiltInKindLeavesOtherKindsOnTheWorldAdapter() {
        var before=ArcaneCasting.world();List<String> calls=new ArrayList<>();
        ArcaneCasting.install(new ArcaneCasting.World(){
            public void perform(ServerPlayer p,ArcaneCasting.Origin o,SpellAction a){calls.add("default");}
            public int corruption(ServerPlayer p){return 0;}public void corrupt(ServerPlayer p,int gain){}
        });
        ArcaneCasting.registerHandler(SpellAction.Damage.class,(p,o,a)->calls.add("damage"));
        try { var world=ArcaneCasting.world();world.perform(null,null,new SpellAction.Damage(1,1));world.perform(null,null,new SpellAction.Ignite(1,1));assertEquals(List.of("damage","default"),calls); }
        finally {ArcaneCasting.unregisterHandler(SpellAction.Damage.class);ArcaneCasting.install(before);}
    }
}
