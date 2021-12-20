package cn.nukkit.entity.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockFallable;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EntityFallingBlockTest {
    @MockLevel
    Level level;

    EntityFallingBlock fallingBlock;

    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 63, 0, BlockState.of(BlockID.STILL_WATER));
        fallingBlock = new TestBlock(BlockID.SAND, 0, 100, 0, level).createFallingEntity(new CompoundTag());
    }

    @Test
    void resetFallDistance() {
        fallingBlock.highestPosition = 255;
        fallingBlock.resetFallDistance();
        assertEquals(100, fallingBlock.highestPosition);

        fallingBlock.highestPosition = 255;
        fallingBlock.close();
        fallingBlock.resetFallDistance();
        assertEquals(255, fallingBlock.highestPosition);
    }

    @Test
    void canCollideWith() {
        Entity entity = mock(Entity.class);
        assertFalse(fallingBlock.canCollide());
        assertFalse(fallingBlock.canCollideWith(entity));

        fallingBlock.blockId = BlockID.ANVIL;
        assertTrue(fallingBlock.canCollide());
        assertTrue(fallingBlock.canCollideWith(entity));
    }

    static class TestBlock extends BlockFallable {
        private final int id;
        public TestBlock(int id, int x, int y, int z, Level level) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            this.level = level;
        }

        @Override
        public String getName() {
            return "Test Block";
        }

        @Override
        public int getId() {
            return id;
        }

        @PowerNukkitOnly
        @Override
        public EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
            return super.createFallingEntity(customNbt);
        }
    }
}
