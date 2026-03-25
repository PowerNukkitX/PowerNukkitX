package cn.nukkit.ddui.element.options;

import cn.nukkit.ddui.Observable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HeaderOptions {

    /**
     * Whether the header is visible in the UI.
     * Supply either a plain {@code boolean} or an {@link Observable}.
     */
    @Builder.Default
    private final Object visible = true;
}