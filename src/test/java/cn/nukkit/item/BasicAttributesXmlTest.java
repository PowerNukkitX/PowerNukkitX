package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Spliterator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author joserobjr
 * @since 2021-12-15
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
public class BasicAttributesXmlTest {
    private static Document document;

    @ParameterizedTest
    @MethodSource("createComparisonStream")
    void testAttribute(Item item, Element definition) {
        final String namespaceId;
        try {
            namespaceId = item.getNamespaceId();
        } catch (IllegalArgumentException e) {
            Item.fromString(definition.getAttribute("namespaced-id"));
            throw new IllegalArgumentException("item.getNamespaceId() failed for " + definition.getAttribute("namespaced-id") + " with damage " + definition.getAttribute("damage"), e);
        }
        final int damage = item.getDamage();
        String prefix = namespaceId + (damage != 0? ":" + damage : "") + " -> " + item.getClass().getSimpleName() + " -> ";
        assertEquals(definition.getAttribute("namespaced-id"), namespaceId, ()-> prefix + "Wrong namespaced-id");
        getInt("numeric-id", definition).ifPresent(id-> assertEquals(id, item.getId(), ()-> prefix + "Wrong numeric-id"));
        assertEquals(definition.getAttribute("name"), item.getName(), ()-> prefix + "Wrong name test method 1");
        final int id = item.getId();
        Item item2 = Item.get(id, damage);
        assertEquals(definition.getAttribute("name"), item2.getName(), ()-> prefix + "Wrong name test method 2");
        if (id >= 0 && id < Item.list.length) {
            try {
                Constructor<?> constructor = ((Class<?>) Item.list[id]).getDeclaredConstructor(Integer.class);
                item2 = (Item) constructor.newInstance(damage);
                assertEquals(definition.getAttribute("name"), item2.getName(), ()-> prefix + "Wrong name test method 3");
            } catch (ReflectiveOperationException e) {
                fail(prefix + "Missing constructor (Integer)", e);
            }

            try {
                Constructor<?> constructor = ((Class<?>) Item.list[id]).getDeclaredConstructor(Integer.class, Integer.TYPE);
                item2 = (Item) constructor.newInstance(damage, 1);
                assertEquals(definition.getAttribute("name"), item2.getName(), ()-> prefix + "Wrong name test method 4");
            } catch (ReflectiveOperationException e) {
                fail(prefix + "Missing constructor (Integer, int)", e);
            }

            if (damage == 0) {
                try {
                    Constructor<?> constructor = ((Class<?>) Item.list[id]).getDeclaredConstructor();
                    item2 = (Item) constructor.newInstance();
                    assertEquals(definition.getAttribute("name"), item2.getName(), ()-> prefix + "Wrong name test method 5");
                } catch (ReflectiveOperationException e) {
                    fail(prefix + "Missing constructor ()", e);
                }
            }
        }
        assertEquals(getInt("stack-size", definition).orElse(64), item.getMaxStackSize(), ()-> prefix + "Wrong stack-size");
        assertEquals(getInt("durability", definition).orElse(-1), item.getMaxDurability(), ()-> prefix + "Wrong durability");
        assertEquals(getInt("fuel-time", definition), Optional.ofNullable(item.getFuelTime()).map(OptionalInt::of).orElseGet(OptionalInt::empty), ()-> prefix + "Wrong fuel-time");
        boolean tool = isTool(definition);
        boolean armor = isArmor(definition);
        assertEquals(tool, item.isTool(), ()-> prefix + "Wrong isTool()");
        assertEquals(armor, item.isArmor(), ()-> prefix + "Wrong isArmor()");
        assertEquals(getTier(definition, tool, armor), item.getTier(), ()-> prefix + "Wrong tier");
        assertEquals(isTrue("axe", definition), item.isAxe(), ()-> prefix + "Wrong isAxe()");
        assertEquals(isTrue("pickaxe", definition), item.isPickaxe(), ()-> prefix + "Wrong isPickaxe()");
        assertEquals(isTrue("shovel", definition), item.isShovel(), ()-> prefix + "Wrong isShovel()");
        assertEquals(isTrue("hoe", definition), item.isHoe(), ()-> prefix + "Wrong isHoe()");
        assertEquals(isTrue("sword", definition), item.isSword(), ()-> prefix + "Wrong isSword()");
        assertEquals(isTrue("shears", definition), item.isShears(), ()-> prefix + "Wrong isShears()");
        assertEquals(isTrue("helmet", definition), item.isHelmet(), ()-> prefix + "Wrong isHelmet()");
        assertEquals(isTrue("chestplate", definition), item.isChestplate(), ()-> prefix + "Wrong isChestplate()");
        assertEquals(isTrue("leggings", definition), item.isLeggings(), ()-> prefix + "Wrong isLeggings()");
        assertEquals(isTrue("boots", definition), item.isBoots(), ()-> prefix + "Wrong isBoots()");
    }

