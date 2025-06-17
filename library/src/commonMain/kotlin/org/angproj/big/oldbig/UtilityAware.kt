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


public interface UtilityAware {

    /**
     * Imported from angelos-project-buffer package.
     */
    public fun Byte.flipOnFlag0(): Byte = (this.toInt() or 0B00000001).toByte()
    public fun Byte.flipOnFlag1(): Byte = (this.toInt() or 0B00000010).toByte()
    public fun Byte.flipOnFlag2(): Byte = (this.toInt() or 0B00000100).toByte()
    public fun Byte.flipOnFlag3(): Byte = (this.toInt() or 0B00001000).toByte()
    public fun Byte.flipOnFlag4(): Byte = (this.toInt() or 0B00010000).toByte()
    public fun Byte.flipOnFlag5(): Byte = (this.toInt() or 0B00100000).toByte()
    public fun Byte.flipOnFlag6(): Byte = (this.toInt() or 0B01000000).toByte()
    public fun Byte.flipOnFlag7(): Byte = (this.toInt() or -0B10000000).toByte()

    public fun Byte.flipOffFlag0(): Byte = (this.toInt() and 0B11111110).toByte()
    public fun Byte.flipOffFlag1(): Byte = (this.toInt() and 0B11111101).toByte()
    public fun Byte.flipOffFlag2(): Byte = (this.toInt() and 0B11111011).toByte()
    public fun Byte.flipOffFlag3(): Byte = (this.toInt() and 0B11110111).toByte()
    public fun Byte.flipOffFlag4(): Byte = (this.toInt() and 0B11101111).toByte()
    public fun Byte.flipOffFlag5(): Byte = (this.toInt() and 0B11011111).toByte()
    public fun Byte.flipOffFlag6(): Byte = (this.toInt() and 0B10111111).toByte()
    public fun Byte.flipOffFlag7(): Byte = (this.toInt() and 0B01111111).toByte()

    public fun Byte.checkFlag0(): Boolean = (this.toInt() and 0B00000001) == 1
    public fun Byte.checkFlag1(): Boolean = (this.toInt() and 0B00000010) == 2
    public fun Byte.checkFlag2(): Boolean = (this.toInt() and 0B00000100) == 4
    public fun Byte.checkFlag3(): Boolean = (this.toInt() and 0B00001000) == 8
    public fun Byte.checkFlag4(): Boolean = (this.toInt() and 0B00010000) == 16
    public fun Byte.checkFlag5(): Boolean = (this.toInt() and 0B00100000) == 32
    public fun Byte.checkFlag6(): Boolean = (this.toInt() and 0B01000000) == 64
    public fun Byte.checkFlag7(): Boolean = (this.toInt() and -0B10000000) == -128

    public fun Short.swapEndian(): Short = swapShort<Unit>(this)
    public fun UShort.swapEndian(): UShort = convS2US<Unit>(swapShort<Unit>(convUS2S<Unit>(this)))
    public fun Int.swapEndian(): Int = swapInt<Unit>(this)
    public fun UInt.swapEndian(): UInt = convI2UI<Unit>(swapInt<Unit>(convUI2I<Unit>(this)))
    public fun Long.swapEndian(): Long = swapLong<Long>(this)
    public fun ULong.swapEndian(): ULong = convL2UL<Unit>(swapLong<Long>(convUL2L<Unit>(this)))
    public fun Float.swapEndian(): Float = convI2F<Unit>(swapInt<Unit>(convF2I<Unit>(this)))
    public fun Double.swapEndian(): Double = convL2D<Unit>(swapLong<Long>(convD2L<Unit>(this)))

    public fun Byte.conv2uB(): UByte = convB2UB<Unit>(this)
    public fun UByte.conv2B(): Byte = convUB2B<Unit>(this)
    public fun Short.conv2uS(): UShort = convS2US<Unit>(this)
    public fun UShort.conv2S(): Short = convUS2S<Unit>(this)
    public fun Int.conv2uI(): UInt = convI2UI<Unit>(this)
    public fun UInt.conv2I(): Int = convUI2I<Unit>(this)
    public fun Int.conv2F(): Float = convI2F<Unit>(this)
    public fun Float.conv2I(): Int = convF2I<Unit>(this)
    public fun Long.conv2uL(): ULong = convL2UL<Unit>(this)
    public fun ULong.conv2L(): Long = convUL2L<Unit>(this)
    public fun Long.conv2D(): Double = convL2D<Unit>(this)
    public fun Double.conv2L(): Long = convD2L<Unit>(this)

