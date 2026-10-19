package com.miophas.corruption.architecture;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import static org.junit.jupiter.api.Assertions.*;

class AddonBoundaryTest {
    @Test void separatelyCompiledConsumerNeverReferencesCommonOrUnpublishedCoreTypes() throws Exception {
        var root = LayerDependenciesTest.root();
        Set<String> published = new HashSet<>();
        for (String line : Files.readAllLines(root.resolve("src/test/resources/architecture/published-core-contracts.txt")))
            if (line.startsWith("type ")) published.add(line.substring(5).replace('.', '/'));
        var folder = root.resolve("build/classes/java/addonFixture");
        assertTrue(Files.isDirectory(folder), "The JAR consumer must compile before tests");
        try (var paths = Files.walk(folder)) {
            for (var file : paths.filter(path -> path.toString().endsWith(".class")).toList()) {
                new ClassReader(Files.readAllBytes(file)).accept(new ClassRemapper(new ClassVisitor(Opcodes.ASM9) {}, new Remapper() {
                    @Override public String map(String name) {
                        assertFalse(name.startsWith("com/miophas/corruption/common/"), file + " depends on " + name);
                        if (name.startsWith("com/miophas/corruption/core/")) assertTrue(published.contains(name), "Unpublished core contract: " + name);
                        return name;
                    }
                }), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }
    }
}
