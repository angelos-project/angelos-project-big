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

import com.code_intelligence.jazzer.Jazzer
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

// https://android.googlesource.com/platform/external/jazzer-api/+/refs/tags/aml_uwb_330810010/README.md
// https://github.com/CodeIntelligenceTesting
// https://codeintelligencetesting.github.io/jazzer-docs/jazzer-api/com/code_intelligence/jazzer/api/package-summary.html
// https://symflower.com/en/company/blog/2022/jazzer-vs-symflower/
public interface FuzzerHelper {

    public fun<F: Any> run(
        factor1: F,
        factor2: F,
        fuzz: (f1: F, f2: F) -> ByteArray
    ): Pair<ByteArray, Boolean> {
        return try {
            Pair(fuzz(factor1, factor2), false)
        } catch (e: java.lang.Exception) {
            when {
                e is NumberFormatException -> Pair(byteArrayOf(), true)
                else -> throw e
            }
        } catch (e: BigMathException) {
            Pair(byteArrayOf(), true)
        }
    }

    public fun verify(
        r1: Pair<ByteArray, Boolean>,
        r2: Pair<ByteArray, Boolean>
    ) {
        assertContentEquals(r1.first, r2.first)
        assertEquals(r1.second, r2.second)
    }
}