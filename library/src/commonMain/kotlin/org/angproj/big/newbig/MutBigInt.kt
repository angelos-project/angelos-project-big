package org.angproj.big.newbig

import org.angproj.big.BigCompare
import org.angproj.big.BigMathException
import org.angproj.big.BigSigned
import kotlin.math.min

/**
 * A mutable BigInt class that represents a mutable big integer.
 * It contains a magnitude array, an offset, and a length.
 * The magnitude is stored as an array of integers, allowing for efficient arithmetic operations.
 *
 * This class provides methods for division, shifting, normalization, and comparison.
 */
public class MutBigInt {
    public var mag: IntArray
    public var off: Int
    public var len: Int

    /**
     * Creates a new mutable BigInt with an empty magnitude array.
     * The length is set to 0 and the offset is set to 0.
     */
    public constructor() {
        mag = IntArray(1)
        off = 0
        len = 0
    }

    /**
     * Creates a new mutable BigInt with the specified value.
     * The value is stored in a single-element magnitude array.
     *
     * @param value the integer value to store in the BigInt.
     */
    public constructor(value: Int): this() {
        mag = IntArray(1)
        len = 1
        mag[0] = value
    }

    /**
     * Creates a new mutable BigInt with the specified magnitude array.
     * The length is set to the size of the array and the offset is set to 0.
     *
     * @param value the integer array representing the magnitude of the BigInt.
     */
    public constructor(value: IntArray): this() {
        mag = value
        len = value.size
    }

    /**
     * Creates a new mutable BigInt with the specified magnitude array, offset, and length.
     *
     * @param value the integer array representing the magnitude of the BigInt.
     * @param offset the offset in the array where the magnitude starts.
     * @param length the length of the magnitude.
     */
    public constructor(mutBigInt: MutBigInt): this() {
        len = mutBigInt.len
        mag = mutBigInt.mag.copyOfRange(mutBigInt.off, mutBigInt.off + len)
    }

    /**
     * Checks if this mutable BigInt is null, which is defined as having no magnitude.
     * This is a singleton instance representing a null value.
     *
     * @return `true` if this mutable BigInt is the null object, `false` otherwise.
     */
    public fun isNull(): Boolean = nullObject === this

    public companion object {
        public const val KNUTH_POW2_THRESH_LEN: Long = 6 // Fix this
        public const val KNUTH_POW2_THRESH_ZEROS: Long = 3 // Fix this

        public val nullObject: MutBigInt by lazy { MutBigInt(intArrayOf()) }
    }
}

/**
 * Divides this mutable BigInt by another mutable BigInt using Knuth's division algorithm.
 * The quotient is stored in the provided `quotient` mutable BigInt.
 * If `needRemainder` is true, the remainder is returned; otherwise, a null object is returned.
 *
 * @param b the divisor mutable BigInt.
 * @param quotient the mutable BigInt to store the quotient.
 * @return the remainder as a mutable BigInt if `needRemainder` is true, otherwise a null object.
 */
public fun MutBigInt.divideKnuth(b: MutBigInt, quotient: MutBigInt): MutBigInt {
    return divideKnuth(b, quotient, true)
}

/**
 * Divides this mutable BigInt by another mutable BigInt using Knuth's division algorithm.
 * The quotient is stored in the provided `quotient` mutable BigInt.
 * If `needRemainder` is true, the remainder is returned; otherwise, a null object is returned.
 *
 * @param _b the divisor mutable BigInt.
 * @param quotient the mutable BigInt to store the quotient.
 * @param needRemainder whether to return the remainder or a null object.
 * @return the remainder as a mutable BigInt if `needRemainder` is true, otherwise a null object.
 */
