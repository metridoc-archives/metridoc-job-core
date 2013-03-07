package metridoc.core

import metridoc.core.tools.ConfigTool

/**
 * Class to use if you are doing groovy scripting and want to add Metridoc functionality
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

    static ConfigObject configure(Script self) {
        //Need to make sure that we use the correct classloader, fixes issues with remote runs
        Thread.currentThread().setContextClassLoader(self.getClass().getClassLoader())
        includeTool(self, ConfigTool)
        return self.binding.config
    }

    static void target(Script self, LinkedHashMap description, Closure unitOfWork) {
        initializeTargetManagerIfNotThere(self)
        getManager(self).target(description, unitOfWork)
    }

    static void includeTargets(Script self, Class<Script> targets) {
        getManager(self).includeTargets(targets)
    }

    static <T> T includeTool(Script self, Class<T> tool) {
        getManager(self).includeTool(tool)
    }

    static <T> T includeTool(Binding self, Class<T> tool) {
        getManager(self).includeTool(tool)
    }

    static void runDefaultTarget(Script self) {
        getManager(self).runDefaultTarget()
    }

    static void runTargets(Script self, String... targets) {
        getManager(self).depends(targets)
    }

    static void depends(Script self, String... targetDependencies) {
        getManager(self).depends(targetDependencies)
    }

    static void profile(Script self, String description, Closure work) {
        getManager(self).profile(description, work)
    }
}
