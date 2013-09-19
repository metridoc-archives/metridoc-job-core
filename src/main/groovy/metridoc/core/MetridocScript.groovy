package metridoc.core

import metridoc.core.services.ConfigService

/**
 * Class to use if you are doing groovy scripting and want to add Metridoc functionality
 */
class MetridocScript {


    private static StepManager getManager(Script self) {
        initializeTargetManagerIfNotThere(self.binding)
        self.stepManager
    }

    private static StepManager getManager(Binding binding) {
        initializeTargetManagerIfNotThere(binding)
        binding.stepManager
    }

    private static initializeTargetManagerIfNotThere(Script script) {
        initializeTargetManagerIfNotThere(script.binding)
    }

    private static initializeTargetManagerIfNotThere(Binding binding) {
        if (!binding.hasVariable("stepManager")) {
            StepManager stepManager = new StepManager(binding: binding)
            binding.stepManager = stepManager
        }

        if (!binding.hasVariable("targetManager")) {
            binding.targetManager = binding.stepManager
        }
    }

    static ConfigObject configure(Script self) {
        //Need to make sure that we use the correct classloader, fixes issues with remote runs
        Thread.currentThread().setContextClassLoader(self.getClass().getClassLoader())
        includeService(self, ConfigService)
        return self.binding.config
    }

    static void step(Script self, LinkedHashMap description, Closure unitOfWork) {
        initializeTargetManagerIfNotThere(self)
        getManager(self).step(description, unitOfWork)
    }

    /**
     * @deprecated
     * @param self
     * @param description
     * @param unitOfWork
     */
    static void target(Script self, LinkedHashMap description, Closure unitOfWork) {
        step(self, description, unitOfWork)
    }

    static void step(Binding self, LinkedHashMap description, Closure unitOfWork) {
        initializeTargetManagerIfNotThere(self)
        getManager(self).step(description, unitOfWork)
    }

    /**
     * @deprecated
     * @param self
     * @param description
     * @param unitOfWork
     */
    static void target(Binding self, LinkedHashMap description, Closure unitOfWork) {
        step(self, description, unitOfWork)
    }

    static void includeSteps(Script self, Class<Script> steps) {
        getManager(self).includeSteps(steps)
    }

    static void includeTargets(Script self, Class<Script> steps) {
        includeSteps(self, steps)
    }

    /**
     * @deprecated
     * @param self
     * @param targets
     */
    static void includeTargets(Binding self, Class<Script> targets) {
        includeSteps(self, targets)
    }

    static void includeSteps(Binding self, Class<Script> steps) {
        getManager(self).includeSteps(steps)
    }

    static <T> T includeService(Script self, Class<T> tool) {
        getManager(self).includeService(tool)
    }

    /**
     * @deprecated
     * @param self
     * @param tool
     * @return
     */
    static <T> T includeTool(Script self, Class<T> tool) {
        getManager(self).includeService(tool)
    }

    static <T> T includeService(Binding self, Class<T> tool) {
        getManager(self).includeService(tool)
    }

    /**
     * @deprecated
     * @param self
     * @param tool
     * @return
     */
    static <T> T includeTool(Binding self, Class<T> tool) {
        getManager(self).includeService(tool)
    }

    static <T> T includeService(Script self, LinkedHashMap args, Class<T> tool) {
        getManager(self).includeService(args, tool)
    }

    /**
     * @deprecated
     * @param self
     * @param args
     * @param tool
     * @return
     */
    static <T> T includeTool(Script self, LinkedHashMap args, Class<T> tool) {
        getManager(self).includeService(args, tool)
    }

    static <T> T includeService(Binding self, LinkedHashMap args, Class<T> tool) {
        getManager(self).includeService(args, tool)
    }

    /**
     * @deprecated
     * @param self
     * @param args
     * @param tool
     * @return
     */
    static <T> T includeTool(Binding self, LinkedHashMap args, Class<T> tool) {
        getManager(self).includeService(args, tool)
    }

    static void runDefaultStep(Script self) {
        getManager(self).runDefaultStep()
    }

    /**
     * @deprecated
     * @param self
     */
    static void runDefaultTarget(Script self) {
        runDefaultStep(self)
    }

    static void runDefaultStep(Binding self) {
        getManager(self).runDefaultStep()
    }

    /**
     * @deprecated
     * @param self
     */
    static void runDefaultTarget(Binding self) {
        runDefaultStep(self)
    }

    static void runSteps(Script self, String... steps) {
        getManager(self).depends(steps)
    }

    /**
     * @deprecated
     * @param self
     * @param targets
     */
    static void runTargets(Script self, String... targets) {
        runSteps(self, targets)
    }

    static void runSteps(Binding self, String... steps) {
        getManager(self).depends(steps)
    }

    /**
     * @deprecated
     * @param self
     * @param targets
     */
    static void runTargets(Binding self, String... targets) {
        runSteps(self, targets)
    }

    static void depends(Script self, String... targetDependencies) {
        getManager(self).depends(targetDependencies)
    }

    static void depends(Binding self, String... targetDependencies) {
        getManager(self).depends(targetDependencies)
    }

    static void profile(Script self, String description, Closure work) {
        getManager(self).profile(description, work)
    }

    static void profile(Binding self, String description, Closure work) {
        getManager(self).profile(description, work)
    }
}
