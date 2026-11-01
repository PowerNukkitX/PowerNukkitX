package org.powernukkitx.ddui.element.options;

import org.powernukkitx.ddui.Observable;
import lombok.Builder;
import lombok.Getter;

/**
 * Icon drawn on a button, pointing at a texture inside a resource pack.
 */
@Getter
@Builder
public class ImageDetails {

    /**
     * Texture path inside the pack, e.g. {@code textures/items/diamond}.
     * Can be a plain {@code String} or an {@link Observable}.
     */
    @Builder.Default
    private final Object imageSrc = "";

    /**
     * Id of the pack that holds the texture.
     * Can be a plain {@code String} or an {@link Observable}.
     */
    @Builder.Default
    private final Object imagePackId = "";
}
