package org.angproj.big

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RandomTest {

    @Test
    fun testCreateRandomBigInt() = withLogic {
        (0 until 256).forEach {
            val rand = BigInt.createRandomBigInt(it)
            //println(BinHex.encodeToHex(rand.toByteArray()))
            assertEquals(rand.bitLength, it)
        }
    }

    @Test
    fun testCreateRandomInRange() = withLogic {
        (0 until 128).forEach {
            val min = BigInt.createRandomBigInt(it).negate()
            val max = min.subtract(BigInt.minusOne)
            val inBetween = BigInt.createRandomInRange(min, max)
            //println(BinHex.encodeToHex(inBetween.toByteArray()))
            assertTrue { min.compareSpecial(inBetween).isLesserOrEqual() }
            assertTrue { max.compareSpecial(inBetween).isGreaterOrEqual() }
        }

        (0 until 128).forEach {
            val min = BigInt.createRandomBigInt(it).negate()
            val max = min.add(BigInt.one)
            val inBetween = BigInt.createRandomInRange(min, max)
            //println(BinHex.encodeToHex(inBetween.toByteArray()))
            assertTrue { min.compareSpecial(inBetween).isLesserOrEqual() }
            assertTrue { max.compareSpecial(inBetween).isGreaterOrEqual() }
        }

        (0 until 128).forEach {
            val min = BigInt.createRandomBigInt(it)
            val max = min.add(BigInt.one)
            val inBetween = BigInt.createRandomInRange(min, max)
            //println(BinHex.encodeToHex(inBetween.toByteArray()))
            assertTrue { min.compareSpecial(inBetween).isLesserOrEqual() }
            assertTrue { max.compareSpecial(inBetween).isGreaterOrEqual() }
        }
    }
}