public fun MutBigInt.divideKnuth(_b: MutBigInt, quotient: MutBigInt, needRemainder: Boolean): MutBigInt {
    var b: MutBigInt = _b
    require(b.len > 0) { throw BigMathException("Division by zero") }

    if (len == 0) {
        quotient.off = 0
        quotient.len = quotient.off
        return if (needRemainder) MutBigInt() else MutBigInt.nullObject
    }

    val cmp: BigCompare = compare(b)
    if (cmp.isLesser()) {
        quotient.off = 0
        quotient.len = quotient.off
        return if (needRemainder) MutBigInt(this) else MutBigInt.nullObject
    }

    if (cmp.isEqual()) {
        quotient.len = 1
        quotient.mag[0] = quotient.len
        quotient.off = 0
        return if (needRemainder) MutBigInt() else MutBigInt.nullObject
    }

    quotient.clear()
    // Special case one word divisor
    if (b.len == 1) {
        val r: Int = divideOneWord(b.mag[b.off], quotient)
        if (needRemainder) {
            if (r == 0) return MutBigInt()
            return MutBigInt(r)
        } else {
            return MutBigInt.nullObject
        }
    }

    // Cancel common powers of two if we're above the KNUTH_POW2_* thresholds
    if (len >= MutBigInt.KNUTH_POW2_THRESH_LEN) {
        val trailingZeroBits: Int = min(getLowestSetBit(), b.getLowestSetBit())
        if (trailingZeroBits >= MutBigInt.KNUTH_POW2_THRESH_ZEROS * 32) {
            val a: MutBigInt = MutBigInt(this)
            b = MutBigInt(b)
            a.rightShift(trailingZeroBits)
            b.rightShift(trailingZeroBits)
            val r: MutBigInt = a.divideKnuth(b, quotient)
            r.leftShift(trailingZeroBits)
            return r
        }
    }

    return divideMagnitude(b, quotient, needRemainder)
}

/**
 * Divides this BigInt by another BigInt and returns the quotient and remainder as a Pair.
 * The quotient is returned as a BigInt with the same sign as this BigInt if the divisor has the same sign,
 * otherwise it is returned with a negative sign.
 *
 * @param value the divisor BigInt.
 * @return a Pair containing the quotient and remainder as BigInts.
 */
