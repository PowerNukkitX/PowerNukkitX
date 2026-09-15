package org.powernukkitx.config.category.network;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class RakNetSettings extends OkaeriConfig {
    @Comment("pnx.settings.network.raknet.packetlimit")
    int packetLimit = 8000;
    @Comment("pnx.settings.network.raknet.autoflush")
    boolean autoFlush = true;
    @Comment("pnx.settings.network.raknet.flushinterval")
    int flushInterval = 10;
    @Comment("pnx.settings.network.raknet.maxqueuedbytes")
    int maxQueuedBytes = 67108864;
    @Comment("pnx.settings.network.raknet.cookiemode")
    String cookieMode = "ACTIVE";
}
