package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DropdownOptions implements ElementOptions {
    @Builder.Default
    private final Object description = "";

    @Builder.Default
    private final Object disabled = false;

    @Builder.Default
    private final Object visible = true;
}
