package cn.powernukkitx.bootstrap.gui.view.impl.update;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public final class CheckUpdateTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            final Object data = node.getUserObject();
            if (data == null) {
                setText("");
            } else {
                if (data instanceof TreeEntry) {
                    final TreeEntry treeEntry = (TreeEntry) data;
                    setIcon(treeEntry.getIcon());
                    setText(treeEntry.getName());
                } else {
                    setText(value.toString());
                }
            }
        } else {
            setText(value.toString());
        }

        return this;
    }
}
