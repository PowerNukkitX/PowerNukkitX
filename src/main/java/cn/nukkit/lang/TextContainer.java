package cn.nukkit.lang;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents a container for text, providing a base for translation and message handling.
 * <p>
 * This class encapsulates a text value and provides methods for getting, setting, and cloning the text.
 * It is commonly used as a base for translation containers and message wrappers in the internationalization (i18n)
 * system. The text can be retrieved, modified, or cloned for further processing.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Stores a text value for translation or display.</li>
 *   <li>Provides getter and setter for the text value.</li>
 *   <li>Supports cloning for safe reuse or modification.</li>
 *   <li>Overrides {@link #toString()} to return the text value.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     TextContainer container = new TextContainer("welcome.message");
 *     String text = container.getText();
 *     container.setText("new.message");
 *     TextContainer copy = container.clone();
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If used in a multi-threaded context, external synchronization is required.
 * </p>
 *
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class TextContainer implements Cloneable {
    /**
     * The text value stored in this container.
     */
    protected String text;

    /**
     * Constructs a TextContainer with the specified text value.
     *
     * @param text the text value to store in this container
     */
    public TextContainer(String text) {
        this.text = text;
    }

    /**
     * Sets the text value stored in this container.
     *
     * @param text the new text value
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the text value stored in this container.
     *
     * @return the text value
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the text value stored in this container. Equivalent to {@link #getText()}.
     *
     * @return the text value
     */
    @Override
    public String toString() {
        return this.getText();
    }

    /**
     * Creates and returns a shallow copy of this TextContainer.
     *
     * @return a clone of this instance, or null if cloning fails
     */
    @Override
    public TextContainer clone() {
        try {
            return (TextContainer) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("Failed to clone the text container {}", this.toString(), e);
        }
        return null;
    }
}
