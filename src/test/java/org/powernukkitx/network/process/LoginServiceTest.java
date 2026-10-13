package org.powernukkitx.network.process;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkitx.config.category.NetworkSettings;

import java.net.InetAddress;
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
        settings.maxPendingLoginsPerAddress(1);
        this.service = new LoginService(settings);
    }

    @AfterEach
    void tearDown() {
        this.service.shutdown();
    }

    @Test
    void tryAdmit_refusesBeyondLimitUntilReleased() {
        Assertions.assertTrue(this.service.tryAdmit(null));
        Assertions.assertTrue(this.service.tryAdmit(null));
        Assertions.assertFalse(this.service.tryAdmit(null));
        Assertions.assertEquals(2, this.service.getPendingLogins());

        this.service.release(null);

        Assertions.assertTrue(this.service.tryAdmit(null));
        Assertions.assertEquals(2, this.service.getPendingLogins());
    }

    @Test
    void tryAdmit_limitsEachPublicAddress() throws Exception {
        InetAddress first = InetAddress.getByName("203.0.113.7");
        InetAddress second = InetAddress.getByName("198.51.100.9");

        Assertions.assertTrue(this.service.tryAdmit(first));
        Assertions.assertFalse(this.service.tryAdmit(first));
        Assertions.assertEquals(1, this.service.getPendingLogins());
        Assertions.assertTrue(this.service.tryAdmit(second));

        this.service.release(first);
        Assertions.assertTrue(this.service.tryAdmit(first));
    }

    @Test
    void tryAdmit_doesNotLimitPrivateAddresses() throws Exception {
        InetAddress proxy = InetAddress.getByName("10.0.0.2");

        Assertions.assertTrue(this.service.tryAdmit(proxy));
        Assertions.assertTrue(this.service.tryAdmit(proxy));
        this.service.release(proxy);
        Assertions.assertTrue(this.service.tryAdmit(InetAddress.getLoopbackAddress()));
    }

    @Test
    void tryAdmit_globalRefusalFreesTheAddressSlot() throws Exception {
        InetAddress address = InetAddress.getByName("203.0.113.7");
        Assertions.assertTrue(this.service.tryAdmit(null));
        Assertions.assertTrue(this.service.tryAdmit(null));

        Assertions.assertFalse(this.service.tryAdmit(address));
        this.service.release(null);

        Assertions.assertTrue(this.service.tryAdmit(address));
    }

    @Test
    void admit_queuesBeyondTheLimitAndResumesInOrder() {
        List<Integer> resumed = new CopyOnWriteArrayList<>();
        Assertions.assertEquals(LoginService.Admission.ADMITTED, this.service.admit(null, () -> resumed.add(0)));
        Assertions.assertEquals(LoginService.Admission.ADMITTED, this.service.admit(null, () -> resumed.add(1)));
        Assertions.assertEquals(LoginService.Admission.QUEUED, this.service.admit(null, () -> resumed.add(2)));
        Assertions.assertEquals(LoginService.Admission.QUEUED, this.service.admit(null, () -> resumed.add(3)));
        Assertions.assertTrue(resumed.isEmpty());
        Assertions.assertEquals(2, this.service.getWaitingLogins());

        this.service.release(null);
        Assertions.assertEquals(List.of(2), resumed);
        Assertions.assertEquals(2, this.service.getPendingLogins());

        this.service.release(null);
        Assertions.assertEquals(List.of(2, 3), resumed);
        Assertions.assertEquals(0, this.service.getWaitingLogins());
    }

    @Test
    void admit_refusesOnceTheQueueIsFull() {
        NetworkSettings settings = new NetworkSettings();
        settings.maxPendingLogins(1);
        settings.maxQueuedLogins(1);
        LoginService small = new LoginService(settings);
        try {
            Assertions.assertEquals(LoginService.Admission.ADMITTED, small.admit(null, () -> {}));
            Assertions.assertEquals(LoginService.Admission.QUEUED, small.admit(null, () -> {}));
            Assertions.assertEquals(LoginService.Admission.REFUSED, small.admit(null, () -> {}));
        } finally {
            small.shutdown();
        }
    }

    @Test
    void admit_refusesAnAddressHoldingItsShareInsteadOfQueuing() throws Exception {
        InetAddress address = InetAddress.getByName("203.0.113.7");
        Assertions.assertEquals(LoginService.Admission.ADMITTED, this.service.admit(address, () -> {}));
        Assertions.assertEquals(LoginService.Admission.REFUSED, this.service.admit(address, () -> {}));
        Assertions.assertEquals(0, this.service.getWaitingLogins());
    }

    @Test
    void admit_skipsAQueuedAddressThatIsAtItsLimit() throws Exception {
        InetAddress capped = InetAddress.getByName("203.0.113.7");
        List<String> resumed = new CopyOnWriteArrayList<>();
        Assertions.assertEquals(LoginService.Admission.ADMITTED, this.service.admit(capped, () -> {}));
        Assertions.assertEquals(LoginService.Admission.ADMITTED, this.service.admit(null, () -> {}));
        Assertions.assertEquals(LoginService.Admission.QUEUED, this.service.admit(null, () -> resumed.add("other")));

        this.service.release(null);

        Assertions.assertEquals(List.of("other"), resumed);
    }

    @Test
    void cancelWaiting_dropsAQueuedLogin() {
        List<Integer> resumed = new CopyOnWriteArrayList<>();
        Runnable queued = () -> resumed.add(2);
        this.service.admit(null, () -> {});
        this.service.admit(null, () -> {});
        Assertions.assertEquals(LoginService.Admission.QUEUED, this.service.admit(null, queued));

        this.service.cancelWaiting(queued);
        this.service.release(null);

        Assertions.assertTrue(resumed.isEmpty());
        Assertions.assertEquals(1, this.service.getPendingLogins());
    }

    @Test
    void tryReservePlayerSlot_countsReservationsWithOnlinePlayers() {
        Assertions.assertTrue(this.service.tryReservePlayerSlot(8, 10));
        Assertions.assertTrue(this.service.tryReservePlayerSlot(8, 10));
        Assertions.assertFalse(this.service.tryReservePlayerSlot(8, 10));
        Assertions.assertEquals(2, this.service.getReservedPlayerSlots());

        this.service.releasePlayerSlot();
        Assertions.assertFalse(this.service.tryReservePlayerSlot(9, 10));
        Assertions.assertTrue(this.service.tryReservePlayerSlot(8, 10));
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
    void playerDataTasks_runInSubmissionOrder() throws InterruptedException {
        List<Integer> order = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 50; i++) {
            final int index = i;
            this.service.executePlayerData(() -> order.add(index));
        }

        CountDownLatch done = new CountDownLatch(1);
        this.service.executePlayerData(done::countDown);
        Assertions.assertTrue(done.await(10, TimeUnit.SECONDS));
        Assertions.assertEquals(50, order.size());
        for (int i = 0; i < 50; i++) {
            Assertions.assertEquals(i, order.get(i));
        }
    }
}
