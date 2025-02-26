package cn.nukkit.form.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The response of a {@link cn.nukkit.form.window.ModalForm}
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ModalResponse extends Response {
    /**
     * The ordinal of the selected option:
     * -1 if invalid
     * 0 if accepted
     * 1 if rejected
     */
    protected int buttonId = -1;
    protected boolean yes = false;
}
