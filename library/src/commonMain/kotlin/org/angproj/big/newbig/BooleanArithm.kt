package org.angproj.big.newbig

import org.angproj.big.BigInt
import kotlin.math.max


public object BooleanArithm {

    /*private fun innerLoop(x: BigInt, y: BigInt, op: (Int, Int) -> Int): IntArray {

    }*/

    public fun and(x: BigInt, y: BigInt): BigInt {
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)
        val ynz = LoadAndSaveBigInt.firstNonZeroIntNum(y.mag)
        val result = IntArray(max(
            LoadAndSaveBigInt.intLength(x.mag, x.sigNum),
            LoadAndSaveBigInt.intLength(y.mag, y.sigNum)
        ))
        for (i in result.indices) result[i] =
            (LoadAndSaveBigInt.getIntNew(result.size - i - 1, x.mag, x.sigNum, xnz)
                and LoadAndSaveBigInt.getIntNew(result.size - i - 1, y.mag, y.sigNum, ynz))

        return ExportImportBigInt.valueOf(result)
    }

    public fun or(x: BigInt, y: BigInt): BigInt {
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)
        val ynz = LoadAndSaveBigInt.firstNonZeroIntNum(y.mag)
        val result = IntArray(max(
            LoadAndSaveBigInt.intLength(x.mag, x.sigNum),
            LoadAndSaveBigInt.intLength(y.mag, y.sigNum)
        ))
        for (i in result.indices) result[i] =
            (LoadAndSaveBigInt.getIntNew(result.size - i - 1, x.mag, x.sigNum, xnz)
                    or LoadAndSaveBigInt.getIntNew(result.size - i - 1, y.mag, y.sigNum, ynz))

        return ExportImportBigInt.valueOf(result)
    }

    public fun xor(x: BigInt, y: BigInt): BigInt {
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)
        val ynz = LoadAndSaveBigInt.firstNonZeroIntNum(y.mag)
        val result = IntArray(max(
            LoadAndSaveBigInt.intLength(x.mag, x.sigNum),
            LoadAndSaveBigInt.intLength(y.mag, y.sigNum)
        ))
        for (i in result.indices) result[i] =
            (LoadAndSaveBigInt.getIntNew(result.size - i - 1, x.mag, x.sigNum, xnz)
                    xor LoadAndSaveBigInt.getIntNew(result.size - i - 1, y.mag, y.sigNum, ynz))

        return ExportImportBigInt.valueOf(result)
    }

    public fun not(x: BigInt): BigInt {
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)
        val result = IntArray(LoadAndSaveBigInt.intLength(x.mag, x.sigNum))
        for (i in result.indices) result[i] =
            LoadAndSaveBigInt.getIntNew(result.size - i - 1, x.mag, x.sigNum, xnz).inv()

        return ExportImportBigInt.valueOf(result)
    }

    public fun andNot(x: BigInt, y: BigInt): BigInt {
        val xnz = LoadAndSaveBigInt.firstNonZeroIntNum(x.mag)
        val ynz = LoadAndSaveBigInt.firstNonZeroIntNum(y.mag)
        val result = IntArray(max(
            LoadAndSaveBigInt.intLength(x.mag, x.sigNum),
            LoadAndSaveBigInt.intLength(y.mag, y.sigNum)
        ))
        for (i in result.indices) result[i] =
            (LoadAndSaveBigInt.getIntNew(result.size - i - 1, x.mag, x.sigNum, xnz)
                    and LoadAndSaveBigInt.getIntNew(result.size - i - 1, y.mag, y.sigNum, ynz).inv())

        return ExportImportBigInt.valueOf(result)
    }
}