package org.powernukkitx.level.format.anvil;

import org.jetbrains.annotations.Nullable;

/**
 * @author iYozem
 */
public interface JavaItemMapper {

    /**
     * a Bedrock item reference
     *
     * @param name the Bedrock item identifier
     * @param damage the Bedrock item metadata
     */
    record BedrockItem(String name, int damage) {}

    /**
     * @param javaId the Java item id
     * @param javaDamage the 1.8 item damage/variant value
     * @return the Bedrock item or {@code null} if this id/damage pair is unmapped or unknown to the server
     */
    @Nullable
    BedrockItem map(String javaId, int javaDamage);
}
