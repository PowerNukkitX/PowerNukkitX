package org.powernukkitx.config.category.network;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The NXS signalling provider, which finds players for this server rather than waiting for them to
 * arrive at an endpoint of its own.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class NxsSettings extends OkaeriConfig {
    @Comment("pnx.settings.network.nethernet.nxs.endpoint")
    String endpoint = "https://agent.warden.cloud";
    @Comment("pnx.settings.network.nethernet.nxs.token")
    String token = "";
    @Comment("pnx.settings.network.nethernet.nxs.advertiseaddresses")
    @CustomKey("advertise-addresses")
    List<String> advertiseAddresses = new ArrayList<>();
    @Comment("pnx.settings.network.nethernet.nxs.data")
    Map<String, String> data = new LinkedHashMap<>();
}
