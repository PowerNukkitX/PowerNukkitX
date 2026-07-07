package org.powernukkitx.form.response;

import org.powernukkitx.form.element.simple.ElementButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.powernukkitx.form.window.ModalForm;

/**
 * The response of a {@link ModalForm}
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse extends Response {
    /**
     * The ordinal of the selected button
     * -1 if invalid
     */
    protected int buttonId = -1;
    /**
     * The button, if pressed
     */
    protected ElementButton button = null;
}
