package com.stormpath.sdk.servlet.config

import com.stormpath.sdk.impl.config.ResourcePropertiesSource
import com.stormpath.sdk.impl.config.YAMLPropertiesSource
import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.DefaultResourceFactory
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.sdk.impl.io.ResourceFactory
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * A test that downloads the <a href="https://github.com/stormpath/stormpath-framework-spec/blob/master/example-config.yaml">web config
 * from the stormpath-framework-spec</a> and compares it with <code>com/stormpath/sdk/servlet/config/web.stormpath.properties</code>.
 *
 * This test will fail when a new property is added to the the spec's example-config.yaml but
 * it does not exist in <code>web.stormpath.properties</code>.
 *
 * @since 1.0.0
 */
class SpecConfigVersusWebPropertiesTest {

    def SPEC_CONFIG_LOCATION = "https://raw.githubusercontent.com/stormpath/stormpath-framework-spec/master/example-config.yaml"
    def DEFAULT_CONFIG_LOCATION = ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/servlet/config/web.stormpath.properties"
    def specProperties, defaultProperties

    @BeforeClass
    void before() {
        String specConfig = SPEC_CONFIG_LOCATION.toURL().getText();
        specProperties = new YAMLPropertiesSource(new TestStringResource(specConfig)).properties

        ResourceFactory resourceFactory = new DefaultResourceFactory()
        Resource defaultConfig = resourceFactory.createResource(DEFAULT_CONFIG_LOCATION)

        defaultProperties = new ResourcePropertiesSource(defaultConfig).properties
    }

    @Test
    void verifyPropertiesInSpecAreInDefault() {

        def diff = specProperties.findResults { k,v ->
            defaultProperties.containsKey(k) ? null : k
        }

        if (diff.size > 0) {
            println "It looks like a new property was added to the Framework Spec and the Java SDK is missing it."
            println "Missing keys in web.stormpath.properties:"
            diff.each {
                println "${it}"
            }
            println "You must add this new property to the Java SDK in order for this test to pass."
            println "Or you could adjust the assertEquals statement in this method to allow for this missing key as a temporary solution."
        }

        assertEquals diff.size(), 0, "Missing keys in default config: ${diff}"
    }

    @Test
    void verifyPropertiesInDefaultAreInSpec() {
        def diff = defaultProperties.findResults { k,v ->
            specProperties.containsKey(k) ? null : k
        }

        if (diff.size != 64) {
            println "It looks like a new property was added to the Framework Spec or to web.stormpath.properties."
            println "Please examine this method to see the mismatch and commented code for debugging what's changed."
        }

        assertEquals diff.size(), 64, "Missing keys in spec config: ${diff}"

        // to see the keys missing in spec, uncomment the following
        /*if (diff.size > 0) {
            println "Missing keys in spec:"
            diff.each {
                println "${it}"
            }
        }*/

        // to see the keys and their values for updating the wiki, uncomment the following
        // https://github.com/stormpath/stormpath-sdk-java/wiki/1.0-Configuration-Changes-&-Additions-Guide#not-in-specification
        /*
        SortedSet<String> keys = new TreeSet<String>(properties.keySet());
        keys.each {
            println("|${it}|" + properties.get(it) + "|")
        }*/
    }

    @Test(enabled = false)
    // todo: Do we care about null vs. blank?
    void verifyValuesMatch() {
        specProperties.findResults { k,v ->
            assertEquals defaultProperties.get(k), v
        }
    }
}

class TestStringResource implements Resource {

    private String string;

    public TestStringResource(String string) {
        this.string = string;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(string.getBytes());
    }
}
