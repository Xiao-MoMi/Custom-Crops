/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core;

public enum CustomForm {

    TRIPWIRE(ExistenceForm.BLOCK),
    NOTE_BLOCK(ExistenceForm.BLOCK),
    MUSHROOM(ExistenceForm.BLOCK),
    CHORUS(ExistenceForm.BLOCK),
    ITEM_FRAME(ExistenceForm.FURNITURE),
    ITEM_DISPLAY(ExistenceForm.FURNITURE),
    ARMOR_STAND(ExistenceForm.FURNITURE),
    BLOCK(ExistenceForm.BLOCK),
    FURNITURE(ExistenceForm.FURNITURE);

    private final ExistenceForm form;

    CustomForm(ExistenceForm form) {
        this.form = form;
    }

    public ExistenceForm existenceForm() {
        return form;
    }
}
