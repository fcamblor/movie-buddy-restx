package io.restx.movies.rest;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import restx.factory.Factory;
import restx.server.WebServerSupplier;
import restx.specs.RestxSpec;
import restx.tests.RestxSpecRule;
import restx.tests.RestxSpecTests;

import java.io.IOException;
import java.util.List;

/**
 * @author fcamblor
 */
@RunWith(Parameterized.class)
public class SpecsTests {

    @Rule
    // Temp workaround in order to be able to execute spec tests on "/" router path
    // See https://github.com/restx/restx/pull/100
    public RestxSpecRule specRule = new RestxSpecRule(
            "",
            Factory.getInstance().queryByClass(WebServerSupplier.class).findOne().get().getComponent(),
            Factory.getInstance());

    private RestxSpec spec;

    @Parameterized.Parameters(name="{0}")
    public static Iterable<Object[]> data() throws IOException {
        List<RestxSpec> specs = RestxSpecTests.findSpecsIn("specs");
        return Iterables.transform(specs, new Function<RestxSpec, Object[]>() {
            @Override
            public Object[] apply(RestxSpec spec) {
                return new Object[]{ spec.getTitle(), spec };
            }
        });
    }

    public SpecsTests(String specName, RestxSpec spec) {
        this.spec = spec;
    }

    @Test
    public void run_spec() throws IOException {
        specRule.runTest(this.spec);
    }
}
