package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonaPieceTest {

    @Test
    void fieldsMatchConstructorArgs() {
        PersonaPiece piece = new PersonaPiece("id1", "skin", "pack1", true, "prod1");
        assertEquals("id1", piece.id);
        assertEquals("skin", piece.type);
        assertEquals("pack1", piece.packId);
        assertTrue(piece.isDefault);
        assertEquals("prod1", piece.productId);
    }

    @Test
    void equalPiecesShareHashCode() {
        PersonaPiece a = new PersonaPiece("id", "t", "p", false, "prod");
        PersonaPiece b = new PersonaPiece("id", "t", "p", false, "prod");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentFieldsAreNotEqual() {
        PersonaPiece a = new PersonaPiece("id", "t", "p", false, "prod");
        PersonaPiece b = new PersonaPiece("id", "t", "p", true, "prod");
        assertNotEquals(a, b);
    }
}
