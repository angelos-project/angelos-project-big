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

public fun BigInt.andNot(value: BigMath<*>): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = getIdx(this@andNot, idx) and getIdx(value, idx).inv()
        }
    }
    return ofIntArray(result)
}