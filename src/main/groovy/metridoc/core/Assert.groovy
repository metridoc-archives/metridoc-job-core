package metridoc.core

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 2/15/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
class Assert {

    static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message)
        }
    }
}
