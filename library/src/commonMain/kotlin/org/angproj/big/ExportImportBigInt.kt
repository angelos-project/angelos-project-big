package org.angproj.big

import org.angproj.sec.util.TypeSize
import org.angproj.sec.util.ensure


public fun IntArray.valueOf(): BigInt = ExportImportBigInt.valueOf(this)


public object ExportImportBigInt {

    private inline fun <reified R: Any> stripKeep(data: IntArray): Int {
        val vlen = data.size
        var keep = 0
        while (keep < vlen && data[keep] == 0) { keep++ }
        return keep
    }

    private fun stripLeadingZeroInts(data: IntArray): IntArray {
        return data.copyOfRange(stripKeep<Unit>(data), data.size)
    }

    public fun trustedStripLeadingZeroInts(data: IntArray): IntArray {
        val keep = stripKeep<Unit>(data)
        return if (keep == 0) data else data.copyOfRange(keep, data.size)
    }

    private inline fun <reified R: Any> positiveKeep(data: IntArray): Int {
        val vlen = data.size
        var keep = 0
        while (keep < vlen && data[keep] == -1) { keep++ }
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
        ensure(data.isNotEmpty()) { BigMathException("Zero length magnitude") }
        return when (data[0] < 0) {
            true -> BigInt(makePositive(data), BigSigned.NEGATIVE)
            else -> {
                val mag = trustedStripLeadingZeroInts(data)
                BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
            }
        }
    }

    public fun internalOf(magnitude: IntArray, sigNum: BigSigned): BigInt {
        return BigInt(magnitude, if (magnitude.isEmpty()) BigSigned.ZERO else sigNum)
    }

    private fun internalOf(value: Long): BigInt {
        var newValue = value
        val newSig = when(newValue < 0) {
            true -> {
                newValue = -newValue
                BigSigned.NEGATIVE
            }
            else -> BigSigned.POSITIVE
        }

        val newMag = when (val highWord = (newValue ushr TypeSize.intBits).toInt()) {
            0 -> IntArray(1).also { it[0] = newValue.toInt() }
            else -> IntArray(2).also {
                it[0] = highWord
                it[1] = newValue.toInt()
            }
        }
        return BigInt(newMag, newSig)
    }

    public fun externalOf(sigNum: BigSigned, magnitude: IntArray): BigInt {
        val mag: IntArray = stripLeadingZeroInts(magnitude)
        val newSigNum = when {
            mag.isEmpty() -> BigSigned.ZERO
            else -> {
                ensure(!sigNum.isZero()) { BigMathException("Signum magnitude mismatch") }
                sigNum
            }
        }
        return BigInt(mag, newSigNum)
    }

    public fun valueOf(value: IntArray): BigInt = when {
        value[0] > 0 -> internalOf(value, BigSigned.POSITIVE)
        else -> internalOf(value)
    }

    public fun valueOf(value: Long): BigInt = when {
        value == 0L -> BigInt.zero
        else -> internalOf(value)
    }

    public fun intValue(x: IntArray, xSig: BigSigned): Int = x.intGetComp(
        0, xSig, x.firstNonzero())

    public fun longValue(x: IntArray, xSig: BigSigned): Long {
        val firstNonZero = x.firstNonzero()
        var result: Long = 0
        for (i in 1 downTo 0) result = (result shl TypeSize.intBits) + (x.intGetComp(i, xSig, firstNonZero).longMask())
        return result
    }

    //Remove completely if not used
    //public fun asComplemented(big: BigInt): IntArray = asComplemented(big.mag, big.sigNum)

    /*public fun asComplemented(mag: IntArray, sigNum: BigSigned): IntArray {
        val firstNonZero = LoadAndSaveBigInt.firstNonZeroIntNum(mag)
        return IntArray(mag.size) { mag.intGetComp(it, sigNum, firstNonZero) }
    }*/
}