package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpacerOptions implements ElementOptions {

    @Builder.Default
    private final Object visible = true;
}