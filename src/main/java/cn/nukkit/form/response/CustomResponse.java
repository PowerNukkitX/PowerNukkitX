package cn.nukkit.form.response;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class CustomResponse implements Response {
    protected final Int2ObjectOpenHashMap<Object> responses = new Int2ObjectOpenHashMap<>();

    public <T> void setResponse(int index, T response) {
        this.responses.put(index, response);
    }

    @SuppressWarnings("unchecked")
    public <T> T getResponse(int index) {
        return (T) this.responses.get(index);
    }

    public ElementResponse getDropdownResponse(int index) {
        return this.getResponse(index);
    }

    public String getInputResponse(int index) {
        return this.getResponse(index);
    }

    public String getLabelResponse(int index) {
        return this.getResponse(index);
    }

    public float getSliderResponse(int index) {
        return this.getResponse(index);
    }

    public ElementResponse getStepSliderResponse(int index) {
        return this.getResponse(index);
    }

    public boolean getToggleResponse(int index) {
        return this.getResponse(index);
    }
}
