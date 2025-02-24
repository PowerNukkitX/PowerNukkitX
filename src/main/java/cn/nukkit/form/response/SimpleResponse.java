package cn.nukkit.form.response;

import cn.nukkit.form.element.simple.ElementButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse implements Response {
    protected int buttonId = -1;
    protected ElementButton button = null;
}
