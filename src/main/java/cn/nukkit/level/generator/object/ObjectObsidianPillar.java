package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockIronBars;
import cn.nukkit.block.BlockObsidian;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.Random;

import static cn.nukkit.block.property.CommonBlockProperties.INFINIBURN_BIT;

@Getter
@Setter
public class ObjectObsidianPillar extends ObjectGenerator {

    public int i = 0;
    private int seed = 0;


    public int getPillar() {
        return Math.abs((i * 73 + seed) % 10);
    }

    public int getRadius() {
        return 2 + getPillar() / 3;
    }

    public int getHeight() {
        return 76 + getPillar() * 3;
    }

    public boolean isGuarded() {
        int pillar = getPillar();
        return pillar == 1 || pillar == 2;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        this.seed = (int) level.getSeed();
        int x = position.getFloorX();
        int z = position.getFloorZ();
        int height = getHeight();
        int radius = getRadius();

        for (int i = 0; i < height; i++) {
            for (int j = -radius; j <= radius; j++) {
                for (int k = -radius; k <= radius; k++) {
                    if (j * j + k * k <= radius * radius + 1) {
                        level.setBlockStateAt(x + j, i, z + k, BlockObsidian.PROPERTIES.getDefaultState());
                    }
                }
            }
        }

        if (this.isGuarded()) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                        for (int k = 0; k < 3; ++k) {
                            level.setBlockStateAt(x + i, height + k, z + j, BlockIronBars.PROPERTIES.getDefaultState());
                        }
                    }
                    level.setBlockStateAt(x + i, height + 3, z + j, BlockIronBars.PROPERTIES.getDefaultState());
                }
            }
        }

        level.setBlockStateAt(x, height, z, BlockBedrock.PROPERTIES.getBlockState(INFINIBURN_BIT.createValue(true)));
        level.setBlockStateAt(x, height + 1, z, BlockFire.PROPERTIES.getDefaultState());
        level.addHook(() -> {
            NbtMap nbt = NbtMap.builder()
                    .putList("Pos", NbtType.DOUBLE, Arrays.asList(x + 0.5, height + 1.0, z + 0.5))
                    .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                    .putList("Rotation", NbtType.FLOAT, Arrays.asList(new Random().nextFloat() * 360, 0f))
                    .build();
            Entity entity = Entity.createEntity(Entity.ENDER_CRYSTAL, level.getChunk(position.getChunkX(), position.getChunkZ()), nbt);
            entity.spawnToAll();
        });
        return true;
    }
}