public fun BigInt.divideAndRemainder(value:BigInt): Pair<BigInt, BigInt> {
    val q: MutBigInt = MutBigInt()
    val a: MutBigInt = MutBigInt(mag)
    val b: MutBigInt = MutBigInt(value.mag)
    val r: MutBigInt = a.divideKnuth(b, q)
    return Pair(
        q.toBigInteger(if (sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
        r.toBigInteger(sigNum)
    )
}

/**
 * Gets the magnitude array of this mutable BigInt.
 * If the offset is greater than 0 or the size of the magnitude array does not match the length,
 * it creates a new array with the correct size and copies the relevant elements.
 *
 * @return the magnitude array of this mutable BigInt.
 */
public fun MutBigInt.getMagnitudeArray(): IntArray {
    if (off > 0 || mag.size != len) {
        val tmp: IntArray = mag.copyOfRange(off, off + len)
        mag.fill(0)
        off = 0
        len = tmp.size
        mag = tmp
    }
    return mag
}

/**
 * Divides this mutable BigInt by another mutable BigInt and returns the quotient and remainder.
 * The quotient is stored in the provided `quotient` mutable BigInt.
 * If `needRemainder` is true, the remainder is returned; otherwise, a null object is returned.
 *
 * @param div the divisor mutable BigInt.
 * @param quotient the mutable BigInt to store the quotient.
 * @param needRemainder whether to return the remainder or a null object.
 * @return the remainder as a mutable BigInt if `needRemainder` is true, otherwise a null object.
 */
public fun MutBigInt.divideMagnitude(
    div: MutBigInt,
    quotient: MutBigInt,
    needRemainder: Boolean
): MutBigInt {
    // assert div.intLen > 1
    // D1 normalize the divisor
    val shift: Int = div.mag[div.off].countLeadingZeroBits()

    // Copy divisor value to protect divisor
    val dlen: Int = div.len
    val divisor: IntArray
    val rem: MutBigInt // Remainder starts as dividend with space for a leading zero
    if (shift > 0) {
        divisor = IntArray(dlen)
        div.primitiveLeftShift(shift, divisor, 0)
        if (mag[off].countLeadingZeroBits() >= shift) {
            val remarr = IntArray(len + 1)
            rem = MutBigInt(remarr)
            rem.len = len
            rem.off = 1
            this.primitiveLeftShift(shift, remarr, 1)
        } else {
            val remarr = IntArray(len + 2)
            rem = MutBigInt(remarr)
            rem.len = len + 1
            rem.off = 1
            var rFrom: Int = off
            var c: Int = 0
            val n2: Int = 32 - shift
            var i: Int = 1
            while (i < len + 1) {
                val b: Int = c
                c = mag[rFrom]
                remarr[i] = (b shl shift) or (c ushr n2)
                i++
                rFrom++
            }
            remarr[len + 1] = c shl shift
        }
    } else {
        //divisor = Arrays.copyOfRange(div.mag, div.off, div.off + div.len)
        divisor = div.mag.copyOfRange(div.off, div.off + div.len)

        rem = MutBigInt(IntArray(len + 1))
        //java.lang.System.arraycopy(mag, off, rem.mag, 1, len)
        mag.copyInto(rem.mag, off, 1, len)
        rem.len = len
        rem.off = 1
    }

    val nlen: Int = rem.len

    // Set the quotient size
    val limit: Int = nlen - dlen + 1
    if (quotient.mag.size < limit) {
        quotient.mag = IntArray(limit)
        quotient.off = 0
    }
    quotient.len = limit
    val q: IntArray = quotient.mag

    // Insert leading 0 in rem
    rem.off = 0
    rem.mag[0] = 0
    rem.len++

    val dh: Int = divisor[0]
    val dhLong: Long = dh.toLong() and LONG_MASK
    val dl: Int = divisor[1]

    // D2 Initialize j
    for (j: Int in 0..<limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat: Int
        var qrem: Int
        var skipCorrection: Boolean = false
        val nh: Int = rem.mag[j + rem.off]
        val nh2: Int = nh + -0x80000000
        val nm: Int = rem.mag[j + 1 + rem.off]

        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk: Long = ((nh.toLong()) shl 32) or (nm.toLong() and LONG_MASK)

            if (nChunk >= 0) {
                qhat = (nChunk / dhLong).toInt()
                qrem = (nChunk - (qhat * dhLong)).toInt()
            } else {
                val tmp: Long = divWord(nChunk, dh)
                qhat = (tmp and LONG_MASK).toInt()
                qrem = (tmp ushr 32).toInt()
            }
        }

        if (qhat == 0) continue

        if (!skipCorrection) { // Correct qhat
            val nl: Long = (rem.mag[j + 2 + rem.off].toLong() and LONG_MASK)
            var rs: Long = ((qrem.toLong() and LONG_MASK) shl 32) or nl
            var estProduct: Long = (dl.toLong() and LONG_MASK) * (qhat.toLong() and LONG_MASK)

            if (unsignedLongCompare(estProduct, rs)) {
                qhat--
                qrem = ((qrem.toLong() and LONG_MASK) + dhLong).toInt()
                if ((qrem.toLong() and LONG_MASK) >= dhLong) {
                    estProduct -= (dl.toLong() and LONG_MASK)
                    rs = ((qrem.toLong() and LONG_MASK) shl 32) or nl
                    if (unsignedLongCompare(estProduct, rs)) qhat--
                }
            }
        }

        // D4 Multiply and subtract
        rem.mag[j + rem.off] = 0
        val borrow: Int = mulsub(rem.mag, divisor, qhat, dlen, j + rem.off)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            divadd(divisor, rem.mag, j + 1 + rem.off)
            qhat--
        }

        // Store the quotient digit
        q[j] = qhat
    } // D7 loop on j

    // D3 Calculate qhat
    // estimate qhat
    var qhat: Int
    var qrem: Int
    var skipCorrection: Boolean = false
    val nh: Int = rem.mag[limit - 1 + rem.off]
    val nh2: Int = nh + -0x80000000
    val nm: Int = rem.mag[limit + rem.off]

    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk: Long = ((nh.toLong()) shl 32) or (nm.toLong() and LONG_MASK)

        if (nChunk >= 0) {
            qhat = (nChunk / dhLong).toInt()
            qrem = (nChunk - (qhat * dhLong)).toInt()
        } else {
            val tmp: Long = divWord(nChunk, dh)
            qhat = (tmp and LONG_MASK).toInt()
            qrem = (tmp ushr 32).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) { // Correct qhat
            val nl: Long = rem.mag[limit + 1 + rem.off].toLong() and LONG_MASK
            var rs: Long = ((qrem.toLong() and LONG_MASK) shl 32) or nl
            var estProduct: Long = (dl.toLong() and LONG_MASK) * (qhat.toLong() and LONG_MASK)

            if (unsignedLongCompare(estProduct, rs)) {
                qhat--
                qrem = ((qrem.toLong() and LONG_MASK) + dhLong).toInt()
                if ((qrem.toLong() and LONG_MASK) >= dhLong) {
                    estProduct -= (dl.toLong() and LONG_MASK)
                    rs = ((qrem.toLong() and LONG_MASK) shl 32) or nl
                    if (unsignedLongCompare(estProduct, rs)) qhat--
                }
            }
        }


        // D4 Multiply and subtract
        val borrow: Int
        rem.mag[limit - 1 + rem.off] = 0
        if (needRemainder) borrow = mulsub(rem.mag, divisor, qhat, dlen, limit - 1 + rem.off)
        else borrow = mulsubBorrow(rem.mag, divisor, qhat, dlen, limit - 1 + rem.off)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            if (needRemainder) divadd(divisor, rem.mag, limit - 1 + 1 + rem.off)
            qhat--
        }

        // Store the quotient digit
        q[(limit - 1)] = qhat
    }


    if (needRemainder) {
        // D8 Unnormalize
        if (shift > 0) rem.rightShift(shift)
        rem.normalize()
    }
    quotient.normalize()
    return if (needRemainder) rem else MutBigInt.nullObject
}

