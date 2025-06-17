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
package org.angproj.big.oldbig


public interface BigMath<E : List<Int>>: BigScope {
    public val mag: E
    public val sigNum: BigSigned

    public val bitCount: Int
    public val bitLength: Int
    public val firstNonZero: Int

    public fun equalsCompare(x: Any): Boolean {
        if(x === this) return true
        if(x !is BigMath<*>) return false
        if(sigNum != x.sigNum) return false
        if(mag.size != x.mag.size) return false
        return mag.indices.indexOfFirst { mag[it] != x.mag[it] } == -1
    }

    public fun getIdx(index: Int): Int = when {
        index < 0 -> 0
        index >= mag.size -> sigNum.signed
        else -> {
            val num = mag.revGet(index)
            when {
                sigNum.isNonNegative() -> num
                index <= firstNonZero -> -num
                else -> num.inv()
            }
        }
    }

    public fun getIdxL(index: Int): Long = getIdx(index).toLong() and 0xffffffffL

    public fun getUnreversedIdx(index: Int): Int {
        val num = mag[index]
        return when {
            sigNum.isNonNegative() -> num
            index <= firstNonZero -> -num
            else -> num.inv()
        }
    }

    public fun intSize(): Int = bitLength.floorDiv(Int.SIZE_BITS) + 1

    public fun asMutableBigInt(): MutableBigInt
    public fun toBigInt(): BigInt


    /**
     * Unknown partial error
     *
     * Vx: ffff
     * Jx: ff
     * Kx: ff
     * Rj: 0
     * Rk: 1
     * kM 1
     * kS -1
     *
     *
     * Expected :1
     * Actual   :0
     *
     * fun testToSize()
     * */
    public fun getByteSize(): Int {
        if (sigNum.isZero()) return 1

        val output = ByteArray(mag.size * 4)
        mag.indices.forEach { output.writeIntAt(it * 4, getIdx(mag.lastIndex - it).swapEndian()) }
        val keep = keep(output, sigNum)

        if (keep == output.size) return 1

        val prepend = sigNum.isNonNegative() == output[keep] < 0
        return when {
            keep == 0 && prepend -> 1 + output.size
            keep == 1 && prepend -> output.size
            keep > 1 && prepend -> 1 + output.size - keep
            keep > 0 -> output.size - keep
            else -> output.size
        }
    }

    public fun toByteArray(): ByteArray {
        if (sigNum.isZero()) return byteArrayOf(0)

        val output = ByteArray(mag.size * 4)
        mag.indices.forEach { output.writeIntAt(it * 4, getIdx(mag.lastIndex - it).swapEndian()) }
        val keep = keep(output, sigNum)

        if (keep == output.size) return byteArrayOf(sigNum.signed.toByte())

        val prepend = sigNum.isNonNegative() == output[keep] < 0
        return when {
            keep == 0 && prepend -> byteArrayOf(sigNum.signed.toByte()) + output
            keep == 1 && prepend -> output.also { it[0] = sigNum.signed.toByte() }
            keep > 1 && prepend -> byteArrayOf(sigNum.signed.toByte()) + output.copyOfRange(keep, output.size)
            keep > 0 -> output.copyOfRange(keep, output.size)
            else -> output
        }
    }

