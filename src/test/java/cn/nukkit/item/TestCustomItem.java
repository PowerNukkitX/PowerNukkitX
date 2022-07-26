package cn.nukkit.item;

import cn.nukkit.item.customitem.ItemCustom;

public class TestCustomItem extends ItemCustom {
    public static String a;

    public TestCustomItem(String id, String name) {
        super(id, name);
    }

    public boolean __super__allowOffHand() {
        return super.allowOffHand();
    }

    @Override
    public boolean allowOffHand() {
        return __super__allowOffHand();
    }
}