/**
 * Divides this mutable BigInt by a single word divisor and stores the quotient in the provided `quotient` mutable BigInt.
 * Returns the remainder as an Int.
 *
 * @param divisor the single word divisor as an Int.
 * @param quotient the mutable BigInt to store the quotient.
 * @return the remainder as an Int.
 */
public fun MutBigInt.divideOneWord(divisor: Int, quotient: MutBigInt): Int {
    val divisorLong: Long = divisor.toLong() and LONG_MASK

    // Special case of one word dividend
    if (len == 1) {
        val dividendValue: Long = mag[off].toLong() and LONG_MASK
        val q: Int = (dividendValue / divisorLong).toInt()
        val r: Int = (dividendValue - q * divisorLong).toInt()
        quotient.mag[0] = q
        quotient.len = if (q == 0) 0 else 1
        quotient.off = 0
        return r
    }

    if (quotient.mag.size < len) quotient.mag = IntArray(len)
    quotient.off = 0
    quotient.len = len

    // Normalize the divisor
    val shift: Int = divisor.countLeadingZeroBits()

    var rem: Int = mag[off]
    var remLong: Long = rem.toLong() and LONG_MASK
    if (remLong < divisorLong) {
        quotient.mag[0] = 0
    } else {
        quotient.mag[0] = (remLong / divisorLong).toInt()
        rem = (remLong - (quotient.mag[0] * divisorLong)).toInt()
        remLong = rem.toLong() and LONG_MASK
    }
    var xlen: Int = len
    while (--xlen > 0) {
        val dividendEstimate: Long = (remLong shl 32) or
                (mag[off + len - xlen].toLong() and LONG_MASK)
        val q: Int
        if (dividendEstimate >= 0) {
            q = (dividendEstimate / divisorLong).toInt()
            rem = (dividendEstimate - q * divisorLong).toInt()
        } else {
            val tmp: Long = divWord(dividendEstimate, divisor)
            q = (tmp and LONG_MASK).toInt()
            rem = (tmp ushr 32).toInt()
        }
        quotient.mag[len - xlen] = q
        remLong = rem.toLong() and LONG_MASK
    }

    quotient.normalize()
    // Unnormalize
    return if (shift > 0) rem % divisor
    else rem
}