    public companion object: UtilityAware {

        public fun bitSizeForInt(n: Int): Int = Int.SIZE_BITS - n.countLeadingZeroBits()

        public fun <E : List<Int>> bitLength(mag: E, sigNum: BigSigned): Int = when {
            mag.isEmpty() -> 0
            sigNum.isNonNegative() -> (mag.lastIndex * 32) + Int.SIZE_BITS - mag[0].countLeadingZeroBits()
            else -> {
                val pow2 = if(mag.size == 1) mag[0].countOneBits() == 1 else mag[0] == 0
                val magBitLength: Int = (mag.lastIndex * 32) + Int.SIZE_BITS - mag[0].countLeadingZeroBits()
                if (pow2) magBitLength - 1 else magBitLength
            }
        }

        public fun <E : List<Int>> bitCount(mag: E, sigNum: BigSigned): Int {
            var count = mag.sumOf { it.countOneBits() }
            if (sigNum.isNegative()) {
                var magTrailingZeroCount = 0
                var j: Int = mag.lastIndex
                while (mag[j] == 0) {
                    magTrailingZeroCount += 32
                    j--
                }
                magTrailingZeroCount += mag[j].countTrailingZeroBits()
                count += magTrailingZeroCount - 1
            }
            return count
        }

        public fun <E : List<Int>> firstNonZero(mag: E): Int = (
                mag.lastIndex downTo 0).indexOfFirst { mag[it] != 0 }.let { if (it == -1) 0 else it }

        public fun <T : BigMath<*>> fromByteArray(
            value: ByteArray,
            build: (IntArray, BigSigned) -> T
        ): T {
            if(!value.isNotEmpty()) throw BigMathException("ByteArray must not be of zero length.")
            val negative = value.first().toInt() < 0

            val sigNum = when (negative) {
                true -> BigSigned.NEGATIVE
                else -> BigSigned.POSITIVE
            }
            val mag = when (negative) {
                true -> makePositive(value)
                else -> stripLeadingZeros(value)
            }

            return build(mag, sigNumZeroAdjust(mag, sigNum))
        }

        private fun makePositive(value: ByteArray): IntArray {
            val keep = keep(value, BigSigned.NEGATIVE)
            val extra = (keep until value.size).indexOfFirst {
                value[it].toInt() != 0 }.let { if (it == -1) 1 else 0 }
            val result = IntArray((value.size - keep + extra + 3).floorDiv(Int.SIZE_BYTES))
            val cache = ByteArray(result.size * Int.SIZE_BYTES - (value.size - keep)).also {
                it.fill(BigSigned.NEGATIVE.signed.toByte())
            } + value.copyOfRange(keep, value.size)

            (result.lastIndex downTo 0).forEach {
                val num = cache.readIntAt(it * Int.SIZE_BYTES)
                result[it] = when {
                    num < 0 -> num.inv().swapEndian()
                    else -> num.swapEndian().inv()
                }
            }

            (result.lastIndex downTo 0).indexOfFirst {
                result[it] = ((result[it].toLong() and 0xffffffffL) + 1).toInt()
                result[it] != 0
            }
            return result
        }

        public fun stripLeadingZeros(value: ByteArray): IntArray {
            val keep = keep(value, BigSigned.POSITIVE)
            val result = IntArray((value.size - keep + 3).floorDiv(Int.SIZE_BYTES))
            val cache = ByteArray(
                result.size * Int.SIZE_BYTES - (
                        value.size - keep)
            ) + value.copyOfRange(keep, value.size)

            (result.lastIndex downTo 0).forEach {
                result[it] = cache.readIntAt(it * Int.SIZE_BYTES).swapEndian()
            }
            return result
        }

        /**
         * Tells how many rightmost bytes to keep, the inversion of how many leftmost
         * bytes that are zero from two's complements point of view, which should be truncated.
         * */
        private fun keep(value: ByteArray, sigNum: BigSigned): Int {
            val keep = value.indexOfFirst { it.toInt() != sigNum.signed }
            return when (keep) {
                -1 -> value.size
                else -> keep
            }
        }

        /**
         * Adjusts the sigNum to ZERO if the magnitude is empty.
         * */
        public fun sigNumZeroAdjust(
            mag: IntArray,
            sigNum: BigSigned
        ): BigSigned = when {
            mag.isEmpty() -> BigSigned.ZERO
            else -> sigNum
        }

        public fun <T : BigMath<*>> fromIntArray(
            value: IntArray,
            build: (IntArray, BigSigned) -> T
        ): T {
            if(value.isEmpty()) throw BigMathException("IntArray must not be of zero length.")
            val negative = value.first() < 0

            val sigNum = when (negative) {
                true -> BigSigned.NEGATIVE
                else -> BigSigned.POSITIVE
            }
            val mag = when (negative) {
                true -> makePositive(value)
                else -> stripLeadingZeros(value)
            }

            return build(mag, sigNumZeroAdjust(mag, sigNum))
        }

        private fun makePositive(value: IntArray): IntArray {
            val keep: Int = keep(value, BigSigned.NEGATIVE)
            val extra = (keep until value.size).indexOfFirst { value[it] != 0 }.let { if (it == -1) 1 else 0 }
            val result = IntArray(value.size - keep + extra)

            (keep until value.size).forEach { result[it - keep + extra] = value[it].inv() }

            (result.lastIndex downTo 0).indexOfFirst {
                result[it] = ((result[it].toLong() and 0xffffffffL) + 1).toInt()
                result[it] != 0
            }
            return result
        }

        public fun stripLeadingZeros(value: IntArray): IntArray {
            val keep = keep(value, BigSigned.POSITIVE)
            return if (keep == 0) value else value.copyOfRange(keep, value.size)
        }

        private fun keep(value: IntArray, sigNum: BigSigned): Int {
            val keep = value.indexOfFirst { it != sigNum.signed }
            return when (keep) {
                -1 -> value.size
                else -> keep
            }
        }

        public fun <T : BigMath<*>> fromLong(
            value: Long,
            build: (IntArray, BigSigned) -> T
        ): T {
            val negative = value < 0

            val sigNum = when (negative) {
                true -> BigSigned.NEGATIVE
                else -> BigSigned.POSITIVE
            }
            val tmp = intArrayOf((value ushr 32).toInt(), value.toInt())
            val mag = when (negative) {
                true -> makePositive(tmp)
                else -> stripLeadingZeros(tmp)
            }

            return build(mag, sigNumZeroAdjust(mag, sigNum))
        }
    }
}