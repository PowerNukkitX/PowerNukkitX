
package org.powernukkitx.network.process;

import org.junit.jupiter.api.Test;
import org.powernukkitx.config.category.NetworkSettings;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginQueueAddressLimitTest {
    @Test
    void onePublicAddressCannotFillTheWaitingQueue() throws Exception {
        LoginService service = service();
        try {
            service.admit(null, () -> {});
            InetAddress address = InetAddress.getByName("203.0.113.7");
            assertEquals(LoginService.Admission.QUEUED, service.admit(address, () -> {}));
            assertEquals(LoginService.Admission.REFUSED, service.admit(address, () -> {}));
            assertEquals(LoginService.Admission.QUEUED,
                service.admit(InetAddress.getByName("198.51.100.9"), () -> {}));
            assertEquals(2, service.getWaitingLogins());
        } finally {
            service.shutdown();
        }
    }

    @Test
    void cancellingAWaiterReturnsItsAddressBudget() throws Exception {
        LoginService service = service();
        try {
            service.admit(null, () -> {});
            InetAddress address = InetAddress.getByName("203.0.113.7");
            Runnable cancelled = () -> {};
            assertEquals(LoginService.Admission.QUEUED, service.admit(address, cancelled));
            service.cancelWaiting(cancelled);
            service.cancelWaiting(cancelled);
            assertEquals(LoginService.Admission.QUEUED, service.admit(address, () -> {}));
            assertEquals(LoginService.Admission.REFUSED, service.admit(address, () -> {}));
        } finally {
            service.shutdown();
        }
    }

    @Test
    void promotionAndReleaseReturnTheQueuedAddressBudget() throws Exception {
        LoginService service = service();
        try {
            service.admit(null, () -> {});
            InetAddress address = InetAddress.getByName("203.0.113.7");
            List<String> resumed = new ArrayList<>();
            service.admit(address, () -> resumed.add("first"));
            service.release(null);
            assertEquals(List.of("first"), resumed);
            assertEquals(LoginService.Admission.REFUSED, service.admit(address, () -> {}));
            service.release(address);
            assertEquals(LoginService.Admission.ADMITTED, service.admit(address, () -> {}));
            assertEquals(0, service.getWaitingLogins());
        } finally {
            service.shutdown();
        }
    }

    @Test
    void privateProxyAddressesCanStillQueueMultiplePlayers() throws Exception {
        LoginService service = service();
        try {
            service.admit(null, () -> {});
            InetAddress proxy = InetAddress.getByName("10.0.0.2");
            assertEquals(LoginService.Admission.QUEUED, service.admit(proxy, () -> {}));
            assertEquals(LoginService.Admission.QUEUED, service.admit(proxy, () -> {}));
        } finally {
            service.shutdown();
        }
    }

    private LoginService service() {
        NetworkSettings settings = new NetworkSettings();
        settings.maxPendingLogins(1);
        settings.maxQueuedLogins(4);
        settings.maxPendingLoginsPerAddress(1);
        settings.loginThreads(1);
        return new LoginService(settings);
    }
}
