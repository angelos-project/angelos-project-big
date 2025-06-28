package org.angproj.big

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BigCompareTest {

    @Test
    fun testIsGreater() {
        assertTrue(BigCompare.GREATER.isGreater())
        assertFalse(BigCompare.EQUAL.isGreater())
        assertFalse(BigCompare.LESSER.isGreater())
    }

    @Test
    fun testIsGreaterOrEqual() {
        assertTrue(BigCompare.GREATER.isGreaterOrEqual())
        assertTrue(BigCompare.EQUAL.isGreaterOrEqual())
        assertFalse(BigCompare.LESSER.isGreaterOrEqual())
    }

    @Test
    fun testIsEqual() {
        assertFalse(BigCompare.GREATER.isEqual())
        assertTrue(BigCompare.EQUAL.isEqual())
        assertFalse(BigCompare.LESSER.isEqual())
    }

    @Test
    fun testIsNotEqual() {
        assertTrue(BigCompare.GREATER.isNotEqual())
        assertFalse(BigCompare.EQUAL.isNotEqual())
        assertTrue(BigCompare.LESSER.isNotEqual())
    }

    @Test
    fun testIsLesserOrEqual() {
        assertFalse(BigCompare.GREATER.isLesserOrEqual())
        assertTrue(BigCompare.EQUAL.isLesserOrEqual())
        assertTrue(BigCompare.LESSER.isLesserOrEqual())
    }

    @Test
    fun testIsLesser() {
        assertFalse(BigCompare.GREATER.isLesser())
        assertFalse(BigCompare.EQUAL.isLesser())
        assertTrue(BigCompare.LESSER.isLesser())
    }

    @Test
    fun testStateValues() {
        assertTrue(BigCompare.GREATER.state == 1)
        assertTrue(BigCompare.EQUAL.state == 0)
        assertTrue(BigCompare.LESSER.state == -1)
    }
}