    static int getTier(Element definition, boolean tool, boolean armor) {
        if (tool) {
            switch (definition.getAttribute("tier")) {
                case "wood":
                    return ItemTool.TIER_WOODEN;
                case "gold":
                    return ItemTool.TIER_GOLD;
                case "stone":
                    return ItemTool.TIER_STONE;
                case "iron":
                    return ItemTool.TIER_IRON;
                case "diamond":
                    return ItemTool.TIER_DIAMOND;
                case "netherite":
                    return ItemTool.TIER_NETHERITE;
                case "none":
                    return 0;
            }
        }
        if (armor) {
            switch (definition.getAttribute("tier")) {
                case "leather":
                    return ItemArmor.TIER_LEATHER;
                case "chain":
                    return ItemArmor.TIER_CHAIN;
                case "other":
                    return ItemArmor.TIER_OTHER;
                case "gold":
                    return ItemArmor.TIER_GOLD;
                case "iron":
                    return ItemArmor.TIER_IRON;
                case "diamond":
                    return ItemArmor.TIER_DIAMOND;
                case "netherite":
                    return ItemArmor.TIER_NETHERITE;
                case "none":
                    return 0;
            }
        }
        return 0;
    }

    static boolean isTool(Element definition) {
        String tool = definition.getAttribute("tool");
        if ("true".equals(tool)) {
            return true;
        } else if ("false".equals(tool)) {
            return false;
        } else {
            return isTrue("axe", definition) || isTrue("pickaxe", definition)
                    || isTrue("shovel", definition) || isTrue("hoe", definition)
                    || isTrue("sword", definition);
        }
    }

    static boolean isArmor(Element definition) {
        String tool = definition.getAttribute("armor");
        if ("true".equals(tool)) {
            return true;
        } else if ("false".equals(tool)) {
            return false;
        } else {
            return isTrue("helmet", definition) || isTrue("chestplate", definition)
                    || isTrue("leggings", definition) || isTrue("boots", definition);
        }
    }

    static Stream<Arguments> createComparisonStream() {
        return createItemStateStream()
                .map(stateDefinition -> {
                    String id = stateDefinition.getAttribute("namespaced-id");
                    String damage = stateDefinition.getAttribute("damage");
                    if (!damage.isEmpty()) {
                        id = id + ":" + damage;
                    }
                    Item item = Item.fromString(id);
                    return Arguments.of(item, stateDefinition);
                });
    }

    static void adjustEnchantability(Element element) {
        if (element.hasAttribute("enchantability")) {
            return;
        }
        int enchantability = getEnchantability(element);
        if (enchantability > 0) {
            element.setAttribute("enchantability", Integer.toString(enchantability));
        }
    }

    static OptionalInt getInt(String name, Element element) {
        String attr = element.getAttribute(name);
        if (attr.isEmpty()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(attr));
    }

    static boolean isTrue(String name, Element element) {
        return is(name, "true", element);
    }

    static boolean is(String name, String expected, Element element) {
        return expected.equals(element.getAttribute(name));
    }

