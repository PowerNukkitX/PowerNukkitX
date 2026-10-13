package org.powernukkitx.network.process;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkitx.config.category.NetworkSettings;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class LoginServiceTest {

    private LoginService service;

    @BeforeEach
    void setUp() {
        NetworkSettings settings = new NetworkSettings();
        settings.maxPendingLogins(2);
        settings.loginThreads(1);
        this.service = new LoginService(settings);
    }

    @AfterEach
    void tearDown() {
        this.service.shutdown();
    }

    @Test
    void tryAdmit_refusesBeyondLimitUntilReleased() {
        Assertions.assertTrue(this.service.tryAdmit());
        Assertions.assertTrue(this.service.tryAdmit());
        Assertions.assertFalse(this.service.tryAdmit());
        Assertions.assertEquals(2, this.service.getPendingLogins());

        this.service.release();

        Assertions.assertTrue(this.service.tryAdmit());
        Assertions.assertEquals(2, this.service.getPendingLogins());
    }

    @Test
    void executeValidation_refusesWhenQueueIsFull() throws InterruptedException {
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch started = new CountDownLatch(1);
        Runnable blocking = () -> {
            started.countDown();
            try {
                release.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // One running on the single thread, two queued, which fills the queue sized to the limit
        Assertions.assertTrue(this.service.executeValidation(blocking));
        Assertions.assertTrue(started.await(10, TimeUnit.SECONDS));
        Assertions.assertTrue(this.service.executeValidation(() -> {}));
        Assertions.assertTrue(this.service.executeValidation(() -> {}));
        Assertions.assertFalse(this.service.executeValidation(() -> {}));

        release.countDown();
    }

    @Test
    void playerDataTasks_runInSubmissionOrder() {
        List<Integer> order = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 50; i++) {
            final int index = i;
            this.service.executePlayerData(() -> order.add(index));
        }

        Assertions.assertTrue(this.service.awaitPlayerData(10_000L));
        Assertions.assertEquals(50, order.size());
        for (int i = 0; i < 50; i++) {
            Assertions.assertEquals(i, order.get(i));
        }
    }
}
