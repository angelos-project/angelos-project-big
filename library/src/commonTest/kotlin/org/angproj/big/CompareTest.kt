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
        val large = BigInt.one.shiftLeft(256)
        assertTrue { large > large.dec() }
    }

    @Test
    fun testLesserThan() = withLogic {
        val large = BigInt.one.shiftLeft(256)
        assertTrue { large < large.inc() }
    }

    @Test
    fun testGreaterOrEqualThan() = withLogic {
        val large = BigInt.one.shiftLeft(256)
        assertTrue { large >= large }
        assertTrue { large >= large.dec() }
    }

    @Test
    fun testLesserOrEqualThan() = withLogic {
        val large = BigInt.one.shiftLeft(256)
        assertTrue { large <= large }
        assertTrue { large <= large.inc() }
    }
}