/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.http.impl

import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC3
 */
class DefaultUserAgentTest {

    @Test
    void testGetMediaTypes() {

        def request = createMock(HttpServletRequest)

        String accept = 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'

        expect(request.getHeader(eq('Accept'))).andReturn(accept);

        replay request

        List<DefaultUserAgent.AcceptedMediaType> mimeTypes = new DefaultUserAgent(request).getMimeTypes();

        assertNotNull mimeTypes
        assertEquals mimeTypes.size(), 4

        assertEquals mimeTypes[0].name, 'text/html'
        assertEquals mimeTypes[0].quality, 1d

        assertEquals mimeTypes[1].name, 'application/xhtml+xml'
        assertEquals mimeTypes[1].quality, 1d

        assertEquals mimeTypes[2].name, 'application/xml'
        assertEquals mimeTypes[2].quality, 0.9d

        assertEquals mimeTypes[3].name, '*/*'
        assertEquals mimeTypes[3].quality, 0.8d

        verify request
    }

    @Test
    void testGetMediaTypesWithOutOfOrderPriorities() {

        def request = createMock(HttpServletRequest)

        String accept = '*/*;foo=bar;q=0.8;blah,text/html,application/xml;q=0.9,application/xhtml+xml'

        expect(request.getHeader(eq('Accept'))).andReturn(accept);

        replay request

        List<DefaultUserAgent.AcceptedMediaType> mimeTypes = new DefaultUserAgent(request).getMimeTypes();

        assertNotNull mimeTypes
        assertEquals mimeTypes.size(), 4

        assertEquals mimeTypes[0].name, 'text/html'
        assertEquals mimeTypes[0].quality, 1d

        assertEquals mimeTypes[1].name, 'application/xhtml+xml'
        assertEquals mimeTypes[1].quality, 1d

        assertEquals mimeTypes[2].name, 'application/xml'
        assertEquals mimeTypes[2].quality, 0.9d

        assertEquals mimeTypes[3].name, '*/*'
        assertEquals mimeTypes[3].quality, 0.8d

        verify request
    }

    @Test
    void testIsHtmlPreferredTrue() {

        def request = createMock(HttpServletRequest)

        String accept = 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'

        expect(request.getHeader(eq('Accept'))).andReturn(accept);

        replay request

        assertTrue new DefaultUserAgent(request).isHtmlPreferred()

        verify request
    }

    @Test
    void testIsHtmlPreferredWhenJsonPrecedesHtml() {

        def request = createMock(HttpServletRequest)

        String accept = 'application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'

        expect(request.getHeader(eq('Accept'))).andReturn(accept);

        replay request

        assertFalse new DefaultUserAgent(request).isHtmlPreferred()

        verify request
    }

    @Test
    void testIsHtmlPreferredWhenNoHtmlMediaTypesExist() {

        def request = createMock(HttpServletRequest)

        String accept = 'application/json,application/xml;q=0.9,*/*;q=0.8'

        expect(request.getHeader(eq('Accept'))).andReturn(accept);

        replay request

        assertFalse new DefaultUserAgent(request).isHtmlPreferred()

        verify request
    }
}
