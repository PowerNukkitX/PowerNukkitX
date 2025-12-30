package cn.nukkit.lang;

/**
 * Represents a placeholder interpolation text container with multi-language support for translation.
 * <p>
 * This class extends {@link TextContainer} and allows for parameterized translation using placeholders
 * in the form of <code>{%0}, {%1}, {%2}, ...</code>. The actual translation and interpolation are handled
 * by {@link BaseLang}. Each parameter in the array is inserted into its corresponding placeholder index.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Supports multi-language translation with parameter interpolation.</li>
 *   <li>Stores an array of parameters for placeholder replacement.</li>
 *   <li>Provides constructors for various parameter input types.</li>
 *   <li>Allows getting and setting parameters individually or as an array.</li>
 *   <li>Supports cloning for safe reuse or modification.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     TranslationContainer tc = new TranslationContainer("welcome.message", "Player");
 *     String[] params = tc.getParameters();
 *     tc.setParameter(0, "Admin");
 *     TranslationContainer copy = tc.clone();
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If used in a multi-threaded context, external synchronization is required.
 * </p>
 *
 * @author MagicDroidX (Nukkit Project)
 */
public class TranslationContainer extends TextContainer {

    /**
     * The array of parameters to be interpolated into the placeholders of the translation string.
     * Each parameter corresponds to a placeholder index (e.g., {%0}, {%1}, ...).
     */
    protected String[] params;

    /**
     * Constructs a TranslationContainer with the specified text and an empty parameter array.
     *
     * @param text the translation key or text
     */
    public TranslationContainer(String text) {
        this(text, new String[]{});
    }

    /**
     * Constructs a TranslationContainer with the specified text and a single parameter.
     *
     * @param text   the translation key or text
     * @param params the single parameter to interpolate
     */
    public TranslationContainer(String text, String params) {
        super(text);
        this.setParameters(new String[]{params});
    }

    /**
     * Constructs a TranslationContainer with the specified text and an array of parameters.
     *
     * @param text   the translation key or text
     * @param params the parameters to interpolate
     */
    public TranslationContainer(String text, String... params) {
        super(text);
        this.setParameters(params);
    }

    /**
     * Returns the array of parameters to be interpolated into the translation string.
     *
     * @return the array of parameters
     */
    public String[] getParameters() {
        return params;
    }

    /**
     * Sets the array of parameters to be interpolated into the translation string.
     *
     * @param params the array of parameters
     */
    public void setParameters(String[] params) {
        this.params = params;
    }

    /**
     * Returns the parameter at the specified index, or null if the index is out of bounds.
     *
     * @param i the index of the parameter
     * @return the parameter at the specified index, or null if out of bounds
     */
    public String getParameter(int i) {
        return (i >= 0 && i < this.params.length) ? this.params[i] : null;
    }

    /**
     * Sets the parameter at the specified index, if the index is valid.
     *
     * @param i   the index of the parameter
     * @param str the new parameter value
     */
    public void setParameter(int i, String str) {
        if (i >= 0 && i < this.params.length) {
            this.params[i] = str;
        }
    }

    /**
     * Creates and returns a deep copy of this TranslationContainer, including a clone of the parameters array.
     *
     * @return a clone of this instance
     */
    @Override
    public TranslationContainer clone() {
        return new TranslationContainer(this.text, this.params.clone());
    }
}
