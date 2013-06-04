package metridoc.camel

import groovy.util.logging.Slf4j
import org.apache.camel.spi.Registry

/**
 * @deprecated moving all new camel functionality to camel-glite
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

    @Override
    Object lookupByName(String name) {
        lookup(name)
    }

    @Override
    def <T> T lookupByNameAndType(String name, Class<T> type) {
        lookup(name, type)
    }

    @Override
    def <T> Map<String, T> findByTypeWithName(Class<T> type) {
        lookupByType(type)
    }

    @Override
    def <T> Set<T> findByType(Class<T> type) {
        findByTypeWithName(type).values() as Set
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
