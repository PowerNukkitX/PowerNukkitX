package cn.nukkit.lang;

import lombok.extern.slf4j.Slf4j;

/**
 * 文本容器
 * 通过{@link #text}存放文本内容
 *
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class TextContainer implements Cloneable {
    protected String text;
    /**
     * @deprecated 
     */
    

    public TextContainer(String text) {
        this.text = text;
    }
    /**
     * @deprecated 
     */
    

    public void setText(String text) {
        this.text = text;
    }
    /**
     * @deprecated 
     */
    

    public String getText() {
        return text;
    }

    /**
     * 等于{@link #getText()}
     * <p>
     * equal {@link #getText()}
     */
    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return this.getText();
    }

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
