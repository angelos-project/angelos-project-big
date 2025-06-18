/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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


import java.math.BigInteger
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue


class ExperimentDebugTestbed2 {

    val f1: String = "01ffffffffffff91910000000000ffff"
    val f2: String = "01ffffffffffff91910000d0c3"

    @Test
    fun testValueInsecure() {
        val bigIntegerF1 = BigInteger(f1.encodeToByteArray())
        val bigIntegerF2 = BigInteger(f2.encodeToByteArray())
        val bigIntF1 = bigIntOf(f1.encodeToByteArray())
        val bigIntF2 = bigIntOf(f2.encodeToByteArray())

        val result1: Pair<BigInt, BigInt> = try {
            bigIntF1.divideAndRemainder(bigIntF2)
        } catch (e: Exception) {
            println("-- BigInt: " + e.message)
            Pair(BigInt.Companion.zero, BigInt.Companion.zero)
        }
        val result2: Array<BigInteger> = try {
            bigIntegerF1.divideAndRemainder(bigIntegerF2)
        } catch (e: Exception) {
            println("-- BigInteger: " + e.message)
            arrayOf(BigInteger.ZERO, BigInteger.ZERO)
        }

        assertTrue { result1.first != BigInt.Companion.zero }

        assertContentEquals(
            result1.first.toByteArray(),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            result1.second.toByteArray(),
            result2[1].toByteArray(),
        )
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalStdlibApi::class)
    @Test
    fun testValue() {
        val bigIntegerF1 = BigInteger(f1.encodeToByteArray())
        val bigIntegerF2 = BigInteger(f2.encodeToByteArray())
        val bigIntF1 = bigIntOf(f1.encodeToByteArray())
        val bigIntF2 = bigIntOf(f2.encodeToByteArray())

        val result1 = bigIntF1.divideAndRemainder(bigIntF2)
        val result2 = bigIntegerF1.divideAndRemainder(bigIntegerF2)

        println("Input: " + f1 + " " + f2)
        println("BigInt: " + result1.first.toByteArray().toHexString() + " " + result1.second.toByteArray().toHexString())
        println("BigInteger: " + result2[0].toByteArray().toHexString() + " " + result2[1].toByteArray().toHexString())

        assertContentEquals(
            result1.first.toByteArray(),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            result1.second.toByteArray(),
            result2[1].toByteArray(),
        )
    }
}