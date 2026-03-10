package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LabelOptions {

    @Builder.Default
    private final Object visible = true;
}