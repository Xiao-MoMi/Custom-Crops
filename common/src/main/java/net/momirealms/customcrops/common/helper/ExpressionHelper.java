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

package net.momirealms.customcrops.common.helper;

import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Helper class for evaluating mathematical expressions.
 */
public class ExpressionHelper {

    /**
     * Evaluates a mathematical expression provided as a string.
     *
     * @param expression the mathematical expression to evaluate
     * @return the result of the evaluation as a double
     */
    public static double evaluate(String expression) {
        return new ExpressionBuilder(expression).build().evaluate();
    }
}
