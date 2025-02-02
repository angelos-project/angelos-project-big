package org.angproj.big

import org.angproj.aux.io.toBinary
import org.angproj.aux.util.NullObject
import kotlin.test.*
import java.math.BigInteger as JavaBigInteger

class BigIntTest {

    @Test
    fun testNull() {
        assertTrue(NullObject.bigInt.isNull())
        assertFalse(BigInt.zero.isNull())
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when importing from the said ByteArray and exporting to a new ByteArray.
     * */
    @Test
    fun testByteArray() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi,
                xBi2
            )
        }
    }

    /**
     * This test recognizes that BigInt can predict its export size properly.
     * */
    @Test
    fun testToSize(): Unit = withLogic {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                getByteSize(xBi2),
                xBi2.toByteArray().size,
            )
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a Long random value
     * the same way when importing and then exporting to a new ByteArray.
     * */
    @Test
    fun testLong() {
        Combinator.longGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger.valueOf(x)

            Debugger.assertContentEquals(
                x,
                xJbi,
                xBi2,
                xJbi,
                xBi2
            )
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when exporting toInt.
     * */
    @Test
    fun testToInt() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                xJbi.toInt(),
                xBi2.toInt()
            )
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when exporting toLong.
     * */
    @Test
    fun testToLong() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                xJbi.toLong(),
                xBi2.toLong()
            )
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the sigNum of the same underlying value similarly.
     * */
    @Test
    fun testSigNum() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                xJbi.signum(),
                xBi2.sigNum.state
            )
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitLength of the same underlying value similarly.
     * */
    @Test
    fun testBitLength() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                xJbi.bitLength(),
                xBi2.bitLength
            )
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitCount of the same underlying value similarly.
     * */
    @Test
    fun testBitCount() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x.toBinary())
            val xJbi = JavaBigInteger(x)

            Debugger.assertEquals(
                x,
                xJbi,
                xBi2,
                xJbi.bitCount(),
                xBi2.bitCount
            )
        }
    }

    /**
     * This test recognizes whether large zero unsigned big integer
     * translates into zero magnitude and sigNum properly.
     * */
    @Test
    fun testUnsignedBigIntOf() {
        val xBi2 = unsignedBigIntOf(ByteArray(100) { 0 }.toBinary())
        val yBi2 = unsignedBigIntOf((ByteArray(100) { 0 }).also { it[5] = 1 })

        assertTrue(xBi2.sigNum.isZero())
        assertTrue(xBi2.mag.isEmpty())

        assertTrue(yBi2.sigNum.isNonZero())
        assertTrue(yBi2.mag.isNotEmpty())
    }
}