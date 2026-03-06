package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TextFieldOptions {

    /** Helper text displayed below the field. */
    @Builder.Default
    private final Object description = "";

    /** Whether the field is non-interactive. */
    @Builder.Default
    private final Object disabled = false;

    /** Whether the field is rendered at all. */
    @Builder.Default
    private final Object visible = true;
}