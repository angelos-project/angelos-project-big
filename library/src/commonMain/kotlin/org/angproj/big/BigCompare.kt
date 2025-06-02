/**
 * Copyright (c) 2023 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

/**
 * Represents the result of a comparison between two values.
 *
 * This enum is used to indicate whether one value is greater than, equal to, or less than another value.
 * It provides methods to check the state of the comparison and to convert it to a signed representation.
 *
 * @property state The integer representation of the comparison result (1 for greater, 0 for equal, -1 for lesser).
 * */
public enum class BigCompare(public val state: Int) {
    GREATER(1),
    EQUAL(0),
    LESSER(-1);

    /**
     * Returns true if this comparison result indicates that the left-hand side is greater than the right-hand side.
     *
     * @return true if this is GREATER, false otherwise.
     * */
    public fun isGreater(): Boolean = this == GREATER

    /**
     * Returns true if this comparison result indicates that the left-hand side is greater than or equal to the right-hand side.
     *
     * @return true if this is GREATER or EQUAL, false otherwise.
     * */
    public fun isGreaterOrEqual(): Boolean = this == GREATER || this == EQUAL

    /**
     * Returns true if this comparison result indicates that the left-hand side is equal to the right-hand side.
     *
     * @return true if this is EQUAL, false otherwise.
     * */
    public fun isEqual(): Boolean = this == EQUAL

    /**
     * Returns true if this comparison result indicates that the left-hand side is not equal to the right-hand side.
     *
     * @return true if this is not EQUAL, false otherwise.
     * */
    public fun isNotEqual(): Boolean = this != EQUAL

    /**
     * Returns true if this comparison result indicates that the left-hand side is less than or equal to the right-hand side.
     *
     * @return true if this is LESSER or EQUAL, false otherwise.
     * */
    public fun isLesserOrEqual(): Boolean = this == LESSER || this == EQUAL

    /**
     * Returns true if this comparison result indicates that the left-hand side is less than the right-hand side.
     *
     * @return true if this is LESSER, false otherwise.
     * */
    public fun isLesser(): Boolean = this == LESSER

    public fun withSigned(sigNum: BigSigned): BigSigned = when (state == sigNum.state) {
        true -> BigSigned.POSITIVE
        else -> BigSigned.NEGATIVE
    }
}