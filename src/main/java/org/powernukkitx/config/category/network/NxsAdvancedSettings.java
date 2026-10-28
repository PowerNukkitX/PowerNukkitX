package org.powernukkitx.config.category.network;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class NxsAdvancedSettings extends OkaeriConfig {
    @Comment("pnx.settings.network.nethernet.nxs.advanced.assistedjoins")
    @CustomKey("assisted-joins")
    boolean assistedJoins = false;
    @Comment("pnx.settings.network.nethernet.nxs.advanced.connectionchecks")
    @CustomKey("connection-checks")
    boolean connectionChecks = true;
    @Comment("pnx.settings.network.nethernet.nxs.advanced.stundiscovery")
    @CustomKey("stun-discovery")
    boolean stunDiscovery = true;
    @Comment("pnx.settings.network.nethernet.nxs.advanced.location")
    Map<String, String> location = new LinkedHashMap<>();
    @Comment("pnx.settings.network.nethernet.nxs.advanced.deregisteronshutdown")
    @CustomKey("deregister-on-shutdown")
    boolean deregisterOnShutdown = false;
}
