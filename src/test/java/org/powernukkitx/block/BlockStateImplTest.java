package org.powernukkitx.block;

import org.cloudburstmc.nbt.NbtMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Tests concurrent unknown block-state cache access.
 *
 * @author Curse
 */
class BlockStateImplTest {

    @Test
    void unknownBlockStateCacheIsThreadSafe() throws Exception {
        final int workers = 16;
        final int statesPerWorker = 512;
        final int sharedHash = 0x6fffffff;
        final NbtMap sharedTag = NbtMap.builder()
                .putString("name", "test:shared_unknown")
                .putCompound("states", NbtMap.EMPTY)
                .build();

        var executor = Executors.newFixedThreadPool(workers);
        try {
            var start = new CountDownLatch(1);
            List<Future<BlockState>> futures = new ArrayList<>(workers);

            for (int worker = 0; worker < workers; worker++) {
                final int threadIndex = worker;
                futures.add(executor.submit(() -> {
                    start.await();
                    for (int i = 0; i < statesPerWorker; i++) {
                        int hash = 0x70000000 + threadIndex * statesPerWorker + i;
                        NbtMap blockTag = NbtMap.builder()
                                .putString("name", "test:unknown_" + hash)
                                .putCompound("states", NbtMap.EMPTY)
                                .build();

                        BlockState state = BlockState.makeUnknownBlockState(hash, blockTag);
                        Assertions.assertSame(state, BlockState.makeUnknownBlockState(hash, blockTag));
                        Assertions.assertEquals(blockTag, state.getBlockStateTag().getCompound("Block"));
                    }
                    return BlockState.makeUnknownBlockState(sharedHash, sharedTag);
                }));
            }

            start.countDown();

            BlockState shared = futures.get(0).get(30, TimeUnit.SECONDS);
            for (Future<BlockState> future : futures) {
                Assertions.assertSame(shared, future.get(30, TimeUnit.SECONDS));
            }
            Assertions.assertEquals(sharedTag, shared.getBlockStateTag().getCompound("Block"));
        } finally {
            executor.shutdownNow();
        }
    }
}