    public fun ByteArray.readShortAt(offset: Int): Short = getShort<Unit>(this, offset)
    public fun ByteArray.readUShortAt(offset: Int): UShort = convS2US<Unit>(getShort<Unit>(this, offset))
    public fun ByteArray.readIntAt(offset: Int): Int = getInt<Unit>(this, offset)
    public fun ByteArray.readUIntAt(offset: Int): UInt = convI2UI<Unit>(getInt<Unit>(this, offset))
    public fun ByteArray.readLongAt(offset: Int): Long = getLong<Unit>(this, offset)
    public fun ByteArray.readULongAt(offset: Int): ULong = convL2UL<Unit>(getLong<Unit>(this, offset))
    public fun ByteArray.readFloatAt(offset: Int): Float = convI2F<Unit>(getInt<Unit>(this, offset))
    public fun ByteArray.readDoubleAt(offset: Int): Double = convL2D<Unit>(getLong<Unit>(this, offset))

    public fun ByteArray.writeShortAt(offset: Int, value: Short) { setShort<Unit>(this, offset, value) }
    public fun ByteArray.writeUShortAt(offset: Int, value: UShort) { setShort<Unit>(this, offset, convUS2S<Unit>(value)) }
    public fun ByteArray.writeIntAt(offset: Int, value: Int) { setInt<Unit>(this, offset, value) }
    public fun ByteArray.writeUIntAt(offset: Int, value: UInt) { setInt<Unit>(this, offset, convUI2I<Unit>(value)) }
    public fun ByteArray.writeLongAt(offset: Int, value: Long) { setLong<Unit>(this, offset, value) }
    public fun ByteArray.writeULongAt(offset: Int, value: ULong) { setLong<Unit>(this, offset, convUL2L<Unit>(value)) }
    public fun ByteArray.writeFloatAt(offset: Int, value: Float) { setInt<Unit>(this, offset, convF2I<Unit>(value)) }
    public fun ByteArray.writeDoubleAt(offset: Int, value: Double) { setLong<Unit>(this, offset, convD2L<Unit>(value)) }

    public fun ByteArray.readRevShortAt(offset: Int): Short = getRevShort<Unit>(this, offset)
    public fun ByteArray.readRevUShortAt(offset: Int): UShort = convS2US<Unit>(getRevShort<Unit>(this, offset))
    public fun ByteArray.readRevIntAt(offset: Int): Int = getRevInt<Unit>(this, offset)
    public fun ByteArray.readRevUIntAt(offset: Int): UInt = convI2UI<Unit>(getRevInt<Unit>(this, offset))
    public fun ByteArray.readRevLongAt(offset: Int): Long = getRevLong<Unit>(this, offset)
    public fun ByteArray.readRevULongAt(offset: Int): ULong = convL2UL<Unit>(getRevLong<Unit>(this, offset))
    public fun ByteArray.readRevFloatAt(offset: Int): Float = convI2F<Unit>(getRevInt<Unit>(this, offset))
    public fun ByteArray.readRevDoubleAt(offset: Int): Double = convL2D<Unit>(getRevLong<Unit>(this, offset))

    public fun ByteArray.writeRevShortAt(offset: Int, value: Short) { setRevShort<Unit>(this, offset, value) }
    public fun ByteArray.writeRevUShortAt(offset: Int, value: UShort) { setRevShort<Unit>(this, offset, convUS2S<Unit>(value)) }
    public fun ByteArray.writeRevIntAt(offset: Int, value: Int) { setRevInt<Unit>(this, offset, value) }
    public fun ByteArray.writeRevUIntAt(offset: Int, value: UInt) { setRevInt<Unit>(this, offset, convUI2I<Unit>(value)) }
    public fun ByteArray.writeRevLongAt(offset: Int, value: Long) { setRevLong<Unit>(this, offset, value) }
    public fun ByteArray.writeRevULongAt(offset: Int, value: ULong) { setRevLong<Unit>(this, offset, convUL2L<Unit>(value)) }
    public fun ByteArray.writeRevFloatAt(offset: Int, value: Float) { setRevInt<Unit>(this, offset, convF2I<Unit>(value)) }
    public fun ByteArray.writeRevDoubleAt(offset: Int, value: Double) { setRevLong<Unit>(this, offset, convD2L<Unit>(value)) }

    private companion object: AbstractUtilityAware()
}

public object UtilityAwareContext : UtilityAware

public fun <T> withUtility(block: UtilityAwareContext.() -> T): T = UtilityAwareContext.block()
public suspend fun <T> withAsyncUtil(block: suspend UtilityAwareContext.() -> T): T = UtilityAwareContext.block()


public inline fun <reified T> withUtility(
    array: ByteArray, block: UtilityAwareContext.(array: ByteArray) -> T
): T = UtilityAwareContext.block(array)