/**
 * Divides a long value by a word (32-bit integer) and returns the quotient and remainder as a Long.
 * The result is returned as a Long where the lower 32 bits are the remainder and the upper 32 bits are the quotient.
 *
 * @param n the long value to divide.
 * @param d the divisor as a 32-bit integer.
 * @return a Long where the lower 32 bits are the remainder and the upper 32 bits are the quotient.
 */
private fun MutBigInt.divWord(n: Long, d: Int): Long {
    val dLong: Long = d.toLong() and LONG_MASK
    var r: Long
    var q: Long

    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return (r shl 32) or (q and LONG_MASK)
    }

    // Approximate the quotient and remainder
    q = (n ushr 1) / (dLong ushr 1)
    r = n - q * dLong

    // Correct the approximation
    while (r < 0) {
        r += dLong
        q--
    }
    while (r >= dLong) {
        r -= dLong
        q++
    }
    // n - q*dlong == r && 0 <= r <dLong, hence we're done.
    return (r shl 32) or (q and LONG_MASK)
}

/**
 * Right shifts this mutable BigInt by the specified number of bits.
 * The shift is done in-place, modifying the current instance.
 *
 * @param n the number of bits to shift right.
 */
public fun MutBigInt.rightShift(n: Int) {
    if (len == 0) return
    val nInts = n ushr 5
    val nBits = n and 0x1F
    len -= nInts
    if (nBits == 0) return
    val bitsInHighWord: Int = bitLengthForInt(mag[off])
    if (nBits >= bitsInHighWord) {
        primitiveLeftShift(32 - nBits)
        len--
    } else {
        primitiveRightShift(nBits)
    }
}

public fun MutBigInt.primitiveRightShift(n: Int) {
    primitiveRightShift(n, mag, off)
}

/**
 * Performs a primitive right shift on the magnitude array of this mutable BigInt.
 * The result is stored in the specified result array starting from the specified offset.
 *
 * @param n the number of bits to shift right.
 * @param result the array to store the result.
 * @param resFrom the offset in the result array where the result should be stored.
 */
private fun MutBigInt.primitiveRightShift(n: Int, result: IntArray, resFrom: Int) {
    val value: IntArray = mag
    val n2: Int = 32 - n

    var b: Int = value[off]
    result[resFrom] = b ushr n
    for (i in 1..<len) {
        val c: Int = b
        b = value[off + i]
        result[resFrom + i] = (c shl n2) or (b ushr n)
    }
}

/**
 * Left shifts this mutable BigInt by the specified number of bits.
 * The shift is done in-place, modifying the current instance.
 *
 * @param n the number of bits to shift left.
 */
