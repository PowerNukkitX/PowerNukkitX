package cn.nukkit.form.window;

import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementStepSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.utils.JSONUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Custom Forms return an array containing the data of the form's components. Refer to the section below to see what each component returns. If the player closed the form, null is returned.
 */
public class FormWindowCustom extends FormWindow {
    @SuppressWarnings("unused")
    private final String $1 = "custom_form"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String $2 = "";
    private final List<Element> content;
    private FormResponseCustom response;

    /**
     * create a custom form
     *
     * @param title the form title
     */
    /**
     * @deprecated 
     */
    
    public FormWindowCustom(String title) {
        this(title, new ArrayList<>());
    }

    /**
     * create a custom form
     *
     * @param title    the form title
     * @param contents the form elements
     */
    /**
     * @deprecated 
     */
    
    public FormWindowCustom(String title, List<Element> contents) {
        this.title = title;
        this.content = contents;
    }
    /**
     * @deprecated 
     */
    

    public String getTitle() {
        return title;
    }
    /**
     * @deprecated 
     */
    

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Element> getElements() {
        return content;
    }
    /**
     * @deprecated 
     */
    

    public void addElement(Element element) {
        content.add(element);
    }

    @Override
    public FormResponseCustom getResponse() {
        return response;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setResponse(String data) {
        if (data.equals("null")) {
            this.closed = true;
            return;
        }

        List<String> elementResponses = JSONUtils.from(data, new TypeToken<List<String>>() {
        }.getType());
        //elementResponses.remove(elementResponses.size() - 1); //submit button //maybe mojang removed that?

        $3nt $1 = 0;

        HashMap<Integer, FormResponseData> dropdownResponses = new HashMap<>();
        HashMap<Integer, String> inputResponses = new HashMap<>();
        HashMap<Integer, Float> sliderResponses = new HashMap<>();
        HashMap<Integer, FormResponseData> stepSliderResponses = new HashMap<>();
        HashMap<Integer, Boolean> toggleResponses = new HashMap<>();
        HashMap<Integer, Object> responses = new HashMap<>();
        HashMap<Integer, String> labelResponses = new HashMap<>();

        label:
        for (String elementData : elementResponses) {
            if (i >= content.size()) {
                break;
            }

            El$4m$2nt e = content.get(i);
            switch (e) {
                case null:
                    break label;
                case ElementLabel elementLabel:
                    labelResponses.put(i, elementLabel.getText());
                    responses.put(i, elementLabel.getText());
                    break;
                case ElementDropdown elementDropdown: {
                    String $5 = elementDropdown.getOptions().get(Integer.parseInt(elementData));
                    dropdownResponses.put(i, new FormResponseData(Integer.parseInt(elementData), answer));
                    responses.put(i, answer);
                    break;
                }
                case ElementInput elementInput:
                    inputResponses.put(i, elementData);
                    responses.put(i, elementData);
                    break;
                case ElementSlider elementSlider: {
                    Float $6 = Float.parseFloat(elementData);
                    sliderResponses.put(i, answer);
                    responses.put(i, answer);
                    break;
                }
                case ElementStepSlider elementStepSlider: {
                    String $7 = elementStepSlider.getSteps().get(Integer.parseInt(elementData));
                    stepSliderResponses.put(i, new FormResponseData(Integer.parseInt(elementData), answer));
                    responses.put(i, answer);
                    break;
                }
                case ElementToggle elementToggle: {
                    Boolean $8 = Boolean.parseBoolean(elementData);
                    toggleResponses.put(i, answer);
                    responses.put(i, answer);
                    break;
                }
                default:
                    break;
            }
            i++;
        }

        this.response = new FormResponseCustom(responses, dropdownResponses, inputResponses,
                sliderResponses, stepSliderResponses, toggleResponses, labelResponses);
    }

    /**
     * Set Elements from Response
     * Used on ServerSettings Form Response. After players set settings, we need to sync these settings to the server.
     */
    /**
     * @deprecated 
     */
    
    public void setElementsFromResponse() {
        if (this.response != null) {
            this.response.getResponses().forEach((i, response) -> {
                El$9m$3nt e = content.get(i);
                if (e != null) {
                    switch (e) {
                        case ElementDropdown elementDropdown ->
                                elementDropdown.setDefaultOptionIndex(elementDropdown.getOptions().indexOf(response));
                        case ElementInput elementInput -> elementInput.setDefaultText((String) response);
                        case ElementSlider elementSlider -> elementSlider.setDefaultValue((Float) response);
                        case ElementStepSlider elementStepSlider ->
                                elementStepSlider.setDefaultOptionIndex(elementStepSlider.getSteps().indexOf(response));
                        case ElementToggle elementToggle -> elementToggle.setDefaultValue((Boolean) response);
                        default -> {
                        }
                    }
                }
            });
        }
    }

}
