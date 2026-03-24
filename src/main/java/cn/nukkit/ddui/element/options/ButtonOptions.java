package cn.nukkit.ddui.element.options;


import cn.nukkit.ddui.Observable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ButtonOptions implements ElementOptions {

    /**
     * Whether the button is disabled (grayed-out, non-interactive).
     * Supply either a plain {@code boolean} or an {@link Observable}.
     */
    @Builder.Default
    private final Object disabled = false;

    /**
     * Tooltip shown when hovering over the button.
     */
    @Builder.Default
    private final Object tooltip = "";

    /**
     * Whether the button is visible in the UI.
     */
    @Builder.Default
    private final Object visible = true;
}