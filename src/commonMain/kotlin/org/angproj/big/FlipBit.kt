/**
 * Copyright (c) 2023-2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
 *      Josh Bloch - earlier implementation
 *      Michael McCloskey - earlier implementation
 *      Alan Eliasen - earlier implementation
 *      Timothy Buktu - earlier implementation
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits
import kotlin.math.max

public fun BigInt.flipBit(pos: Int): BigInt {
    require(pos >= 0) { BigMathException("Can not flip an imaginary bit at a negative position.") }

    val bigCnt = pos.floorDiv(TypeBits.int)
    val result = IntArray(max(intSize(this), bigCnt + 2))

    result.indices.forEach { result.revSet(it, getIdx(this, it)) }
    result.revSet(bigCnt, result.revGet(bigCnt) xor bigMask(pos))

    return fromIntArray(result)
}