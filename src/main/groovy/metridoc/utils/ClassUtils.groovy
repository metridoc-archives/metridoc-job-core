package metridoc.utils

class ClassUtils {

    /**
     * copied directly from spring's C;assUtils.  Just didn't want to depend on sprign for this
     * @return default classloader, almost always the context class loader.
     */
    static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }

        return cl
    }
}
