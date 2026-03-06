package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CloseButtonOptions {

    /** Label shown on the button. */
    @Builder.Default
    private final Object label = "Close";

    /** Whether the button is rendered in the UI. */
    @Builder.Default
    private final Object visible = true;
}