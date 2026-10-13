package com.miophas.corruption.common.entity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/** The throwaway server NeoForge gives unit tests, with a world to put things in. It is flat, with its surface at y -60. */
final class TestWorld {
    /**
     * Runs on the server thread, where the world may be touched, and brings a failed assertion back as
     * itself. Afterwards the chunks the test loaded are let go: left loaded, a few tests' worth fill the
     * test JVM's memory, the server thread dies of it, and every later test waits on it for ever. Should
     * the server stop answering for any other reason, this gives up rather than hang the build.
     */
    static void onServer(MinecraftServer server, Consumer<ServerLevel> body) {
        try {
            server.submit(() -> {
                if (server.overworld() == null) loadWorld(server);
                ServerLevel level = server.overworld();
                try { body.accept(level); }
                finally { for (int tick = 0; tick < 3; tick++) level.getChunkSource().tick(() -> true, false); }
            }).get(5, TimeUnit.MINUTES);
        } catch (ExecutionException failure) {
            if (failure.getCause() instanceof Error error) throw error;
            throw new IllegalStateException(failure.getCause());
        } catch (TimeoutException | InterruptedException failure) { throw new IllegalStateException("The test server stopped answering", failure); }
    }

    /** The test server comes with registries and data packs but no world; this is the step a real server takes to make one. */
    private static void loadWorld(MinecraftServer server) {
        try {
            var load = MinecraftServer.class.getDeclaredMethod("loadLevel");
            load.setAccessible(true);
            load.invoke(server);
        } catch (ReflectiveOperationException failure) { throw new IllegalStateException("Could not load the test world", failure); }
    }

    /** Loads the chunks covering these block coordinates, so entities there can be found and the ground walked on. */
    static void load(ServerLevel level, int minX, int maxX, int minZ, int maxZ) {
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++)
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) level.getChunk(chunkX, chunkZ);
    }

    private TestWorld() {}
}
