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
package org.angproj.big.newbig

import java.math.BigInteger
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue


class ExperimentDebugTestbed {

    val f1: String = "0000fffaffffffffff00000000000000"
    val f2: String = "00ffff00000000fffb"

    @Test
    fun testValueInsecure() {
        val bigIntegerF1 = BigInteger(f1, 16)
        val bigIntegerF2 = BigInteger(f2, 16)
        val bigIntF1 = internalOf(bigIntegerF1.toByteArray())
        val bigIntF2 = internalOf(bigIntegerF2.toByteArray())

        val result1: Pair<BigInt, BigInt> = try {
            bigIntF1.divideAndRemainder(bigIntF2)
        } catch (e: Exception) {
            println("-- BigInt: " + e.message)
            Pair(BigInt.zero, BigInt.zero)
        }
        val result2: Array<BigInteger> = try {
            bigIntegerF1.divideAndRemainder(bigIntegerF2)
        } catch (e: Exception) {
            println("-- BigInteger: " + e.message)
            arrayOf(BigInteger.ZERO, BigInteger.ZERO)
        }

        assertTrue { result1.first != BigInt.zero }

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalStdlibApi::class)
    @Test
    fun testValue() {
        val bigIntegerF1 = BigInteger(f1, 16)
        val bigIntegerF2 = BigInteger(f2, 16)
        val bigIntF1 = internalOf(bigIntegerF1.toByteArray())
        val bigIntF2 = internalOf(bigIntegerF2.toByteArray())

        val result1 = bigIntF1.divideAndRemainder(bigIntF2)
        val result2 = bigIntegerF1.divideAndRemainder(bigIntegerF2)

        println("Input: " + f1 + " " + f2)
        println("BigInt: " + toByteArray(result1.first.mag, result1.first.sigNum).toHexString() + " " + toByteArray(result1.second.mag, result1.second.sigNum).toHexString())
        println("BigInteger: " + result2[0].toByteArray().toHexString() + " " + result2[1].toByteArray().toHexString())

        assertContentEquals(
            toByteArray(result1.first.mag, result1.first.sigNum),
            result2[0].toByteArray(),
        )
        assertContentEquals(
            toByteArray(result1.second.mag, result1.second.sigNum),
            result2[1].toByteArray(),
        )
    }
}