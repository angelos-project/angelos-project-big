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

public infix fun BigInt.xor(value: BigMath<*>): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = getIdx(this@xor, idx) xor getIdx(value, idx)
        }
    }

    return ofIntArray(result)
}