/**
 * Copyright (c) 2021-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.big.oldbig

public abstract class AbstractUtilityAware {

    protected inline fun<reified R: Any> joinLong(
        u: Int, l: Int
    ): Long = ((u.toLong() shl 32 and -0x100000000) or (l.toLong() and 0xFFFFFFFF))
    protected inline fun<reified R: Any> joinInt(
        u: Short, l: Short
    ): Int = (u.toInt() shl 16 and -0x10000) or (l.toInt() and 0xFFFF)
    protected inline fun<reified R: Any> joinShort(
        u: Byte, l: Byte
    ): Short = ((u.toInt() shl 8 and 0xFF00) or (l.toInt() and 0xFF)).toShort()

    protected inline fun<reified R: Any> upperLong(value: Long): Int = (value ushr 32).toInt()
    protected inline fun<reified R: Any> upperInt(value: Int): Short = (value ushr 16).toShort()
    protected inline fun<reified R: Any> upperShort(value: Short): Byte = (value.toInt() ushr 8).toByte()

    protected inline fun<reified R: Any> lowerLong(value: Long): Int = value.toInt()
    protected inline fun<reified R: Any> lowerInt(value: Int): Short = value.toShort()
    protected inline fun<reified R: Any> lowerShort(value: Short): Byte = value.toByte()

    /**
     * Type swapping stuff
     * */
    protected inline fun<reified R: Any> swapLong(
        value: Long
    ): Long = joinLong<R>(swapInt<R>(lowerLong<R>(value)), swapInt<R>(upperLong<R>(value)))
    protected inline fun<reified R: Any> swapInt(
        value: Int)
    : Int = joinInt<R>(swapShort<R>(lowerInt<R>(value)), swapShort<R>(upperInt<R>(value)))
    protected inline fun<reified R: Any> swapShort(
        value: Short
    ): Short = joinShort<R>(lowerShort<R>(value), upperShort<R>(value))

    /**
     * Type conversion stuff
     * */
    protected inline fun<reified R: Any> convL2UL(value: Long): ULong = value.toULong()
    protected inline fun<reified R: Any> convUL2L(value: ULong): Long = value.toLong()
    protected inline fun<reified R: Any> convL2D(value: Long): Double = Double.fromBits(value)
    protected inline fun<reified R: Any> convD2L(value: Double): Long = value.toRawBits()
    protected inline fun<reified R: Any> convI2UI(value: Int): UInt = value.toUInt()
    protected inline fun<reified R: Any> convUI2I(value: UInt): Int = value.toInt()
    protected inline fun<reified R: Any> convI2F(value: Int): Float = Float.fromBits(value)
    protected inline fun<reified R: Any> convF2I(value: Float): Int = value.toRawBits()
    protected inline fun<reified R: Any> convS2US(value: Short): UShort = value.toUShort()
    protected inline fun<reified R: Any> convUS2S(value: UShort): Short = value.toShort()
    protected inline fun<reified R: Any> convB2UB(value: Byte): UByte = value.toUByte()
    protected inline fun<reified R: Any> convUB2B(value: UByte): Byte = value.toByte()

    /**
     * ByteArray read and write stuff
     * */
    protected inline fun <reified R : Any> getLong(
        res: ByteArray, pos: Int
    ): Long = joinLong<R>(getInt<R>(res, pos + 4), getInt<R>(res, pos))

    protected inline fun <reified R : Any> getInt(
        res: ByteArray, pos: Int
    ): Int = joinInt<R>(getShort<R>(res, pos + 2), getShort<R>(res, pos))

    protected inline fun <reified R : Any> getShort(
        res: ByteArray, pos: Int
    ): Short = joinShort<R>(res[pos + 1], res[pos])

    protected inline fun <reified R : Any> setLong(res: ByteArray, pos: Int, value: Long): Unit {
        setInt<R>(res, pos + 4, upperLong<R>(value))
        setInt<R>(res, pos, lowerLong<R>(value))
    }

    protected inline fun <reified R : Any> setInt(res: ByteArray, pos: Int, value: Int): Unit {
        setShort<R>(res, pos + 2, upperInt<R>(value))
        setShort<R>(res, pos, lowerInt<R>(value))
    }

    protected inline fun <reified R : Any> setShort(res: ByteArray, pos: Int, value: Short): Unit {
        res[pos + 1] = upperShort<R>(value)
        res[pos] = lowerShort<R>(value)
    }

    protected inline fun <reified R : Any> getRevShort(
        res: ByteArray, pos: Int
    ): Short = joinShort<R>(res[pos], res[pos + 1])

    protected inline fun <reified R : Any> getRevInt(
        res: ByteArray, pos: Int
    ): Int = joinInt<R>(getRevShort<R>(res, pos), getRevShort<R>(res, pos + 2))

    protected inline fun <reified R : Any> getRevLong(
        res: ByteArray, pos: Int
    ): Long = joinLong<R>(getRevInt<R>(res, pos), getRevInt<R>(res, pos + 4))

    protected inline fun <reified R : Any> setRevLong(res: ByteArray, pos: Int, value: Long) {
        setRevInt<R>(res, pos, upperLong<R>(value))
        setRevInt<R>(res, pos + 4, lowerLong<R>(value))
    }

    protected inline fun <reified R : Any> setRevInt(res: ByteArray, pos: Int, value: Int) {
        setRevShort<R>(res, pos, upperInt<R>(value))
        setRevShort<R>(res, pos + 2, lowerInt<R>(value))
    }

    protected inline fun <reified R : Any> setRevShort(res: ByteArray, pos: Int, value: Short) {
        res[pos] = upperShort<R>(value)
        res[pos + 1] = lowerShort<R>(value)
    }

    /**
     * Public functions
     * */

    //protected val networkDifference: Boolean = Platform.isBigEndian()
}