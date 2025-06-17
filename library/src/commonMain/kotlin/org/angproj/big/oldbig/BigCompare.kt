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
package org.angproj.big.oldbig

public enum class BigCompare(public val state: Int) {
    GREATER(1),
    EQUAL(0),
    LESSER(-1);

    public fun isGreater(): Boolean = this == GREATER

    public fun isGreaterOrEqual(): Boolean = this == GREATER || this == EQUAL

    public fun isEqual(): Boolean = this == EQUAL

    public fun isNotEqual(): Boolean = this != EQUAL

    public fun isLesserOrEqual(): Boolean = this == LESSER || this == EQUAL

    public fun isLesser(): Boolean = this == LESSER

    public fun withSigned(sigNum: BigSigned): BigSigned = when (state == sigNum.state) {
        true -> BigSigned.POSITIVE
        else -> BigSigned.NEGATIVE
    }
}