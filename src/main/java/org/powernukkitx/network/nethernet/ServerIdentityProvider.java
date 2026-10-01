package org.powernukkitx.network.nethernet;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.netty.util.nethernet.ServerIdentity;
import org.powernukkitx.Server;
import org.powernukkitx.utils.TextFormat;

import java.io.File;
import java.nio.file.Path;

/**
 * The NetherNet identity this server presents to clients.
 * <p>
 * Clients pin the public key, so the PEM is generated once and then kept: replacing it makes every
 * returning player accept the trust prompt again.
 *
 * @author xRookieFight
 * @since 13/09/2026
 */
@Slf4j
@UtilityClass
public class ServerIdentityProvider {

    private volatile ServerIdentity identity;

    public ServerIdentity identity(Server server) throws Exception {
        ServerIdentity current = identity;
        if (current != null) {
            return current;
        }

        synchronized (ServerIdentityProvider.class) {
            if (identity == null) {
                identity = load(server);
            }
            return identity;
        }
    }

    private ServerIdentity load(Server server) throws Exception {
        String configured = server.getSettings().networkSettings().netherNetSettings().identityFile();
        File pem = Path.of(server.getDataPath()).resolve(configured).toFile();
        File parent = pem.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new IllegalStateException("Unable to create the NetherNet identity directory " + parent);
        }

        boolean existed = pem.isFile();
        ServerIdentity loaded = ServerIdentity.fromPemOrCreate(pem, domain(server));
        if (!existed) {
            log.info("Generated a NetherNet identity at {}. Share this file across a fleet to be trusted as one "
                + "operator, and keep it: replacing it re-prompts every player", pem);
        }
        return loaded;
    }

    /**
     * The operator name shown in the client's trust prompt. Display text only, so it can change
     * without replacing the key.
     */
    public String domain(Server server) {
        String configured = server.getSettings().networkSettings().netherNetSettings().identityDomain();
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        return TextFormat.clean(server.getMotd());
    }
}
