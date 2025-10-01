# BigInt

A high-quality arbitrary-precision big integer library implemented in pure Kotlin/Common for effective multiplatform support.

## Features

### Core Arithmetic Operations
- Addition, subtraction, multiplication, and division with remainder
- Square root calculation with remainder support
- Greatest Common Divisor (GCD) computation
- Modular arithmetic (Mod, Pow)
- Random number generation

### Bit Manipulation
- Bitwise operations (AND, OR, XOR, NOT, AND-NOT)
- Bit shifting (left/right)
- Individual bit operations (set, clear, flip, test)
- Bit length and bit count calculations

### Advanced Features
- Multi-platform support through Kotlin Multiplatform
- Efficient big-endian integer array representation
- Comprehensive fuzzing test suite for operation validation
- Serialization/deserialization support
- Unsigned operations handling
- Exception handling through BigMathException

### Performance Optimizations
- Specialized magnitude handling
- Efficient carry propagation in arithmetic operations
- Optimized division algorithm
- Smart memory management

## Getting Started

### Gradle Setup
```kotlin
dependencies {
    implementation("org.angproj.big:angelos-project-big:0.10.2")
}
```

### Basic Usage
```kotlin
import org.angproj.big.*

// Create BigInt instances
val a = bigIntOf(123456789)
val b = bigIntOf(987654321)

// Perform operations
val sum = a + b
val product = a * b
val quotient = a / b
val remainder = a % b

// Bit operations
val shifted = a shl 1
val andResult = a and b
```

## Quality Assurance

### Fuzzing Coverage
The library includes extensive fuzzing tests for core operations:
- Arithmetic operations (addition, multiplication, division)
- Bit manipulations (shifts, AND, OR, XOR)
- Conversion operations (to/from Long)
- Special operations (abs, max, pow)

### Testing
- Comprehensive unit test suite
- Property-based testing
- Java BigInteger compatibility tests
- Cross-platform validation

## Performance Comparison
- Optimized for cryptographic operations
- Comparable performance to Java BigInteger for standard operations
- Enhanced performance for specific use cases through specialized algorithms

## Contributing
Contributions are welcome! Please read our [Contributing Guidelines](CONTRIBUTING.md) for details on how to submit pull requests.

## License
This project is licensed under the [MIT License](LICENSE).
