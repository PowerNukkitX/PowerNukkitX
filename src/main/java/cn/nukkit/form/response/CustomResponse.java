package cn.nukkit.form.response;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The response of a {@link cn.nukkit.form.window.CustomForm}
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class CustomResponse extends Response {
    protected final Int2ObjectOpenHashMap<Object> responses = new Int2ObjectOpenHashMap<>();

    /**
     * Set a response for an element (internal)
     *
     * @param index    The index of the response
     * @param response The value of the response
     * @param <T>      Any
     */
    public <T> void setResponse(int index, T response) {
        this.responses.put(index, response);
    }

    /**
     * Get an element's response by its index
     *
     * @param index The index of the element
     * @return The response corresponding to the index
     * @param <T> Any valid response
     */
    @SuppressWarnings("unchecked")
    public <T> T getResponse(int index) {
        return (T) this.responses.get(index);
    }

    /**
     * Get a dropdown's response
     *
     * @param index The index of the dropdown
     * @return The elementId and elementText of the dropdown's response
     */
    public ElementResponse getDropdownResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get an input's response
     *
     * @param index The index of the input
     * @return The input
     */
    public String getInputResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get a slider's response
     *
     * @param index The index of the slider
     * @return The slider's float response
     */
    public float getSliderResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get a step sliders's response
     *
     * @param index The index of the step slider
     * @return The elementId and elementText of the step slider's response
     */
    public ElementResponse getStepSliderResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get a toggle's response
     *
     * @param index The index of the toggle
     * @return Whether the toggle was turned on or not
     */
    public boolean getToggleResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get a label
     *
     * @param index The index of the label
     * @return The label
     */
    public String getLabelResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get a label
     *
     * @param index The index of the divider
     * @return The divider
     */
    public String getDividerResponse(int index) {
        return this.getResponse(index);
    }

    /**
     * Get a label
     *
     * @param index The index of the header
     * @return The header
     */
    public String getHeaderResponse(int index) {
        return this.getResponse(index);
    }
}
