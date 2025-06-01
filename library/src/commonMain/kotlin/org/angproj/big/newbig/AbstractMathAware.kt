package org.angproj.big.newbig

import org.angproj.aux.util.NullObject
import org.angproj.big.BigCompare
import org.angproj.big.BigMathException
import org.angproj.big.BigSigned
import kotlin.math.min


public abstract class AbstractMathAware {

public data class BigInt(
    public val mag: IntArray,
    public val sigNum: BigSigned
) {
    public companion object
}

public val LONG_MASK: Long = 0xffffffffL

private inline fun <reified R: Any> stripKeep(data: IntArray): Int {
    val vlen = data.size
    var keep = 0
    while (keep < vlen && data[keep] == 0) {
        keep++
    }
    return keep
}

public fun stripLeadingZeroInts(data: IntArray): IntArray {
    return data.copyOfRange(stripKeep<Unit>(data), data.size)
}

public fun trustedStripLeadingZeroInts(data: IntArray): IntArray {
    val keep = stripKeep<Unit>(data)
    return if (keep == 0) data else data.copyOfRange(keep, data.size)
}

private inline fun <reified R: Any> positiveKeep(data: IntArray): Int {
    val vlen = data.size
    var keep = 0
    while (keep < vlen && data[keep] == -1) {
        keep++
    }
    return keep
}

public fun makePositive(data: IntArray): IntArray {
    val keep = positiveKeep<Unit>(data)
    var j = keep
    while (j < data.size && data[j] == 0) { j++ }

    val extraInt = (if (j == data.size) 1 else 0)
    val result = IntArray(data.size - keep + extraInt)

    for (i in keep..<data.size) result[i - keep + extraInt] = data[i].inv()
    var i = result.size - 1
    while (++result[i] == 0) { i-- }

    return result
}

public fun internalOf(data: IntArray): BigInt {
    require(data.isNotEmpty()) { throw BigMathException("Zero length magnitude") }
    return when (data[0] < 0) {
        true -> BigInt(makePositive(data), BigSigned.NEGATIVE)
        else -> {
            val mag = trustedStripLeadingZeroInts(data)
            BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
        }
    }
}

public fun stripLeadingZeroBytes(bytes: ByteArray, from: Int, len: Int): IntArray {
    return stripLeadingZeroBytes(Int.MIN_VALUE, bytes, from, len)
}

private fun stripLeadingZeroBytes(_b: Int, bytes: ByteArray, _from: Int, len: Int): IntArray {
    var b = _b
    var from = _from
    if (len == 0) { return intArrayOf() }

    val to = from + len
    if (b < -128) { b = bytes[from].toInt() }

    ++from

    while (b == 0 && from < to) { b = bytes[from++].toInt() }
    if (b == 0) { return intArrayOf() }

    val res = IntArray(((to - from) shr 2) + 1)

    var d0 = b and 0xFF
    while (((to - from) and 0x3) != 0) { d0 = d0 shl 8 or (bytes[from++].toInt() and 0xFF) }
    res[0] = d0

    var i = 1
    while (from < to) {
        res[i++] = (bytes[from++].toInt() shl 24 or ((bytes[from++].toInt() and 0xFF) shl 16
                ) or ((bytes[from++].toInt() and 0xFF) shl 8) or (bytes[from++].toInt() and 0xFF))
    }
    return res
}

public fun makePositive(_b: Int, bytes: ByteArray, _from: Int, len: Int): IntArray {
    var b: Int = _b
    var from = _from
    val to = from + len

    ++from

    while (b == -1 && from < to) { b = bytes[from++].toInt() }

    var d0 = -1 shl 8 or (b and 0xFF)
    while (((to - from) and 0x3) != 0) {
        b = bytes[from++].toInt()
        d0 = d0 shl 8 or (b and 0xFF)
    }
    val f = from

    while (b == 0 && from < to) { b = bytes[from++].toInt() }

    var d = b and 0xFF
    while (((to - from) and 0x3) != 0) { d = d shl 8 or (bytes[from++].toInt() and 0xFF) }

    val c = if ((to - from or d0 or d) == 0) 1 else 0
    val res = IntArray(c + 1 + ((to - f) shr 2))
    res[0] = if (c == 0) d0 else -1
    var i = res.size - ((to - from) shr 2)
    if (i > 1) { res[i - 1] = d }

    while (from < to) {
        res[i++] = (bytes[from++].toInt() shl 24 or ((bytes[from++].toInt() and 0xFF) shl 16
                ) or ((bytes[from++].toInt() and 0xFF) shl 8) or (bytes[from++].toInt() and 0xFF))
    }

    while (--i >= 0 && res[i] == 0) { Unit }
    res[i] = -res[i]
    while (--i >= 0) { res[i] = res[i].inv() }
    return res
}

public fun internalOf(bytes: ByteArray, off: Int, len: Int) : BigInt {
    require(bytes.isNotEmpty()) { throw BigMathException("Zero length magnitude") }

    /*Objects.checkFromIndexSize(off, len, bytes.size)
    if (len == 0) {
        mag = ZERO.mag
        signum = ZERO.signum
        return
    }*/

    val b = bytes[off].toInt()
    return when(b < 0) {
        true -> BigInt(makePositive(b, bytes, off, len), BigSigned.NEGATIVE)
        else -> {
            val mag = stripLeadingZeroBytes(b, bytes, off, len)
            BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
        }
    }
}

public fun internalOf(bytes: ByteArray): BigInt = internalOf(bytes, 0, bytes.size)

public fun firstNonzeroIntNum(mag: IntArray): Int {
    val mlen: Int = mag.size
    var i: Int = mlen - 1
    while (i >= 0 && mag[i] == 0) { i-- }
    return mlen - i - 1
}

public fun bitLengthForInt(n: Int): Int = 32 - n.countLeadingZeroBits()

public fun bitLength(mag: IntArray, sigNum: BigSigned): Int = when (mag.isEmpty()) {
        true -> 0
        else -> {
        val magBitLength: Int = (mag.lastIndex shl 5) + bitLengthForInt(mag[0])
        when (sigNum.isNegative()) {
            true -> {
                // Check if magnitude is a power of two
                var pow2 = mag[0].countOneBits() == 1
                var i = 1
                while (i < mag.size && pow2) {
                    pow2 = mag[i] == 0
                    i++
                }

                when(pow2) {
                    true -> magBitLength -1
                    else -> magBitLength
                }
            }
            else -> magBitLength
        }
    }
}

public fun bitCount(mag: IntArray, sigNum: BigSigned): Int {
    var bc = 0 // offset by one to initialize
    // Count the bits in the magnitude
    mag.forEach { bc += it.countOneBits() }
    if (sigNum.isNegative()) {
        // Count the trailing zeros in the magnitude
        var magTrailingZeroCount = 0
        var j: Int = mag.lastIndex
        while (mag[j] == 0) {
            magTrailingZeroCount += 32
            j--
        }
        magTrailingZeroCount += mag[j].countLeadingZeroBits()
        bc += magTrailingZeroCount - 1
    }
    return bc
}

public fun intLength(mag: IntArray, sigNum: BigSigned): Int = (bitLength(mag, sigNum) ushr 5) + 1
public fun signBit(sigNum: BigSigned): Int = if (sigNum.isNegative()) 1 else 0
public fun signInt(sigNum: BigSigned): Int = if (sigNum.isNegative()) -1 else 0

private fun getInt(n: Int, mag: IntArray, sigNum: BigSigned): Int {
    if (n < 0) return 0
    if (n >= mag.size) return signInt(sigNum)

    val magInt: Int = mag[mag.lastIndex - n]

    return (if (sigNum.isNonNegative()) magInt else (if (n <= firstNonzeroIntNum(mag)) -magInt else magInt.inv()))
}

public fun toByteArray(mag: IntArray, sigNum: BigSigned): ByteArray {
    val byteLen: Int = bitLength(mag, sigNum) / 8 + 1
    val byteArray = ByteArray(byteLen)

    var i = byteLen - 1
    var bytesCopied = 4
    var nextInt = 0
    var intIndex = 0
    while (i >= 0) {
        if (bytesCopied == 4) {
            nextInt = getInt(intIndex++, mag, sigNum)
            bytesCopied = 1
        } else {
            nextInt = nextInt ushr 8
            bytesCopied++
        }
        byteArray[i] = nextInt.toByte()
        i--
    }
    return byteArray
}

public data class MutBigInt(
    public var mag: IntArray,
    public var off: Int,
    public var len: Int
) {

    public constructor(): this(intArrayOf(), 0, 0)

    public constructor(value: Int): this(intArrayOf(value), 0, 1)

    public constructor(value: IntArray): this(value, 0, value.size)

    public constructor(mutBigInt: MutBigInt): this(mutBigInt.mag, mutBigInt.off, mutBigInt.len)

    public companion object {
        public const val KNUTH_POW2_THRESH_LEN: Long = 6 // Fix this
        public const val KNUTH_POW2_THRESH_ZEROS: Long = 3 // Fix this
    }
}


public fun MutBigInt.isNull(): Boolean = NullObject.mutBigInt === this
private val nullMutBigInt = MutBigInt(NullObject.intArray, 0, 0)
public val NullObject.mutBigInt: MutBigInt
    get() = nullMutBigInt

public fun MutBigInt.clear() {
    len = 0
    off = len
    var index = 0
    val n: Int = mag.size
    while (index < n) {
        mag[index] = 0
        index++
    }
}

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
        val b2 = bval[j] + -0x80000000
        if (b1 < b2) return BigCompare.LESSER
        if (b1 > b2) return BigCompare.GREATER
        i++
        j++
    }
    return BigCompare.EQUAL
}

