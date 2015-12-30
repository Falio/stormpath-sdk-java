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
package com.stormpath.sdk.saml;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.ResourceException;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @since 1.0.RC8
 */
public class SamlRuntimeException extends ResourceException {

    // TODO: Micah - What are the right error codes here?
    private final static List<Integer> supportedErrors = java.util.Collections.unmodifiableList(Arrays.asList(10011, 10012, 11001, 11002, 11003, 12001));

    public SamlRuntimeException(Error error) {
        super(error);
        Assert.isTrue(supports(error), "error type not supported; must be one of: " + supportedErrors.toString());
    }

    // TODO: Micah - What are the right error code conversions here?
    public void rethrow() throws InvalidSamlTokenException, SamlSessionTimeoutException {
        Error error = this.getStormpathError();

        if (error.getCode() == 10011 || error.getCode() == 10012 || error.getCode() == 11001 || error.getCode() == 11002 || error.getCode() == 11003) {
            throw new InvalidSamlTokenException(error);
        }

        if (this.getStormpathError().getCode() == 12001) {
            throw new SamlSessionTimeoutException(error);
        }

        throw new IllegalStateException("error type is unrecognized: " + error.getCode());
    }

    protected boolean supports(Error error) {
        return supportedErrors.contains(error.getCode());
    }


}
