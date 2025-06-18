package org.angproj.big.newbig

import org.angproj.big.BigInt
import org.angproj.big.BigMathException
import org.angproj.big.BigSigned


public fun Int.longMask(): Long = this.toLong() and 0xFFFFFFFFL

public fun Long.intMask(): Int = (this and 0xFFFFFFFFL).toInt()

public fun Long.lowerHalf(): Long = this and 0xFFFFFFFFL

public fun Long.upUnsigned(): Long = this ushr 32

public fun IntArray.longSet(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }

public fun IntArray.intSet(index: Int, value: Int) { this[lastIndex - index] = value }

public fun IntArray.intGet(index: Int): Int = this[lastIndex - index]

public fun IntArray.firstNonzero(): Int = LoadAndSaveBigInt.firstNonZeroIntNum(this)

public fun IntArray.intLength(sigNum: BigSigned): Int = LoadAndSaveBigInt.intLength(this, sigNum)

public fun IntArray.intGetComp(
    index: Int, sigNum: BigSigned, firstNonZero: Int
): Int = LoadAndSaveBigInt.getIntNew(index, this,  sigNum, firstNonZero)

public fun IntArray.intGetCompUnrev(
    index: Int, sigNum: BigSigned, firstNonZero: Int
): Int = LoadAndSaveBigInt.getIntNewUnrev(index, this,  sigNum, firstNonZero)

public fun IntArray.rev(index: Int): Int = this.lastIndex - index

public fun Int.rev(): Int = 32 - this

public fun BigInt.bitLength(): Int = LoadAndSaveBigInt.bitLength(mag, sigNum)


public object LoadAndSaveBigInt {

    public fun bitLengthForInt(n: Int): Int = 32 - n.countLeadingZeroBits()

    public fun intLength(mag: IntArray, sigNum: BigSigned): Int = (bitLength(mag, sigNum) ushr 5) + 1

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

    private fun signInt(sigNum: BigSigned): Int = if (sigNum.isNegative()) -1 else 0

    public fun firstNonZeroIntNum(mag: IntArray): Int {
        val mlen: Int = mag.size
        var i: Int = mlen - 1
        while (i >= 0 && mag[i] == 0) { i-- }
        return mlen - i - 1
    }

    private fun getInt(n: Int, mag: IntArray, sigNum: BigSigned): Int {
        if (n < 0) return 0
        if (n >= mag.size) return signInt(sigNum)

        val magInt: Int = mag[mag.lastIndex - n]

        return (if (sigNum.isNonNegative()) magInt else (if (n <= firstNonZeroIntNum(mag)) -magInt else magInt.inv()))
    }

