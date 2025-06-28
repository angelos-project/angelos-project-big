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
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.big

import org.angproj.sec.util.ensure


public fun BigInt.mod(value: BigInt): BigInt {
    ensure(value.sigNum.isPositive()) { BigMathException("Modulus must be positive.") }
    val result = remainder(value)
    return if(result.sigNum.isNonNegative()) result else result.add(value)
}