package org.angproj.big.newbig

import org.angproj.big.BigInt
import org.angproj.big.BigMathException
import kotlin.math.max


public object BitwiseArithm {

    public fun testBit(x: BigInt, n: Int): Boolean {
        if (n < 0) { throw BigMathException("Can not test an imaginary bit at a negative position.") }
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)
        return (LoadAndSaveBigInt.getIntNew(n ushr 5, x.mag, x.sigNum, xnz) and (1 shl (n and 31))) != 0
    }

    public fun setBit(x: BigInt, n: Int): BigInt {
        if (n < 0) { throw BigMathException("Can not set an imaginary bit at a negative position.") }
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)

        val intNum = n ushr 5
        val result = IntArray(max(LoadAndSaveBigInt.intLength(x.mag, x.sigNum), intNum + 2))
        for (i in result.indices) result[result.size - i - 1] = LoadAndSaveBigInt.getIntNew(i, x.mag, x.sigNum, xnz)
        result[result.size - intNum - 1] = result[result.size - intNum - 1] or (1 shl (n and 31))

        return ExportImportBigInt.valueOf(result)
    }

    public fun clearBit(x: BigInt, n: Int): BigInt {
        if (n < 0) { throw BigMathException("Can not clear an imaginary bit at a negative position.") }
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)

        val intNum = n ushr 5
        val result = IntArray(max(LoadAndSaveBigInt.intLength(x.mag, x.sigNum), ((n + 1) ushr 5) + 1))
        for (i in result.indices) result[result.size - i - 1] = LoadAndSaveBigInt.getIntNew(i, x.mag, x.sigNum, xnz)
        result[result.size - intNum - 1] = result[result.size - intNum - 1] and (1 shl (n and 31)).inv()

        return ExportImportBigInt.valueOf(result)
    }

    public fun flipBit(x: BigInt, n: Int): BigInt {
        if (n < 0) { throw BigMathException("Can not flip an imaginary bit at a negative position.") }
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)

        val intNum = n ushr 5
        val result = IntArray(max(LoadAndSaveBigInt.intLength(x.mag, x.sigNum), intNum + 2))
        for (i in result.indices) result[result.size - i - 1] = LoadAndSaveBigInt.getIntNew(i, x.mag, x.sigNum, xnz)
        result[result.size - intNum - 1] = result[result.size - intNum - 1] xor (1 shl (n and 31))

        return ExportImportBigInt.valueOf(result)
    }
}