public fun MutBigInt.leftShift(n: Int) {
    if (len == 0) return

    val nInts: Int = n ushr 5
    val nBits: Int = n and 0x1F
    val leadingZeros: Int = mag[off].countLeadingZeroBits()

    // If shift can be done without moving words, do so
    if (n <= leadingZeros) {
        primitiveLeftShift(nBits)
        return
    }

    var newLen: Int = len + nInts
    if (nBits > leadingZeros) newLen++

    val result: IntArray
    val newOffset: Int
    if (mag.size < newLen) { // The array must grow
        result = IntArray(newLen)
        newOffset = 0
    } else {
        result = mag
        newOffset = if (mag.size - off >= newLen) off else 0
    }

    var trailingZerosPos: Int = newOffset + len
    if (nBits != 0) {
        // Do primitive shift directly for speed
        if (nBits <= leadingZeros) {
            primitiveLeftShift(nBits, result, newOffset) // newOffset <= offset
        } else {
            val lastInt: Int = mag[off + len - 1]
            primitiveRightShift(32 - nBits, result, newOffset) // newOffset <= offset
            result[trailingZerosPos++] = lastInt shl nBits
        }
    } else if (!result.contentEquals(mag) || newOffset != off) {
        //java.lang.System.arraycopy(mag, off, result, newOffset, len)
        mag.copyInto(result, off, newOffset, len)
    }

    // Add trailing zeros
    if (result.contentEquals(mag)) result.fill(trailingZerosPos, newOffset + newLen, 0)
    //Arrays.fill(result, trailingZerosPos, newOffset + newLen, 0)

    mag = result
    len = newLen
    off = newOffset
}

public fun MutBigInt.primitiveLeftShift(n: Int) {
    primitiveLeftShift(n, mag, off)
}

/**
 * Performs a primitive left shift on the magnitude array of this mutable BigInt.
 * The result is stored in the specified result array starting from the specified offset.
 *
 * @param n the number of bits to shift left.
 * @param result the array to store the result.
 * @param resFrom the offset in the result array where the result should be stored.
 */
public fun MutBigInt.primitiveLeftShift(n: Int, result: IntArray, resFrom: Int) {
    val value: IntArray = mag
    val n2: Int = 32 - n
    val m: Int = len - 1
    var b: Int = value[off]
    for (i in 0..<m) {
        val c: Int = value[off + i + 1]
        result[resFrom + i] = (b shl n) or (c ushr n2)
        b = c
    }
    result[resFrom + m] = b shl n
}

/**
 * Clears the mutable BigInt by setting its length to 0 and offset to 0.
 * All elements in the magnitude array are set to 0.
 */
public fun MutBigInt.clear() {
    len = 0
    off = len
    var index: Int = 0
    val n: Int = mag.size
    while (index < n) {
        mag[index] = 0
        index++
    }
}

/**
 * Compares this mutable BigInt with another mutable BigInt.
 * The comparison is done as an unsigned integer comparison.
 *
 * @param b the mutable BigInt to compare with.
 * @return `BigCompare.LESSER` if this is less than `b`, `BigCompare.GREATER` if this is greater than `b`,
 *         or `BigCompare.EQUAL` if they are equal.
 */
public fun MutBigInt.compare(b: MutBigInt): BigCompare {
    val blen: Int = b.len
    if (len < blen) return BigCompare.LESSER
    if (len > blen) return BigCompare.GREATER

    // Add Integer.MIN_VALUE to make the comparison act as unsigned integer
    // comparison.
    val bval: IntArray = b.mag
    var i: Int = off
    var j: Int = b.off
    while (i < len + off) {
        val b1: Int = mag[i] + -0x80000000
        val b2: Int = bval[j] + -0x80000000
        if (b1 < b2) return BigCompare.LESSER
        if (b1 > b2) return BigCompare.GREATER
        i++
        j++
    }
    return BigCompare.EQUAL
}

/**
 * Gets the position of the lowest set bit in this mutable BigInt.
 * The position is counted from the least significant bit, starting at 0.
 * If there are no set bits, -1 is returned.
 *
 * @return the position of the lowest set bit, or -1 if there are no set bits.
 */
public fun MutBigInt.getLowestSetBit(): Int {
    if (len == 0) return -1
    val b: Int
    var j: Int = len - 1
    while ((j > 0) && (mag[j + off] == 0)) { j-- }
    b = mag[j + off]
    if (b == 0) return -1
    return ((len - 1 - j) shl 5) + b.countTrailingZeroBits()
}

/**
 * Normalizes this mutable BigInt by removing leading zeros.
 * If the length becomes zero, the offset is set to 0.
 * This method modifies the current instance in-place.
 */
