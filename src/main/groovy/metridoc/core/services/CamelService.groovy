package metridoc.core.services

import camelscript.CamelGLite

/**
 * @author Tommy Barker
 */
class CamelService {
    Binding binding
    @Delegate
    CamelGLite delegate

    void init() {
        delegate = new CamelGLite(binding)
    }
}
