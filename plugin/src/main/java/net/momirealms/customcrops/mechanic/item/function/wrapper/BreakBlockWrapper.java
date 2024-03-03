/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BreakBlockWrapper extends BreakWrapper {

    private final Block brokenBlock;

    public BreakBlockWrapper(Player player, Block brokenBlock) {
        super(player, brokenBlock.getLocation());
        this.brokenBlock = brokenBlock;
    }

    public Block getBrokenBlock() {
        return brokenBlock;
    }
}

