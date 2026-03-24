package cn.nukkit.ddui.element.options;

import cn.nukkit.ddui.Observable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToggleOptions implements ElementOptions {

    /**
     * Description text displayed above or alongside the toggle.
     * Can be a plain {@code String} or an {@link Observable}.
     */
    @Builder.Default
    private final Object description = "";

    /**
     * Whether the toggle is disabled (non-interactive).
     * Supply either a plain {@code boolean} or an {@link Observable}.
     */
    @Builder.Default
    private final Object disabled = false;

    /**
     * Whether the toggle is visible in the UI.
     * Supply either a plain {@code boolean} or an {@link Observable}.
     */
    @Builder.Default
    private final Object visible = true;
}