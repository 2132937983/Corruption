package com.miophas.corruption.architecture;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import static org.junit.jupiter.api.Assertions.*;

/** Checks bytecode references, including lambdas, descriptors and fully qualified calls. */
class LayerDependenciesTest {
    private static final String BASE = "com/miophas/corruption/";
    static Path root() {
        for(Path at=Path.of("").toAbsolutePath();at!=null;at=at.getParent())
            if(Files.isDirectory(at.resolve("src/main/java/"+BASE)))return at;
        throw new AssertionError("Project root not found");
    }
    private static Set<String> references(String name) throws Exception {
        Set<String> references=new HashSet<>();
        try(InputStream bytes=LayerDependenciesTest.class.getClassLoader().getResourceAsStream(name+".class")) {
            assertNotNull(bytes,name);
            new ClassReader(bytes).accept(new ClassRemapper(new ClassVisitor(Opcodes.ASM9){},new Remapper(){
                @Override public String map(String internalName){references.add(internalName);return internalName;}
            }),ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
        }
        return references;
    }
    private static Set<String> classes(String layer) throws Exception {
        Set<String> names=new HashSet<>();Path folder=root().resolve("build/classes/java/main/"+BASE+layer);
        try(var paths=Files.walk(folder)) {
            for(Path path:paths.filter(p->p.toString().endsWith(".class")&&!p.getFileName().toString().equals("package-info.class")).toList())
                names.add(BASE+layer+"/"+folder.relativize(path).toString().replace('\\','/').replace(".class",""));
        }
        return names;
    }
    @Test void coreAndApiNeverReachCommon() throws Exception {
        for(String layer:List.of("core","api"))for(String owner:classes(layer))
            for(String dependency:references(owner))assertFalse(dependency.startsWith(BASE+"common/"),owner+" depends on "+dependency);
    }
    @Test void coreRulesDoNotReachGameCodeThroughApiTypesOrLambdas() throws Exception {
        Set<String> allowedCoordinates=Set.of("net/minecraft/core/BlockPos","net/minecraft/core/BlockPos$MutableBlockPos",
                "net/minecraft/core/Direction","net/minecraft/world/level/ChunkPos");
        for(String owner:classes("core")) {
            Set<String> visited=new HashSet<>();Deque<String> todo=new ArrayDeque<>();todo.add(owner);
            while(!todo.isEmpty())for(String dependency:references(todo.removeFirst())) {
                if(!visited.add(dependency))continue;
                if(dependency.startsWith("net/minecraft/")||dependency.startsWith("net/neoforged/"))
                    assertTrue(owner.startsWith(BASE+"core/infection/")&&allowedCoordinates.contains(dependency),owner+" reaches game code: "+dependency);
                if(dependency.startsWith(BASE+"api/"))todo.add(dependency);
            }
        }
    }
    @Test void reusableSkillsDoNotReferenceBossPresetsOrConcreteBossEntities() throws Exception {
        for(String owner:List.of("BurrowSkill","RamSkill","VenusSkill"))
            for(String dependency:references(BASE+"common/entity/skill/"+owner)) {
                assertFalse(dependency.startsWith(BASE+"core/demon/")&&!dependency.startsWith(BASE+"core/demon/skill/"),owner+" depends on a boss preset: "+dependency);
                assertFalse(dependency.startsWith(BASE+"common/entity/")&&!dependency.startsWith(BASE+"common/entity/skill/"),owner+" depends on a concrete entity: "+dependency);
            }
    }
}
