package org.angproj.big

/**
 * This object provides methods to create `BigInt` instances from unsigned byte arrays.
 * It is used internally to handle the conversion of byte arrays into `BigInt` objects
 * without leading zero bytes.
 * */
public object Unsigned {

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

    /**
     * Creates a `BigInt` instance from an unsigned byte array.
     *
     * @param bytes the byte array representing the magnitude of the `BigInt`.
     * @return a `BigInt` instance with the specified magnitude and positive sign.
     * @throws BigMathException if the byte array is empty or has zero length.
     * */
    public fun internalOf(bytes: ByteArray): BigInt = internalOf(bytes, bytes.size) { this[it] }

    public fun <E> internalOf(data: E, size: Int, readOctet: E.(i: Int) -> Byte) : BigInt {
        ensureThat<BigMathException>(size > 0) { "Zero length magnitude" }

        val firstOctet = data.readOctet(0).toInt()
        val mag = stripLeadingZeroBytes(firstOctet, data, size, readOctet)
        return BigInt(mag, if(mag.isEmpty()) BigSigned.ZERO else BigSigned.POSITIVE)
    }
}