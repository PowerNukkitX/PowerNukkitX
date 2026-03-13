package cn.nukkit.ddui.element.options;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HeaderOptions {

    @Builder.Default
    private final Object visible = true;
}