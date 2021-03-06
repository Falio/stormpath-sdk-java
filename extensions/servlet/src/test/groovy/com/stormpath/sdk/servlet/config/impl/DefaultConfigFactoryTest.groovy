package com.stormpath.sdk.servlet.config.impl

import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC9
 */
class DefaultConfigFactoryTest {

    MockServletContext mockServletContext
    Config config

    @BeforeMethod
    void setup() {
        mockServletContext = new MockServletContext()
        config = new ConfigLoader().createConfig(mockServletContext)
    }

    @Test
    public void testReadFromDefaultProperties() {
        assertEquals config.get('stormpath.web.login.uri'), '/login'
    }

    @Test
    public void testStormPathPropertiesInClasspathOverridesDefault() {
        assertEquals config.get('stormpath.web.login.nextUri'), '/foo'
    }

    @Test
    public void testStormPathYAMLInClasspathOverridesDefault() {
        assertEquals config.get('stormpath.web.logout.uri'), '/getout'
    }

    @Test
    public void testStormPathJSONInClasspathOverridesDefault() {
        assertEquals config.get('stormpath.web.verify.nextUri'), '/home?status=fabulous'
    }

    @Test
    public void testBaseUrlFromEnvironmentVariable() {
        assertEquals config.get('stormpath.baseUrl'), 'http://api.stormpath.com/v100'
        Map<String, String> env = new HashMap<>()
        def baseUrl = 'http://env.stormpath.com/v2'
        env.put('STORMPATH_BASEURL', baseUrl)
        setEnv(env)
        config = new ConfigLoader().createConfig(new MockServletContext())
        assertEquals config.get('stormpath.baseUrl'), baseUrl
    }

    // From http://stackoverflow.com/a/496849
    private static void setEnv(Map<String, String> newenv) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for(Class cl : classes) {
            if('java.util.Collections$UnmodifiableMap'.equals(cl.getName())) {
                Field field = cl.getDeclaredField('m');
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
    }
}
