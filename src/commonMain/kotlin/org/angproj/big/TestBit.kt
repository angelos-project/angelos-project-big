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
package org.angproj.big

import org.angproj.aux.io.TypeBits

public fun BigInt.testBit(pos: Int): Boolean {
    require(pos >= 0) { BigMathException("Can not test an imaginary bit at a negative position.") }
    return getIdx(this, pos.floorDiv(TypeBits.int)) and bigMask(pos) != 0
}