public fun MutBigInt.divideKnuth(b: MutBigInt, quotient: MutBigInt): MutBigInt {
    return divideKnuth(b, quotient, true)
}

public fun MutBigInt.divideKnuth(_b: MutBigInt, quotient: MutBigInt, needRemainder: Boolean): MutBigInt {
    var b = _b
    require(b.len > 0) { BigMathException("Division by zero") }

    if (len == 0) {
        quotient.off = 0
        quotient.len = quotient.off
        return if (needRemainder) MutBigInt() else NullObject.mutBigInt
    }

    val cmp = compare(b)
    if (cmp.isLesser()) {
        quotient.off = 0
        quotient.len = quotient.off
        return if (needRemainder) MutBigInt(this) else NullObject.mutBigInt
    }

    if (cmp.isEqual()) {
        quotient.len = 1
        quotient.mag[0] = quotient.len
        quotient.off = 0
        return if (needRemainder) MutBigInt() else NullObject.mutBigInt
    }

    quotient.clear()
    // Special case one word divisor
    if (b.len == 1) {
        val r: Int = divideOneWord(b.mag[b.off], quotient)
        if (needRemainder) {
            if (r == 0) return MutBigInt()
            return MutBigInt(r)
        } else {
            return NullObject.mutBigInt
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

public fun MutBigInt.rightShift(n: Int) {
    if (len == 0) return
    val nInts = n ushr 5
    val nBits = n and 0x1F
    this.len -= nInts
    if (nBits == 0) return
    val bitsInHighWord: Int = bitLengthForInt(mag[off])
    if (nBits >= bitsInHighWord) {
        this.primitiveLeftShift(32 - nBits)
        this.len--
    } else {
        primitiveRightShift(nBits)
    }
}

public fun MutBigInt.primitiveRightShift(n: Int) {
    primitiveRightShift(n, mag, off)
}

private fun MutBigInt.primitiveRightShift(n: Int, result: IntArray, resFrom: Int) {
    val value: IntArray = mag
    val n2 = 32 - n

    var b = value[off]
    result[resFrom] = b ushr n
    for (i in 1..<len) {
        val c = b
        b = value[off + i]
        result[resFrom + i] = (c shl n2) or (b ushr n)
    }
}

public fun MutBigInt.leftShift(n: Int) {
    if (len == 0) return

    val nInts = n ushr 5
    val nBits = n and 0x1F
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

public fun MutBigInt.primitiveLeftShift(n: Int, result: IntArray, resFrom: Int) {
    val value: IntArray = mag
    val n2 = 32 - n
    val m: Int = len - 1
    var b = value[off]
    for (i in 0..<m) {
        val c = value[off + i + 1]
        result[resFrom + i] = (b shl n) or (c ushr n2)
        b = c
    }
    result[resFrom + m] = b shl n
}

public fun MutBigInt.getLowestSetBit(): Int {
    if (len == 0) return -1
    val b: Int
    var j: Int = len - 1
    while ((j > 0) && (mag[j + off] == 0)) { j-- }
    b = mag[j + off]
    if (b == 0) return -1
    return ((len - 1 - j) shl 5) + b.countTrailingZeroBits()
}

public fun MutBigInt.divideOneWord0(divisor: Int, quotient: MutBigInt): Int {
    val divisorLong: Long = (divisor.toLong() and LONG_MASK)

    // Special case of one word dividend
    if (len == 1) {
        val dividendValue: Int = mag[off]
        val q: Int = Int.divideUnsigned(dividendValue, divisor)
        val r: Int = Int.remainderUnsigned(dividendValue, divisor)
        quotient.mag[0] = q
        quotient.len = if (q == 0) 0 else 1
        quotient.off = 0
        return r
    }

    if (quotient.mag.size < len) quotient.mag = IntArray(len)
    quotient.off = 0
    quotient.len = len

    var rem: Long = 0
    for (xlen in len downTo 1) {
        val dividendEstimate = (rem shl 32) or (mag[off + len - xlen].toLong() and LONG_MASK)
        val q: Int = Long.divideUnsigned(dividendEstimate, divisorLong).toInt()
        rem = Long.remainderUnsigned(dividendEstimate, divisorLong)
        quotient.mag[len - xlen] = q
    }

    quotient.normalize()
    return rem.toInt()
}

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

public fun Long.Companion.compare(x: Long, y: Long): Int {
    return if (x < y) -1 else (if (x == y) 0 else 1)
}

public fun Long.Companion.compareUnsigned(x: Long, y: Long): Int {
    return compare(x + MIN_VALUE, y + MIN_VALUE)
}

/**public fun Long.Companion.divideUnsigned0(dividend: Long, divisor: Long): Long {
    if (divisor < 0L) { // signed comparison
        return if ((compareUnsigned(dividend, divisor)) < 0) 0L else 1L
    }

    if (dividend > 0)  //  Both inputs non-negative
        return dividend / divisor
    else {
        return toUnsignedBigInteger(dividend).divide
        (toUnsignedBigInteger(divisor)).longValue()
    }
}*/

public fun Long.Companion.divideUnsigned(
    dividend: Long, divisor: Long
): Long = dividend.toULong().div(divisor.toULong()).toLong()


/**public fun Long.Companion.remainderUnsigned0(dividend: Long, divisor: Long): Long {
    if (dividend > 0 && divisor > 0) { // signed comparisons
        return dividend % divisor
    } else {
        return if (compareUnsigned(dividend, divisor) < 0)  // Avoid explicit check for 0 divisor
            dividend
        else toUnsignedBigInteger(dividend).remainder
        (toUnsignedBigInteger(divisor)).longValue()
    }
}*/

public fun Long.Companion.remainderUnsigned(
    dividend: Long, divisor: Long
): Long = dividend.toULong().rem(divisor.toULong()).toLong()

/**public fun Int.Companion.divideUnsigned(dividend: Int, divisor: Int): Int {
    return (toUnsignedLong(dividend) / toUnsignedLong(divisor)).toInt()
}

public fun Int.Companion.remainderUnsigned(dividend: Int, divisor: Int): Int {
    return (toUnsignedLong(dividend) % toUnsignedLong(divisor)).toInt()
}*/

public fun Int.Companion.divideUnsigned(
    dividend: Int, divisor: Int
): Int = dividend.toUInt().div(divisor.toUInt()).toInt()

public fun Int.Companion.remainderUnsigned(
    dividend: Int, divisor: Int
): Int = dividend.toUInt().rem(divisor.toUInt()).toInt()

public fun Int.Companion.toUnsignedLong(x: Int): Long {
    return (x.toLong()) and 0xffffffffL
}

public fun MutBigInt.divideMagnitude0(
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
            var c = 0
            val n2 = 32 - shift
            var i = 1
            while (i < len + 1) {
                val b = c
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
    val limit = nlen - dlen + 1
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

    val dh = divisor[0]
    val dhLong = dh.toLong() and LONG_MASK
    val dl = divisor[1]

    // D2 Initialize j
    for (j in 0..<limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        val nh: Int = rem.mag[j + rem.off]
        val nh2 = nh + -0x80000000
        val nm: Int = rem.mag[j + 1 + rem.off]

        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = ((nh.toLong()) shl 32) or (nm.toLong() and LONG_MASK)
            qhat = Long.divideUnsigned(nChunk, dhLong).toInt()
            qrem = Long.remainderUnsigned(nChunk, dhLong).toInt()
        }

        if (qhat == 0) continue

        if (!skipCorrection) { // Correct qhat
            val nl: Long = (rem.mag[j + 2 + rem.off].toLong() and LONG_MASK)
            var rs = ((qrem.toLong() and LONG_MASK) shl 32) or nl
            var estProduct = (dl.toLong() and LONG_MASK) * (qhat.toLong() and LONG_MASK)

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
    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    val nh: Int = rem.mag[limit - 1 + rem.off]
    val nh2 = nh + -0x80000000
    val nm: Int = rem.mag[limit + rem.off]

    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = ((nh.toLong()) shl 32) or (nm.toLong() and LONG_MASK)
        qhat = Long.divideUnsigned(nChunk, dhLong).toInt()
        qrem = Long.remainderUnsigned(nChunk, dhLong).toInt()
    }
    if (qhat != 0) {
        if (!skipCorrection) { // Correct qhat
            val nl: Long = rem.mag[limit + 1 + rem.off].toLong() and LONG_MASK
            var rs = ((qrem.toLong() and LONG_MASK) shl 32) or nl
            var estProduct = (dl.toLong() and LONG_MASK) * (qhat.toLong() and LONG_MASK)

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
    return if (needRemainder) rem else NullObject.mutBigInt
}

public fun mulsub(q: IntArray, a: IntArray, x: Int, len: Int, _offset: Int): Int {
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

public fun divadd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0

    for (j in a.indices.reversed()) {
        val sum = (a[j].toLong() and LONG_MASK) +
                (result[j + offset].toLong() and LONG_MASK) + carry
        result[j + offset] = sum.toInt()
        carry = sum ushr 32
    }
    return carry.toInt()
}

public fun mulsubBorrow(q: IntArray, a: IntArray, x: Int, len: Int, _offset: Int): Int {
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

public fun unsignedLongCompare(one: Long, two: Long): Boolean {
    return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE)
}

public fun MutBigInt.toBigInteger(sigNum: BigSigned): BigInt {
    if (len == 0 || sigNum.isZero()) return BigInt(intArrayOf(), BigSigned.ZERO)
    return BigInt(getMagnitudeArray(), sigNum)
}

public fun BigInt.divideAndRemainder(value:BigInt): Pair<BigInt, BigInt> {
    val q: MutBigInt = MutBigInt()
    val a: MutBigInt = MutBigInt(this.mag)
    val b: MutBigInt = MutBigInt(value.mag)
    val r: MutBigInt = a.divideKnuth(b, q)
    return Pair(
        q.toBigInteger(if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
        r.toBigInteger(this.sigNum)
    )
}

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
            var c = 0
            val n2 = 32 - shift
            var i = 1
            while (i < len + 1) {
                val b = c
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
    val limit = nlen - dlen + 1
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

    val dh = divisor[0]
    val dhLong = dh.toLong() and LONG_MASK
    val dl = divisor[1]

    // D2 Initialize j
    for (j in 0..<limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        val nh: Int = rem.mag[j + rem.off]
        val nh2 = nh + -0x80000000
        val nm: Int = rem.mag[j + 1 + rem.off]

        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = ((nh.toLong()) shl 32) or (nm.toLong() and LONG_MASK)

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
            var rs = ((qrem.toLong() and LONG_MASK) shl 32) or nl
            var estProduct = (dl.toLong() and LONG_MASK) * (qhat.toLong() and LONG_MASK)

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
    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    val nh: Int = rem.mag[limit - 1 + rem.off]
    val nh2 = nh + -0x80000000
    val nm: Int = rem.mag[limit + rem.off]

    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = ((nh.toLong()) shl 32) or (nm.toLong() and LONG_MASK)

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
            var rs = ((qrem.toLong() and LONG_MASK) shl 32) or nl
            var estProduct = (dl.toLong() and LONG_MASK) * (qhat.toLong() and LONG_MASK)

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
    return if (needRemainder) rem else NullObject.mutBigInt
}


public fun divWord(n: Long, d: Int): Long {
    val dLong = d.toLong() and LONG_MASK
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


public fun MutBigInt.divideOneWord(divisor: Int, quotient: MutBigInt): Int {
    val divisorLong = divisor.toLong() and LONG_MASK

    // Special case of one word dividend
    if (len == 1) {
        val dividendValue: Long = mag[off].toLong() and LONG_MASK
        val q = (dividendValue / divisorLong).toInt()
        val r = (dividendValue - q * divisorLong).toInt()
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
    var remLong = rem.toLong() and LONG_MASK
    if (remLong < divisorLong) {
        quotient.mag[0] = 0
    } else {
        quotient.mag[0] = (remLong / divisorLong).toInt()
        rem = (remLong - (quotient.mag[0] * divisorLong)).toInt()
        remLong = rem.toLong() and LONG_MASK
    }
    var xlen: Int = len
    while (--xlen > 0) {
        val dividendEstimate = (remLong shl 32) or
                (mag[off + len - xlen].toLong() and LONG_MASK)
        val q: Int
        if (dividendEstimate >= 0) {
            q = (dividendEstimate / divisorLong).toInt()
            rem = (dividendEstimate - q * divisorLong).toInt()
        } else {
            val tmp = divWord(dividendEstimate, divisor)
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

    public fun internalOf(magnitude: IntArray, sigNum: BigSigned): BigInt {
        return BigInt(magnitude, if (magnitude.isEmpty()) BigSigned.ZERO else sigNum)
    }

    private fun implMultiplyToLen(x: IntArray, xlen: Int, y: IntArray, ylen: Int, z: IntArray?): IntArray {
        var z = z
        val xstart = xlen - 1
        val ystart = ylen - 1

        if (z == null || z.size < (xlen + ylen)) z = IntArray(xlen + ylen)

        var carry: Long = 0
        var j = ystart
        var k = ystart + 1 + xstart
        while (j >= 0) {
            val product = (y[j].toLong() and LONG_MASK) *
                    (x[xstart].toLong() and LONG_MASK) + carry
            z[k] = product.toInt()
            carry = product ushr 32
            j--
            k--
        }
        z[xstart] = carry.toInt()

        for (i in xstart - 1 downTo 0) {
            carry = 0
            var j = ystart
            var k = ystart + 1 + i
            while (j >= 0) {
                val product = (y[j].toLong() and LONG_MASK) *
                        (x[i].toLong() and LONG_MASK) +
                        (z[k].toLong() and LONG_MASK) + carry
                z[k] = product.toInt()
                carry = product ushr 32
                j--
                k--
            }
            z[i] = carry.toInt()
        }
        return z
    }
}