    static Stream<Element> createItemStateStream() {
        return createItemDefinitionStream().flatMap(itemDefinition -> {
            NodeList itemStates = itemDefinition.getElementsByTagName("item-state");
            if (itemStates.getLength() == 0) {
                Element damageZero = createItemStateZero(itemDefinition);
                adjustEnchantability(damageZero);
                return Stream.of(damageZero);
            } else {
                //noinspection MismatchedQueryAndUpdateOfCollection
                ElementList<Element> elements = new ElementList<>(itemStates, Element.class);
                Stream<Element> stream = elements.stream();
                if (elements.stream().noneMatch(itemState-> "0".equals(itemState.getAttribute("damage")))) {
                    stream = Stream.concat(Stream.of(createItemStateZero(itemDefinition)), stream);
                }
                return stream
                        .map(itemState -> (Element) itemState.cloneNode(true))
                        .peek(itemState -> copyUnsetAttributes(itemDefinition, itemState))
                        .peek(BasicAttributesXmlTest::adjustEnchantability);
            }
        });
    }

    static Element createItemStateZero(Element itemDefinition) {
        Element itemState = document.createElement("item-state");
        itemState.setAttribute("damage", "0");
        copyUnsetAttributes(itemDefinition, itemState);
        return itemState;
    }

    static void copyUnsetAttributes(Element from, Element to) {
        NamedNodeMap attributes = from.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            if (attr.getNamespaceURI() != null) {
                if (!to.hasAttributeNS(attr.getNamespaceURI(), attr.getLocalName())) {
                    to.setAttributeNode((Attr) attr.cloneNode(false));
                }
            } else {
                if (!to.hasAttribute(attr.getName())) {
                    to.setAttributeNode((Attr) attr.cloneNode(false));
                }
            }
        }
    }

    static Stream<Element> createItemDefinitionStream() {
        return new ElementList<>(document.getElementsByTagName("item"), Element.class).stream();
    }

    static int getEnchantability(Element element) {
        if (isTrue("tool", element)) {
            switch (element.getAttribute("tier")) {
                case "wood":
                case "netherite":
                    return 15;
                case "stone":
                    return 5;
                case "iron":
                    return 14;
                case "gold":
                    return 22;
                case "diamond":
                    return 10;
                default:
                    return 1;
            }
        }
        if (isTrue("armor", element)) {
            switch (element.getAttribute("tier")) {
                case "leather":
                case "netherite":
                    return 15;
                case "chain":
                    return 12;
                case "iron":
                case "turtle":
                    return 9;
                case "gold":
                    return 25;
                case "diamond":
                    return 10;
                default:
                    return 1;
            }
        }
        return 0;
    }

    static class ElementList<E extends Node> extends AbstractList<E> implements RandomAccess {
        final NodeList nodeList;
        final Class<E> eClass;

        ElementList(NodeList nodeList, Class<E> eClass) {
            this.nodeList = nodeList;
            this.eClass = eClass;
        }

        @Override
        public E get(int index) {
            return eClass.cast(nodeList.item(index));
        }

        @Override
        public int size() {
            return nodeList.getLength();
        }

        @Override
        public Spliterator<E> spliterator() {
            return Spliterators.spliterator(this, ORDERED | DISTINCT | NONNULL | SIZED | SUBSIZED);
        }
    }

    @BeforeAll
    static void loadBasicAttributesXml() throws IOException, ParserConfigurationException, SAXException {
        try(InputStream is = Objects.requireNonNull(ItemTest.class.getClassLoader().getResourceAsStream("cn/nukkit/item/basicItemAttributes.xml"));
            BufferedInputStream input = new BufferedInputStream(is);
            InputStream dtdIS = Objects.requireNonNull(ItemTest.class.getClassLoader().getResourceAsStream("cn/nukkit/item/basicAttributes.dtd"));
            BufferedInputStream dtdInput = new BufferedInputStream(dtdIS)
        ) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true);
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver((publicId, systemId) -> {
                if (systemId == null || !systemId.endsWith("basicAttributes.dtd")) {
                    return null;
                }
                return new InputSource(dtdInput);
            });
            document = documentBuilder.parse(input);
        }
    }

    @AfterAll
    static void unloadBasicAttributesXml() {
        document = null;
    }
}
