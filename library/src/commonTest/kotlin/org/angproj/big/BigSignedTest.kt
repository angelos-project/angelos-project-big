package org.angproj.big

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BigSignedTest {

    @Test
    fun testNegate() {
        assertEquals(BigSigned.NEGATIVE, BigSigned.POSITIVE.negate())
        assertEquals(BigSigned.POSITIVE, BigSigned.NEGATIVE.negate())
        assertEquals(BigSigned.ZERO, BigSigned.ZERO.negate())
    }

    @Test
    fun testIsPositive() {
        assertTrue(BigSigned.POSITIVE.isPositive())
        assertFalse(BigSigned.ZERO.isPositive())
        assertFalse(BigSigned.NEGATIVE.isPositive())
    }

    @Test
    fun testIsZero() {
        assertFalse(BigSigned.POSITIVE.isZero())
        assertTrue(BigSigned.ZERO.isZero())
        assertFalse(BigSigned.NEGATIVE.isZero())
    }

    @Test
    fun testIsNegative() {
        assertFalse(BigSigned.POSITIVE.isNegative())
        assertFalse(BigSigned.ZERO.isNegative())
        assertTrue(BigSigned.NEGATIVE.isNegative())
    }

    @Test
    fun testIsNonZero() {
        assertTrue(BigSigned.POSITIVE.isNonZero())
        assertFalse(BigSigned.ZERO.isNonZero())
        assertTrue(BigSigned.NEGATIVE.isNonZero())
    }

    @Test
    fun testIsNonNegative() {
        assertTrue(BigSigned.POSITIVE.isNonNegative())
        assertTrue(BigSigned.ZERO.isNonNegative())
        assertFalse(BigSigned.NEGATIVE.isNonNegative())
    }

    @Test
    fun testStateValues() {
        assertEquals(1, BigSigned.POSITIVE.state)
        assertEquals(0, BigSigned.ZERO.state)
        assertEquals(-1, BigSigned.NEGATIVE.state)
    }

    @Test
    fun testSignedValues() {
        assertEquals(0, BigSigned.POSITIVE.signed)
        assertEquals(0, BigSigned.ZERO.signed)
        assertEquals(-1, BigSigned.NEGATIVE.signed)
    }
}