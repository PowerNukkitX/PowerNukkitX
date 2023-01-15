package cn.nukkit.lang;

/**
 * 一个带有多语言功能的占位符插值文本容器，多语言功能通过{@link BaseLang}实现.
 * <p>
 * A placeholder interpolation text container with multi-language functionality,the multilingualism is implemented via {@link BaseLang}.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public class TranslationContainer extends TextContainer implements Cloneable {

    /**
     * 将参数数组每个下标的参数插入对应占位符{@code {%0,%1,%2...}}
     * <p>
     * Insert the argument of each index of the params into the corresponding placeholder {@code {%0,%1,%2...}
     */
    protected String[] params;

    public TranslationContainer(String text) {
        this(text, new String[]{});
    }

    public TranslationContainer(String text, String params) {
        super(text);
        this.setParameters(new String[]{params});
    }

    public TranslationContainer(String text, String... params) {
        super(text);
        this.setParameters(params);
    }

    public String[] getParameters() {
        return params;
    }

    public void setParameters(String[] params) {
        this.params = params;
    }

    public String getParameter(int i) {
        return (i >= 0 && i < this.params.length) ? this.params[i] : null;
    }

    public void setParameter(int i, String str) {
        if (i >= 0 && i < this.params.length) {
            this.params[i] = str;
        }
    }

    @Override
    public TranslationContainer clone() {
        return new TranslationContainer(this.text, this.params.clone());
    }
}
