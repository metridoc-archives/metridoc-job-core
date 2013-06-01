package metridoc.core

/**
 * @deprecated should replace all usages with groovy power asserts
 */
class Assert {

    static void isTrue(boolean expression, String message) {
        if (!expression) {
            throwException(message)
        }
    }

    static void notNull(value, String message) {
        if (value == null) {
            throwException(message)
        }
    }

    private static void throwException(String message) {
        throw new IllegalArgumentException(message)
    }
}
