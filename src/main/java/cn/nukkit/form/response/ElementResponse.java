package cn.nukkit.form.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * The response of a {@link cn.nukkit.form.element.custom.ElementDropdown} or {@link cn.nukkit.form.element.custom.ElementStepSlider}
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ElementResponse {
    /**
     * The ordinal of the selected option or step
     */
    protected final int elementId;
    /**
     * The text of the selected option or step
     */
    protected final String elementText;
}
