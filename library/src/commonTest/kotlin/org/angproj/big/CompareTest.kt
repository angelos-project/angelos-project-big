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

import kotlin.test.Test
import kotlin.test.assertTrue

class CompareTest {

    @Test
    fun testGreaterThan() = withLogic {
        val number = BigInt.one.shiftLeft(256)
        assertTrue { number > number.dec() }
    }

    @Test
    fun testLesserThan() = withLogic {
        val number = BigInt.one.shiftLeft(256)
        assertTrue { number < number.inc() }
    }

    @Test
    fun testGreaterOrEqualThan() = withLogic {
        val number = BigInt.one.shiftLeft(256)
        assertTrue { number >= number }
        assertTrue { number >= number.dec() }
    }

    @Test
    fun testLesserOrEqualThan() = withLogic {
        val number = BigInt.one.shiftLeft(256)
        assertTrue { number <= number }
        assertTrue { number <= number.inc() }
    }
}