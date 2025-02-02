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
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.big

import org.angproj.aux.io.TypeBits
import kotlin.math.max

public fun BigInt.clearBit(pos: Int): BigInt {
    require(pos >= 0) { BigMathException("Can not clear an imaginary bit at a negative position.") }

    val bigCnt = pos.floorDiv(TypeBits.int)
    val result = IntArray(max(intSize(this), (pos + 1).floorDiv(TypeBits.int) + 1))

    result.indices.forEach { result.revSet(it, getIdx(this, it))  }
    result.revSet(bigCnt, result.revGet(bigCnt) and bigMask(pos).inv())

    return fromIntArray(result)
}