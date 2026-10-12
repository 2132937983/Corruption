package com.miophas.corruption.common.block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/** Lets native connection calculations see the source identities of neighboring static variants. */
final class OriginalBlockView {
    static BlockState unwrap(BlockState state) {
        return state.getBlock() instanceof StaticCorruptedBlock block ? block.source(state) : state;
    }
    static LevelAccessor of(LevelAccessor level) {
        return (LevelAccessor)Proxy.newProxyInstance(LevelAccessor.class.getClassLoader(),new Class<?>[]{LevelAccessor.class},(proxy,method,args)->{
            try {
                Object result=method.invoke(level,args);
                return method.getName().equals("getBlockState") ? unwrap((BlockState)result) : result;
            } catch(InvocationTargetException exception) { throw exception.getCause(); }
        });
    }
    private OriginalBlockView(){}
}