    private fun toByteArray(mag: IntArray, sigNum: BigSigned): ByteArray {
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

    public fun getIntNew(n: Int, mag: IntArray, sigNum: BigSigned, firstNonZero: Int): Int {
        if (n < 0) return 0
        if (n >= mag.size) return signInt(sigNum)

        val magInt: Int = mag[mag.lastIndex - n]

        return if (sigNum.isNonNegative()) magInt else if (n <= firstNonZero) -magInt else magInt.inv()
    }

    public fun getIntNewUnrev(n: Int, mag: IntArray, sigNum: BigSigned, firstNonZero: Int): Int {
        if (n < 0) return 0
        if (n >= mag.size) return signInt(sigNum)

        val magInt: Int = mag[n]

        return if (sigNum.isNonNegative()) magInt else if (n <= firstNonZero) -magInt else magInt.inv()
    }

    public fun toByteArrayNew0(mag: IntArray, sigNum: BigSigned): ByteArray {
        val firstNonZero = firstNonZeroIntNum(mag)
        val byteLen: Int = bitLength(mag, sigNum) / 8 + 1
        val byteArray = ByteArray(byteLen)

        var i = byteLen - 1
        var bytesCopied = 4
        var nextInt = 0
        var intIndex = 0
        while (i >= 0) {
            if (bytesCopied == 4) {
                nextInt = getIntNew(intIndex++, mag, sigNum, firstNonZero)
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

    public fun toByteArrayNew(mag: IntArray, sigNum: BigSigned): ByteArray = toArbitraryByte(
        mag, sigNum,
        { idx, data -> this[idx] = data }
    ) { ByteArray(it) }

    public fun <E> toArbitraryByte(
        mag: IntArray, sigNum: BigSigned,
        writeOctet: E.(Int, Byte) -> Unit,
        factory: (Int) -> E
    ): E {
        val firstNonZero = firstNonZeroIntNum(mag)
        val byteLen: Int = bitLength(mag, sigNum) / 8 + 1
        val byteData = factory(byteLen)

        var i = byteLen - 1
        var bytesCopied = 4
        var nextInt = 0
        var intIndex = 0
        while (i >= 0) {
            if (bytesCopied == 4) {
                nextInt = getIntNew(intIndex++, mag, sigNum, firstNonZero)
                bytesCopied = 1
            } else {
                nextInt = nextInt ushr 8
                bytesCopied++
            }
            byteData.writeOctet(i, nextInt.toByte())
            i--
        }
        return byteData
    }

    private fun stripLeadingZeroBytes(bytes: ByteArray, from: Int, len: Int): IntArray {
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

    private fun makePositive(_b: Int, bytes: ByteArray, _from: Int, len: Int): IntArray {
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

    private fun internalOf(bytes: ByteArray, off: Int, len: Int) : BigInt {
        require(bytes.isNotEmpty()) { throw BigMathException("Zero length magnitude") }

        /**Objects.checkFromIndexSize(off, len, bytes.size)
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

    public fun internalOf0(bytes: ByteArray): BigInt = internalOf(bytes, 0, bytes.size)

    public fun internalOf1(bytes: ByteArray): BigInt {
        require(bytes.isNotEmpty()) { throw BigMathException("Zero length magnitude") }

        val b = bytes.first().toInt()
        return when(b < 0) {
            true -> BigInt(makePositive(bytes), BigSigned.NEGATIVE)
            else -> {
                val mag = stripLeadingZeroBytes(bytes)
                BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
            }
        }
    }

    private fun keep(data: ByteArray, sigNum: BigSigned, start: Int = 1): Int {
        var byte: Int = data[start-1].toInt()
        var keep = start
        while (byte == sigNum.signed && keep < data.size) { byte = data[keep++].toInt() }
        return keep
    }

    private fun stripLeadingZeroBytes(value: ByteArray): IntArray {
        var index = keep(value, BigSigned.POSITIVE)
        if (value[index-1].toInt() == 0) { return intArrayOf() }
        val result = IntArray(((value.size - index) shr 2) + 1)

        var first = value[index-1].toInt() and 0xff
        repeat((value.size - index).mod(4)) {
            first = first shl 8 or (value[index++].toInt() and 0xff)
        }
        result[0] = first

        repeat ((value.size - index).div(4)) {
            result[it + 1] = (value[index++].toInt() shl 24) or
                    ((value[index++].toInt() and 0xff) shl 16) or
                    ((value[index++].toInt() and 0xff) shl 8) or
                    (value[index++].toInt() and 0xff)
        }
        return result
    }

    private fun makePositive(value: ByteArray): IntArray {
        var index = keep(value, BigSigned.NEGATIVE)
        var first = -1 shl 8 or (value[index-1].toInt() and 0xff)
        repeat((value.size - index).mod(4)) {
            first = first shl 8 or (value[index++].toInt() and 0xff)
        }
        val realSize = index

        index = keep(value, BigSigned.POSITIVE, index)
        var second = value[index-1].toInt() and 0xff
        repeat((value.size - index).mod(4)) {
            second = second shl 8 or (value[index++].toInt() and 0xff)
        }

        val extra = if ((value.size - index or first or second) == 0) 1 else 0
        val result = IntArray(extra + 1 + (value.size - realSize).div(4))
        result[0] = if (extra == 0) first else -1
        var idx = result.size - (value.size - index).div(4)
        if (idx > 1) result[idx - 1] = second

        repeat ((value.size - index).div(4)) {
            result[idx++] = (value[index++].toInt() shl 24) or
                    ((value[index++].toInt() and 0xff) shl 16) or
                    ((value[index++].toInt() and 0xff) shl 8) or
                    (value[index++].toInt() and 0xff)
        }

        while (--idx >= 0 && result[idx] == 0) { Unit }
        result[idx] = -result[idx]
        while (--idx >= 0) { result[idx] = result[idx].inv() }
        return result
    }



    // Read
    public fun internalOf(bytes: ByteArray): BigInt = internalOf(bytes, bytes.size) { this[it] }

    public fun <E> internalOf0(data: E, size: Int, readOctet: E.(i: Int) -> Byte): BigInt {
        require(size > 0) { throw BigMathException("Zero length magnitude") }

        val b = data.readOctet(0).toInt()
        return when(b < 0) {
            true -> BigInt(makePositive0(data, size, readOctet), BigSigned.NEGATIVE)
            else -> {
                val mag = stripLeadingZeroBytes0(data, size, readOctet)
                BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
            }
        }
    }

    private fun <E> keepData0(data: E, size: Int, sigNum: BigSigned, start: Int = 1, readOctet: E.(i: Int) -> Byte): Int {
        var byte: Int = data.readOctet(start-1).toInt()
        var keep = start
        while (byte == sigNum.signed && keep < size) { byte = data.readOctet(keep++).toInt() }
        return keep
    }

    private fun <E> stripLeadingZeroBytes0(data: E, size: Int, readOctet: E.(i: Int) -> Byte): IntArray {
        var index = keepData0(data, size, BigSigned.POSITIVE, 1, readOctet)
        if (data.readOctet(index-1).toInt() == 0) { return intArrayOf() }
        val result = IntArray(((size - index) shr 2) + 1)

        var first = data.readOctet(index-1).toInt() and 0xff
        repeat((size - index).mod(4)) {
            first = first shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }
        result[0] = first

        repeat ((size - index).div(4)) {
            result[it + 1] = (data.readOctet(index++).toInt() shl 24) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 16) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 8) or
                    (data.readOctet(index++).toInt() and 0xff)
        }
        return result
    }

    private fun <E> makePositive0(data: E, size: Int, readOctet: E.(i: Int) -> Byte): IntArray {
        var index = keepData0(data, size, BigSigned.NEGATIVE, 1, readOctet)
        var first = -1 shl 8 or (data.readOctet(index-1).toInt() and 0xff)
        repeat((size - index).mod(4)) {
            first = first shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }
        val realSize = index

        index = keepData0(data, size, BigSigned.POSITIVE, index, readOctet)
        var second = data.readOctet(index-1).toInt() and 0xff
        repeat((size - index).mod(4)) {
            second = second shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }

        val extra = if ((size - index or first or second) == 0) 1 else 0
        val result = IntArray(extra + 1 + (size - realSize).div(4))
        result[0] = if (extra == 0) first else -1
        var idx = result.size - (size - index).div(4)
        if (idx > 1) result[idx - 1] = second

        repeat ((size - index).div(4)) {
            result[idx++] = (data.readOctet(index++).toInt() shl 24) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 16) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 8) or
                    (data.readOctet(index++).toInt() and 0xff)
        }

        while (--idx >= 0 && result[idx] == 0) { Unit }
        result[idx] = -result[idx]
        while (--idx >= 0) { result[idx] = result[idx].inv() }
        return result
    }




    private fun <E> stripLeadingZeroBytes(
        firstOctet: Int, data: E, size: Int, readOctet: E.(i: Int) -> Byte
    ): IntArray {
        var octet = firstOctet
        var index = 1

        while (octet == BigSigned.POSITIVE.signed && index < size) {
            octet = data.readOctet(index++).toInt() }
        if (octet == 0) { return intArrayOf() }

        val result = IntArray((size - index).div(4) + 1)

        var first = octet and 0xFF
        repeat((size - index).mod(4)) {
            first = first shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }
        result[0] = first

        repeat ((size - index).div(4)) {
            result[it + 1] = (data.readOctet(index++).toInt() shl 24) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 16) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 8) or
                    (data.readOctet(index++).toInt() and 0xff)
        }
        return result
    }

    private fun <E> makePositive(
        firstOctet: Int, data: E, size: Int, readOctet: E.(i: Int) -> Byte
    ): IntArray {
        var octet: Int = firstOctet
        var index = 1

        while (octet == BigSigned.NEGATIVE.signed && index < size) {
            octet = data.readOctet(index++).toInt() }

        var first = -1 shl 8 or (octet and 0xFF)
        repeat((size - index).mod(4)) {
            octet = data.readOctet(index++).toInt()
            first = first shl 8 or (octet and 0xFF)
        }
        val realSize = index

        while (octet == BigSigned.POSITIVE.signed && index < size) {
            octet = data.readOctet(index++).toInt() }

        var second = octet and 0xFF
        repeat((size - index).mod(4)) {
            second = second shl 8 or (data.readOctet(index++).toInt() and 0xff)
        }

        val extra = if ((size - index or first or second) == 0) 1 else 0
        val result = IntArray(extra + 1 + (size - realSize).div(4))
        result[0] = if (extra == 0) first else -1
        var idx = result.size - (size - index).div(4)
        if (idx > 1) result[idx - 1] = second

        repeat ((size - index).div(4)) {
            result[idx++] = (data.readOctet(index++).toInt() shl 24) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 16) or
                    ((data.readOctet(index++).toInt() and 0xff) shl 8) or
                    (data.readOctet(index++).toInt() and 0xff)
        }

        while (--idx >= 0 && result[idx] == 0) { Unit }
        result[idx] = -result[idx]
        while (--idx >= 0) { result[idx] = result[idx].inv() }
        return result
    }

    public fun <E> internalOf(data: E, size: Int, readOctet: E.(i: Int) -> Byte) : BigInt {
        require(size > 0) { throw BigMathException("Zero length magnitude") }

        val firstOctet = data.readOctet(0).toInt()
        return when(firstOctet < 0) {
            true -> BigInt(makePositive(firstOctet, data, size, readOctet), BigSigned.NEGATIVE)
            else -> {
                val mag = stripLeadingZeroBytes(firstOctet, data, size, readOctet)
                BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
            }
        }
    }
}