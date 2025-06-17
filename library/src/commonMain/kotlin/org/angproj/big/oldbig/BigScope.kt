/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import kotlin.math.max

public interface BigScope: UtilityAware {

    public fun bigMask(pos: Int): Int = 1 shl (pos and Int.SIZE_BITS - 1)

    public fun <A: List<Int>, B: List<Int>> maxOfArrays(x: A, y: B, extra: Int = 1): IntArray =
        IntArray(max(x.size, y.size) + extra)

    public fun IntArray.revIdx(index: Int): Int = lastIndex - index
    public fun IntArray.revGet(index: Int): Int = this[lastIndex - index]
    public fun IntArray.revSet(index: Int, value: Int) { this[lastIndex - index] = value }

    public fun <E : List<Int>> E.getL(index: Int): Long = this[index].getL()
    public fun <E : List<Int>> E.revIdx(index: Int): Int = lastIndex - index
    public fun <E : List<Int>> E.revGet(index: Int): Int = this[lastIndex - index]
    public fun <E : List<Int>> E.revGetL(index: Int): Long = this[lastIndex - index].getL()

    public fun <I: BigMath<*>, O: BigMath<*>> biggerFirst(
        x: I,
        y: I,
        block: (x: I, y: I) -> O
    ): O = when (x.mag.size < y.mag.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

    public fun Int.getL(): Long = this.toLong() and 0xffffffffL
}