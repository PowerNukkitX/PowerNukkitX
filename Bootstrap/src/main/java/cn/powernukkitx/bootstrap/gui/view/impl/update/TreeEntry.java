package cn.powernukkitx.bootstrap.gui.view.impl.update;

import cn.powernukkitx.bootstrap.util.SwingUtils;

import javax.swing.*;

public final class TreeEntry {
    public static final int SIZE = 16;

    private Icon icon;
    private String name;
    private String extra;

    public TreeEntry(Icon icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public TreeEntry setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public String getName() {
        return name;
    }

    public TreeEntry setName(String name) {
        this.name = name;
        return this;
    }

    public static TreeEntry createWaitEntry(String name) {
        return new TreeEntry(SwingUtils.getIcon("wait.png", SIZE), name);
    }

    public static TreeEntry createComponentEntry(String name) {
        return new TreeEntry(SwingUtils.getIcon("component.png", SIZE), name);
    }

    public static TreeEntry createDownloadEntry(String name) {
        return new TreeEntry(SwingUtils.getIcon("download.png", SIZE), name);
    }

    public static TreeEntry createWarnEntry(String name) {
        return new TreeEntry(SwingUtils.getIcon("warn.png", SIZE), name);
    }

    public static TreeEntry createErrorEntry(String name) {
        return new TreeEntry(SwingUtils.getIcon("error.png", SIZE), name);
    }

    public static TreeEntry createOkEntry(String name) {
        return new TreeEntry(SwingUtils.getIcon("ok.png", SIZE), name);
    }

    public String getExtra() {
        return extra;
    }

    public TreeEntry setExtra(String extra) {
        this.extra = extra;
        return this;
    }
}
