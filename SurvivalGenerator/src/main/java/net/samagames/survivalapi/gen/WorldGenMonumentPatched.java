package net.samagames.survivalapi.gen;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.generator.NormalChunkGenerator;

import java.lang.reflect.Field;

/**
 * @author Florian Cassayre (6infinity8)
 */
public class WorldGenCavesPatched extends WorldGenCaves
{
    private final int amount;

    /**
     * <p>Used to control the world generation.
     * This module can amplify or reduce the amount of caves in the world.</p>
     * <p>Setting the parameter to 0 will totally remove caves from the world. Setting it to 2 will double the amount of caves.</p>
     * <p>Note: this may affect your server performance by <code>amountOfCaves * -5%</code>.</p>
     * @param amountOfCaves number of times the cave generator method should be called.
     */
    public WorldGenCavesPatched(int amountOfCaves)
    {
        this.amount = amountOfCaves;
    }

    @Override
    public void a(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, ChunkSnapshot chunksnapshot)
    {
        for(int i = 0; i < this.amount; i++)
        {
            int k = this.a;

            this.c = world;
            this.b.setSeed(world.getSeed() + i);
            long l = this.b.nextLong();
            long i1 = this.b.nextLong();

            for (int j1 = chunkX - k; j1 <= chunkX + k; ++j1)
            {
                for (int k1 = chunkZ - k; k1 <= chunkZ + k; ++k1)
                {
                    long l1 = (long) j1 * l;
                    long i2 = (long) k1 * i1;

                    this.b.setSeed(l1 ^ i2 ^ world.getSeed());
                    this.a(world, j1, k1, chunkX, chunkZ, chunksnapshot);
                }
            }
        }
    }

    public static void loadForWorld(org.bukkit.World world, int amountOfCaves) throws NoSuchFieldException, IllegalAccessException
    {
        World craftWorld = ((CraftWorld) world).getHandle();
        NormalChunkGenerator normalChunkGenerator = (NormalChunkGenerator) ((ChunkProviderServer) craftWorld.N()).chunkProvider;

        Field chunkProvider = NormalChunkGenerator.class.getDeclaredField("provider");
        chunkProvider.setAccessible(true);

        Field worldGenCaveField = ChunkProviderGenerate.class.getDeclaredField("u");
        worldGenCaveField.setAccessible(true);
        worldGenCaveField.set(chunkProvider.get(normalChunkGenerator), new WorldGenCavesPatched(amountOfCaves));
    }
}