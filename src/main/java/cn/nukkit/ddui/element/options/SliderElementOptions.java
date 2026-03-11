package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SliderElementOptions implements ElementOptions {

    /** Helper text displayed below the slider. */
    @Builder.Default
    private final Object description = "";

    /** Whether the slider is non-interactive. */
    @Builder.Default
    private final Object disabled = false;

    /** Whether the slider is rendered in the UI. */
    @Builder.Default
    private final Object visible = true;

    /**
     * Amount the value changes per user interaction step.
     * Defaults to {@code 1}.
     */
    @Builder.Default
    private final Object step = 1;
}