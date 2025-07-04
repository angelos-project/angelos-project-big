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
@file:OptIn(ExperimentalStdlibApi::class)

package org.angproj.big

import org.angproj.sec.util.TypeSize
import java.math.BigInteger as JavaBigInteger

public fun ByteArray.writeIntAt(index: Int, value: Int) {
    repeat(4) {
        this[it + index] = (value ushr (it * TypeSize.byteBits)).toByte()
    }
}

public fun ByteArray.writeLongAt(index: Int, value: Long) {
    repeat(8) {
        this[it + index] = (value ushr (it * TypeSize.byteBits)).toByte()
    }
}

object Debugger {

    private const val sigMagError = "sigNum and magnitude not equal, either bigger or not zero."
    private const val magSizeError = "Magnitude has leading zero integers."

    @OptIn(ExperimentalStdlibApi::class)
    fun printValue(name: String, value: Int) {
        val arr = ByteArray(4)
        arr.writeIntAt(0, value)
        println("V$name: ${arr.toHexString()}")
    }

    fun printValue(name: String, value: Long) {
        val arr = ByteArray(8)
        arr.writeLongAt(0, value)
        println("V$name: ${arr.toHexString()}")
    }

    fun printValue(name: String, value: ByteArray) {
        println("V$name: ${value.toHexString()}")
    }

    fun printDecimal(name: String, value: JavaBigInteger) {
        println("D$name: $value")
    }

    fun printJava(name: String, value: JavaBigInteger) {
        println("J$name: ${value.toByteArray().toHexString()}")
    }

    fun printKotlin(name: String, value: BigInt) {
        // println("K$name: ${value.toByteArray().toHexString()}")
        println("K: ==== ==== START === ====")
        println(name)
        value.printDebug()
        println("K: ==== ==== OVER ==== ====")
    }

    fun printJavaResult(value: JavaBigInteger) {
        println("Rj: ${value.toByteArray().toHexString()}")
    }

    fun printKotlinRestult(value: BigInt) {
        //println("Rk: ${value.toByteArray().toHexString()}")
        println("Rk: ==== ==== START === ====")
        value.printDebug()
        println("Rk: ==== ==== OVER ==== ====")
    }

    /*private fun keep(value: List<Int>, sigNum: BigSigned): Int {
        val keep = value.indexOfFirst { it != sigNum.signed }
        return when (keep) {
            -1 -> value.size
            else -> keep
        }
    }*/

