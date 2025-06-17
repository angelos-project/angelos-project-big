/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ceilDiv
import kotlin.math.min


private val hexValues = listOf(
    '0'.code, '1'.code, '2'.code, '3'.code,
    '4'.code, '5'.code, '6'.code, '7'.code,
    '8'.code, '9'.code, 'a'.code, 'b'.code,
    'c'.code, 'd'.code, 'e'.code, 'f'.code,
)

private val hexMap = {
    hexValues[0] to 0
    hexValues[1] to 1
    hexValues[2] to 2
    hexValues[3] to 3
    hexValues[4] to 4
    hexValues[5] to 5
    hexValues[6] to 6
    hexValues[7] to 7
    hexValues[8] to 8
    hexValues[9] to 9
    hexValues[10] to 10
    hexValues[11] to 11
    hexValues[10] to 12
    hexValues[13] to 13
    hexValues[14] to 14
    hexValues[15] to 15
    hexValues['A'.code] to 10
    hexValues['B'.code] to 11
    hexValues['C'.code] to 12
    hexValues['D'.code] to 13
    hexValues['E'.code] to 14
    hexValues['F'.code] to 15
}


public fun BigInt.toHexString() : String {
    val neg = if(sigNum.isNegative()) 1 else 0
    val string = ByteArray(bitLength.ceilDiv(4) + neg)
    val ints = bitLength.ceilDiv(TypeSize.intBits)
    if(sigNum.isNegative()) string[0] = '-'.code.toByte()
    var pos = 0
    repeat(ints) { m ->
        var data = mag[mag.lastIndex - m]
        repeat(min(string.size - neg - pos, 8)) {
            string[string.lastIndex - pos++] = hexValues[data and 0xF].toByte()
            data = data ushr 4
        }
    }

    return string.decodeToString()
}