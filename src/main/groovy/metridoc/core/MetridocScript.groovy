package metridoc.core

import org.codehaus.groovy.reflection.ReflectionUtils

/**
 * Class to use if you are dong groovy scripting
 */
class MetridocScript {

    private static TargetManager getManager(Script self) {
        initializeTargetManagerIfNotThere(self.binding)
        self.targetManager
    }

    private static TargetManager getManager(Binding binding) {
        initializeTargetManagerIfNotThere(binding)
        binding.targetManager
    }

    private static initializeTargetManagerIfNotThere(Script script) {
        initializeTargetManagerIfNotThere(script.binding)
    }

    private static initializeTargetManagerIfNotThere(Binding binding) {
        if (!binding.hasVariable("targetManager")) {
            TargetManager targetManager = new TargetManager(binding: binding)
            binding.targetManager = targetManager
        }
    }

    static void target(Script self, LinkedHashMap description, Closure unitOfWork) {
        initializeTargetManagerIfNotThere(self)
        getManager(self).target(description, unitOfWork)
    }

    static void includeTargets(Script self, Class<Script> targets) {
        getManager(self).includeTargets(targets)
    }

    static void includeTool(Script self, Class tool) {
        getManager(self).includeTool(tool)
    }

    static void includeTool(Binding self, Class tool) {
        getManager(self).includeTool(tool)
    }

    static void runDefaultTarget(Script self) {
        getManager(self).runDefaultTarget()
    }

    static void runTargets(Script self, String... targets)  {
        getManager(self).depends(targets)
    }

    static void profile(Script self, String description, Closure work) {
        getManager(self).profile(description, work)
    }
}