    fun assertContentEquals(
        x: Long,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(
                rK.sigNum.isZero() and rK.mag.isEmpty() ||
                        rK.sigNum.isNonZero() and rK.mag.isNotEmpty(),
                sigMagError
            )
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            /*println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEquals(
        x: ByteArray,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(
                rK.sigNum.isZero() and rK.mag.isEmpty() ||
                        rK.sigNum.isNonZero() and rK.mag.isNotEmpty(),
                sigMagError
            )
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            /*println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }

    fun <E: Any> assertEquals(
        x: ByteArray,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: E,
        rK: E,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertEquals(
                rK,
                rJ
            )
            kotlin.test.assertTrue(
                kX.sigNum.isZero() and kX.mag.isEmpty() ||
                        kX.sigNum.isNonZero() and kX.mag.isNotEmpty(),
                sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            println("Rj: $rJ")
            println("Rk: $rK")
            /*println("kM ${kX.mag.size}")
            println("kS ${kX.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }

    fun <E: Any> assertEquals(
        x: ByteArray,
        y: Int,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: E,
        rK: E,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertEquals(
                rK,
                rJ
            )
            kotlin.test.assertTrue(
                kX.sigNum.isZero() and kX.mag.isEmpty() ||
                        kX.sigNum.isNonZero() and kX.mag.isNotEmpty(),
                sigMagError
            )
        } catch (e: AssertionError) {
            printValue("x", x)
            println("Vy: $y")
            printJava("x", jX)
            printKotlin("x", kX)
            println("Rj: $rJ")
            println("Rk: $rK")
            /*println("kM ${kX.mag.size}")
            println("kS ${kX.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }

    fun <E: Any> assertEquals(
        x: ByteArray,
        y: ByteArray,
        jX: JavaBigInteger,
        jY: JavaBigInteger,
        kX: BigInt,
        kY: BigInt,
        rJ: E,
        rK: E,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertEquals(
                rK,
                rJ
            )
            kotlin.test.assertTrue(
                kX.sigNum.isZero() and kX.mag.isEmpty() ||
                        kX.sigNum.isNonZero() and kX.mag.isNotEmpty(),
                sigMagError
            )
            kotlin.test.assertTrue(
                kY.sigNum.isZero() and kY.mag.isEmpty() ||
                        kY.sigNum.isNonZero() and kY.mag.isNotEmpty(),
                sigMagError
            )
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printDecimal("x", jX)
            printValue("y", y)
            printJava("y", jY)
            printKotlin("y", kY)
            printDecimal("y", jY)
            println("Rj: $rJ")
            println("Rj: $rK")

            if(!fuzz) throw e
        }
    }

    fun assertContentEquals(
        x: ByteArray,
        y: Int,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(
                rK.sigNum.isZero() and rK.mag.isEmpty() ||
                        rK.sigNum.isNonZero() and rK.mag.isNotEmpty(),
                sigMagError
            )
        } catch (e: AssertionError) {
            printValue("x", x)
            println("Vy: $y")
            printJava("x", jX)
            printKotlin("x", kX)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            /*println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEquals(
        x: ByteArray,
        y: ByteArray,
        jX: JavaBigInteger,
        jY: JavaBigInteger,
        kX: BigInt,
        kY: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(
                rK.sigNum.isZero() and rK.mag.isEmpty() ||
                        rK.sigNum.isNonZero() and rK.mag.isNotEmpty(),
                sigMagError
            )
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printDecimal("x", jX)
            printValue("y", y)
            printJava("y", jY)
            printKotlin("y", kY)
            printDecimal("y", jY)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            /*println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEqualsDouble(
        x: ByteArray,
        y: ByteArray,
        jX: JavaBigInteger,
        jY: JavaBigInteger,
        kX: BigInt,
        kY: BigInt,
        rJ: Array<JavaBigInteger>,
        rK: Pair<BigInt, BigInt>,
        fuzz: Boolean = false
    ) {
        try {
            //println("FIRST")
            kotlin.test.assertContentEquals(
                rK.first.toByteArray(),
                rJ.first().toByteArray()
            )
            //println("SECOND")
            kotlin.test.assertContentEquals(
                rK.second.toByteArray(),
                rJ.last().toByteArray()
            )
            //println("THIRD")
            kotlin.test.assertTrue(
                rK.first.sigNum.isZero() and rK.first.mag.isEmpty() ||
                        rK.first.sigNum.isNonZero() and rK.first.mag.isNotEmpty(),
                sigMagError
            )
            //println("FOURTH")
            kotlin.test.assertTrue(
                rK.second.sigNum.isZero() and rK.second.mag.isEmpty() ||
                        rK.second.sigNum.isNonZero() and rK.second.mag.isNotEmpty(),
                sigMagError
            )
            //println("FIFTH")
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printDecimal("x", jX)
            printValue("y", y)
            printJava("y", jY)
            printKotlin("y", kY)
            printDecimal("y", jY)
            println("Rj A: ${rJ.first().toByteArray().toHexString()}")
            println("Rk A: ${rK.first.toByteArray().toHexString()}")
            println("Rj B: ${rJ.last().toByteArray().toHexString()}")
            println("Rk B: ${rK.second.toByteArray().toHexString()}")
            printKotlinRestult(rK.first)
            printKotlinRestult(rK.second)
            /*println("kM A ${rK.first.mag.size}")
            println("kS A ${rK.first.sigNum.state}")
            println("kM B ${rK.second.mag.size}")
            println("kS B ${rK.second.sigNum.state}")*/
            println()

            if(!fuzz) throw e
        }
    }
}