public fun MutBigInt.normalize() {
    if (len == 0) {
        off = 0
        return
    }

    var index: Int = off
    if (mag[index] != 0) return

    val indexBound: Int = index + len
    do {
        index++
    } while (index < indexBound && mag[index] == 0)

    val numZeros: Int = index - off
    len -= numZeros
    off = (if (len == 0) 0 else off + numZeros)
}

/**
 * Converts this mutable BigInt to an immutable BigInt.
 * If the length is zero or the sign is zero, it returns a zero BigInt.
 *
 * @param sigNum the sign of the resulting BigInt.
 * @return a new BigInt representing this mutable BigInt.
 */
public fun MutBigInt.toBigInteger(sigNum: BigSigned): BigInt {
    if (len == 0 || sigNum.isZero()) return BigInt.zero
    return internalOf(getMagnitudeArray(), sigNum)
}

/**
 * Multiplies the elements of `a` by `x` and subtracts the result from the elements of `q`.
 * The result is stored in `q`, and the function returns any carry that results from the subtraction.
 *
 * @param q the array to subtract from.
 * @param a the array containing the multiplicands.
 * @param x the multiplier.
 * @param len the length of the arrays.
 * @param _offset the offset in `q` where the subtraction starts.
 * @return the carry resulting from the subtraction.
 */
private fun MutBigInt.mulsub(q: IntArray, a: IntArray, x: Int, len: Int, _offset: Int): Int {
    var offset = _offset
    val xLong = x.toLong() and LONG_MASK
    var carry: Long = 0
    offset += len

    for (j in len - 1 downTo 0) {
        val product = (a[j].toLong() and LONG_MASK) * xLong + carry
        val difference = q[offset] - product
        q[offset--] = difference.toInt()
        carry = ((product ushr 32)
                + (if ((difference and LONG_MASK) >
            (((product.toInt().inv()).toLong() and LONG_MASK))
        ) 1 else 0))
    }
    return carry.toInt()
}

/**
 * Adds the elements of `a` to the elements of `result` starting from the specified offset.
 * The addition is done in-place, modifying the `result` array.
 *
 * @param a the array containing the values to add.
 * @param result the array to which the values are added.
 * @param offset the offset in `result` where the addition starts.
 * @return any carry that results from the addition.
 */
private fun MutBigInt.divadd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0

    for (j in a.indices.reversed()) {
        val sum = (a[j].toLong() and LONG_MASK) +
                (result[j + offset].toLong() and LONG_MASK) + carry
        result[j + offset] = sum.toInt()
        carry = sum ushr 32
    }
    return carry.toInt()
}

/**
 * Subtracts the product of `x` and the elements of `a` from the elements of `q`.
 * The result is stored in `q`, and the function returns any carry that results from the subtraction.
 *
 * @param q the array to subtract from.
 * @param a the array containing the multiplicands.
 * @param x the multiplier.
 * @param len the length of the arrays.
 * @param _offset the offset in `q` where the subtraction starts.
 * @return the carry resulting from the subtraction.
 */
private fun MutBigInt.mulsubBorrow(q: IntArray, a: IntArray, x: Int, len: Int, _offset: Int): Int {
    var offset = _offset
    val xLong = x.toLong() and LONG_MASK
    var carry: Long = 0
    offset += len
    for (j in len - 1 downTo 0) {
        val product = (a[j].toLong() and LONG_MASK) * xLong + carry
        val difference = q[offset--] - product
        carry = ((product ushr 32)
                + (if ((difference and LONG_MASK) >
            (((product.toInt().inv()).toLong() and LONG_MASK))
        ) 1 else 0))
    }
    return carry.toInt()
}

/**
 * Compares two unsigned long values.
 * This function treats the long values as unsigned integers for comparison purposes.
 *
 * @param one the first long value to compare.
 * @param two the second long value to compare.
 * @return `true` if `one` is greater than `two` when treated as unsigned integers, `false` otherwise.
 */
private fun MutBigInt.unsignedLongCompare(one: Long, two: Long): Boolean {
    return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE)
}