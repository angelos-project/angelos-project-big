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
 * Represents a signed value that can be positive, zero, or negative.
 *
 * This enum is used to represent the sign of a value in a way that is compatible with
 * the BigInt class, allowing for easy comparison and manipulation of signed values.
 *
 * @property state The integer representation of the sign (1 for positive, 0 for zero, -1 for negative).
 * @property signed The signed integer representation (0 for positive, 0 for zero, -1 for negative).
 * */
public enum class BigSigned(public val state: Int, public val signed: Int) {
    POSITIVE(1, 0),
    ZERO(0, 0),
    NEGATIVE(-1, -1);

    /**
     * Returns a BigSigned value that is the negation of this value.
     *
     * @return POSITIVE if this is NEGATIVE, NEGATIVE if this is POSITIVE, or ZERO if this is ZERO.
     * */
    public fun negate(): BigSigned = when (this) {
        POSITIVE -> NEGATIVE
        NEGATIVE -> POSITIVE
        else -> this
    }

    /**
     * Returns true if the value is positive.
     *
     * @return true if the value is positive, false if zero or negative.
     * */
    public fun isPositive(): Boolean = this == POSITIVE

    /**
     * Returns true if the value is zero.
     *
     * @return true if the value is zero, false if negative or positive.
     * */
    public fun isZero(): Boolean = this == ZERO

    /**
     * Returns true if the value is negative.
     *
     * @return true if the value is negative, false if zero or positive.
     * */
    public fun isNegative(): Boolean = this == NEGATIVE

    /**
     * Returns true if the value is non-zero.
     *
     * @return true if the value is non-zero, false if zero.
     * */
    public fun isNonZero(): Boolean = when (this) {
        ZERO -> false
        else -> true
    }

    /**
     * Returns true if the value is zero or positive.
     *
     * @return true if the value is zero or positive, false if negative.
     * */
    public fun isNonNegative(): Boolean = when (this) {
        NEGATIVE -> false
        else -> true
    }
}