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

package net.momirealms.customcrops.api.misc.value;

import net.momirealms.customcrops.api.context.Context;

/**
 * The MathValue interface represents a mathematical value that can be evaluated
 * within a specific context. This interface allows for the evaluation of mathematical
 * expressions or plain numerical values in the context of custom crops mechanics.
 *
 * @param <T> the type of the holder object for the context
 */
public interface MathValue<T> {

    /**
     * Evaluates the mathematical value within the given context.
     *
     * @param context the context in which the value is evaluated
     * @return the evaluated value as a double
     */
    double evaluate(Context<T> context);

    /**
     * Evaluates the mathematical value within the given context.
     *
     * @param context the context in which the value is evaluated
     * @param parseRawPlaceholders whether to parse raw placeholders for instance %xxx%
     * @return the evaluated value as a double
     */
    default double evaluate(Context<T> context, boolean parseRawPlaceholders) {
        return evaluate(context);
    }

    /**
     * Creates a MathValue based on a mathematical expression.
     *
     * @param expression the mathematical expression to evaluate
     * @param <T> the type of the holder object for the context
     * @return a MathValue instance representing the given expression
     */
    static <T> MathValue<T> expression(String expression) {
        return new ExpressionMathValueImpl<>(expression);
    }

    /**
     * Creates a MathValue based on a plain numerical value.
     *
     * @param value the numerical value to represent
     * @param <T> the type of the holder object for the context
     * @return a MathValue instance representing the given plain value
     */
    static <T> MathValue<T> plain(double value) {
        return new PlainMathValueImpl<>(value);
    }

    /**
     * Creates a MathValue based on a range of values.
     *
     * @param value the ranged value to represent
     * @param <T> the type of the holder object for the context
     * @return a MathValue instance representing the given ranged value
     */
    static <T> MathValue<T> rangedDouble(String value) {
        return new RangedDoubleValueImpl<>(value);
    }

    /**
     * Creates a MathValue based on a range of values.
     *
     * @param value the ranged value to represent
     * @param <T> the type of the holder object for the context
     * @return a MathValue instance representing the given ranged value
     */
    static <T> MathValue<T> rangedInt(String value) {
        return new RangedIntValueImpl<>(value);
    }

    /**
     * Automatically creates a MathValue based on the given object.
     * If the object is a String, it is treated as a mathematical expression.
     * If the object is a numerical type (Double, Integer, Long, Float), it is treated as a plain value.
     *
     * @param o the object to evaluate and create a MathValue from
     * @param <T> the type of the holder object for the context
     * @return a MathValue instance representing the given object, either as an expression or a plain value
     * @throws IllegalArgumentException if the object type is not supported
     */
    static <T> MathValue<T> auto(Object o) {
        return auto(o, false);
    }

    static <T> MathValue<T> auto(Object o, boolean intFirst) {
        if (o instanceof String s) {
            if (s.contains("~")) {
                return intFirst ? (s.contains(".") ? rangedDouble(s) : rangedInt(s)) : rangedDouble(s);
            }
            try {
                return plain(Double.parseDouble(s));
            } catch (NumberFormatException e) {
                return expression(s);
            }
        } else if (o instanceof Number n) {
            return plain(n.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported type: " + o.getClass());
    }
}
