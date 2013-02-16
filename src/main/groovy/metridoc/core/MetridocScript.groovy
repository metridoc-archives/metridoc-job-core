package metridoc.core

/**
 * Class to use if you are dong groovy scripting
 */
class MetridocScript {

    static void target(LinkedHashMap description, Closure unitOfWork) {

    }

    /**
     * fires off the default target.  This is "default" by default, can be changed by binding defaultTarget to something
     * else in the groovy script
     */
    static void runTargets(Script script) {

    }

    static void runTargets(Script script, String... targets) {

    }
}
