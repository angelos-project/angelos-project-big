package org.angproj.big.newbig

import org.angproj.big.BigMathException
import org.angproj.big.BigSigned
import org.angproj.sec.util.TypeSize


public data class BigInt(
    public val mag: IntArray,
    public val sigNum: BigSigned
) {
    public fun isNull(): Boolean = nullObject === this

    public companion object {
        public val zero: BigInt = internalOf(byteArrayOf(0))
        public val minusOne: BigInt = internalOf(byteArrayOf(-1))

        public val nullObject: BigInt by lazy { BigInt(intArrayOf(), BigSigned.ZERO) }
    }
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

// Old BitCOunt from MathLogic WORKS
public fun bitCount(mag: IntArray, sigNum: BigSigned): Int {
    var count = mag.sumOf { it.countOneBits() }
    if (sigNum.isNegative()) {
        var magTrailingZeroCount = 0
        var j: Int = mag.lastIndex
        while (mag[j] == 0) {
            magTrailingZeroCount += TypeSize.intBits
            j--
        }
        magTrailingZeroCount += mag[j].countTrailingZeroBits()
        count += magTrailingZeroCount - 1
    }
    return count
}

// New implementation, questionable
public fun bitCount_(mag: IntArray, sigNum: BigSigned): Int {
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

public fun internalOf(magnitude: IntArray, sigNum: BigSigned): BigInt {
    return BigInt(magnitude, if (magnitude.isEmpty()) BigSigned.ZERO else sigNum)
}

private fun shiftLeft(mag: IntArray, n: Int): IntArray {
    val nInts = n ushr 5
    val nBits = n and 0x1f
    val magLen = mag.size
    val newMag: IntArray

    if (nBits == 0) {
        newMag = IntArray(magLen + nInts)
        //java.lang.System.arraycopy(mag, 0, newMag, 0, magLen)
        mag.copyInto(newMag, 0, 0, magLen)
    } else {
        var i = 0
        val nBits2 = 32 - nBits
        val highBits = mag[0] ushr nBits2
        newMag = when {
            highBits != 0 -> IntArray(magLen + nInts + 1).also { it[i++] = highBits }
            else -> IntArray(magLen + nInts)
        }
        val numIter = magLen - 1
        shiftLeftImplWorker(newMag, mag, i, nBits, numIter)
        newMag[numIter + i] = mag[numIter] shl nBits
    }
    return newMag
}

private fun shiftLeftImplWorker(newArr: IntArray, oldArr: IntArray, _newIdx: Int, shiftCount: Int, numIter: Int) {
    var newIdx = _newIdx
    val shiftCountRight = 32 - shiftCount
    var oldIdx = 0
    while (oldIdx < numIter) {
        newArr[newIdx++] = (oldArr[oldIdx++] shl shiftCount) or (oldArr[oldIdx] ushr shiftCountRight)
    }
}

private fun BigInt.shiftRightImpl(n: Int): BigInt {
    val nInts = n ushr 5
    val nBits = n and 0x1f
    val magLen: Int = mag.size
    var newMag: IntArray

    // Special case: entire contents shifted off the end
    //if (nInts >= magLen) return (if (sigNum.isNonNegative()) BigInt.zero else negConst.get(1))
    if (nInts >= magLen) return (if (sigNum.isNonNegative()) BigInt.zero else BigInt.minusOne)

    if (nBits == 0) {
        val newMagLen = magLen - nInts
        newMag = mag.copyOf(newMagLen)
    } else {
        var i = 0
        val highBits: Int = mag[0] ushr nBits
        newMag = when {
            highBits != 0 -> IntArray(magLen - nInts).also { it[i++] = highBits }
            else -> IntArray(magLen - nInts - 1)
        }
        val numIter = magLen - nInts - 1
        //java.util.Objects.checkFromToIndex(0, numIter + 1, mag.length)
        //java.util.Objects.checkFromToIndex(i, numIter + i, newMag.size)
        shiftRightImplWorker(newMag, mag, i, nBits, numIter)
    }

    if (sigNum.isNegative()) {
        // Find out whether any one-bits were shifted off the end.
        var onesLost = false
        var i = magLen - 1
        val j = magLen - nInts
        while (i >= j && !onesLost) {
            onesLost = (mag[i] != 0)
            i--
        }
        if (!onesLost && nBits != 0) onesLost = (mag[magLen - nInts - 1] shl (32 - nBits) != 0)

        if (onesLost) newMag = javaIncrement(newMag)
    }

    return BigInt(newMag, sigNum)
}

private fun shiftRightImplWorker(newArr: IntArray, oldArr: IntArray, newIdx: Int, shiftCount: Int, numIter: Int) {
    val shiftCountLeft = 32 - shiftCount
    var idx = numIter
    var nidx = if (newIdx == 0) numIter - 1 else numIter
    while (nidx >= newIdx) {
        newArr[nidx--] = (oldArr[idx--] ushr shiftCount) or (oldArr[idx] shl shiftCountLeft)
    }
}

private fun javaIncrement(_value: IntArray): IntArray {
    var value = _value
    var lastSum = 0
    var i = value.size - 1
    while (i >= 0 && lastSum == 0) {
        lastSum = (1.let { value[i] += it; value[i] })
        i--
    }
    if (lastSum == 0) {
        value = IntArray(value.size + 1)
        value[0] = 1
    }
    return value
}


// Some necessary stuff

public fun BigInt.negate(): BigInt = internalOf(mag, sigNum.negate())


public fun BigInt.abs(): BigInt = when (sigNum) {
    BigSigned.NEGATIVE -> negate()
    else -> this
}