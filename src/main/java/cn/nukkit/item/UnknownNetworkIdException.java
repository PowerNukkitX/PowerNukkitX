
/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.item;

import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-03-23
 */


public class UnknownNetworkIdException extends IllegalStateException {
    @Nullable
    private final transient Item item;


    public UnknownNetworkIdException() {
        item = null;
    }


    public UnknownNetworkIdException(String s) {
        super(s);
        item = null;
    }


    public UnknownNetworkIdException(String message, Throwable cause) {
        super(message, cause);
        item = null;
    }


    public UnknownNetworkIdException(Throwable cause) {
        super(cause);
        item = null;
    }


    public UnknownNetworkIdException(Item item) {
        this.item = copy(item);
    }


    public UnknownNetworkIdException(Item item, String s) {
        super(s);
        this.item = copy(item);
    }


    public UnknownNetworkIdException(Item item, String message, Throwable cause) {
        super(message, cause);
        this.item = copy(item);
    }


    public UnknownNetworkIdException(Item item, Throwable cause) {
        super(cause);
        this.item = copy(item);
    }


    @Nullable
    public Item getItem() {
        return item == null? null : item.clone();
    }

    private static Item copy(Item item) {
        return item == null? null : item.clone();
    }
}
