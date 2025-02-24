package cn.nukkit.form.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ElementResponse {
    protected final int elementId;
    protected final String elementText;
}
