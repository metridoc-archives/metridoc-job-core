package metridoc.camel

import groovy.util.logging.Slf4j
import org.apache.camel.spi.Registry

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/28/12
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class CamelScriptRegistry implements Registry {
    Closure closure
    def delegateOverride
    private Map<String, Object> _propertiesMap = [:]

    Map<String, Object> getPropertiesMap() {
        if (_propertiesMap) return _propertiesMap
        assert closure: "closure must not be null"
        def owner = closure.owner
        def delegate = closure.delegate
        switch (closure.resolveStrategy) {
            case Closure.DELEGATE_FIRST:
                loadPropertyMap(owner, delegate, delegateOverride)
                break
            case Closure.DELEGATE_ONLY:
                loadPropertyMap(delegate, delegateOverride)
                break
            case Closure.OWNER_FIRST:
                loadPropertyMap(delegate, delegateOverride, owner)
                break
            case Closure.OWNER_ONLY:
                loadPropertyMap([owner] as Object[])
                break
            default:
                loadPropertyMap([owner] as Object[])
        }

        return _propertiesMap
    }

    private loadPropertyMap(Object... objects) {
        objects.each {
            if (it) {
                if (it instanceof Script) {
                    def binding = it.binding
                    if (binding) {
                        _propertiesMap.putAll(binding.variables)
                    }
                }
                if (it.properties) {
                    _propertiesMap.putAll(it.properties)
                }

                if (it instanceof Map) {
                    _propertiesMap.putAll(it)
                }
            }
        }
    }

    Object lookup(String name) {
        propertiesMap[name]
    }

    def <T> T lookup(String name, Class<T> type) {
        def o = lookup(name);

        try {
            if (o) {
                return type.cast(o);
            }
        } catch (ClassCastException ex) {
            log.debug "Could not convert object with name $name and type ${o.getClass()} to ${type.name}, lookup will return null instead of the object value", ex
        }

        return null
    }

    def <T> Map<String, T> lookupByType(Class<T> type) {
        propertiesMap.findAll {
            lookup(it.key, type) //if it is null, it will be skipped
        }
    }
}
