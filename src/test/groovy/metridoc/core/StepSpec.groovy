package metridoc.core

import spock.lang.Specification

/**
 * @author Tommy Barker
 */
class StepSpec extends Specification {

    void "test step workflow from annotated class"() {
        given:
        def binding = new Binding()

        when:
        Foo foo
        use(MetridocScript) {
            foo = binding.includeService(Foo)
            binding.runStep("foo")
        }

        then:
        foo.fooRan && foo.barRan
    }

    class Foo {

        boolean barRan = false
        boolean fooRan = false

        @Step(description = "runs bar")
        void bar() {
            assert !fooRan: "foo should run first"
            barRan = true
        }

        @Step(description = "runs foo", depends = ["bar"])
        void foo() {
            fooRan = true
        }
    }
}

