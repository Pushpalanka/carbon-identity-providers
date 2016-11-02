/*
 * Copyright (c) 2016 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.provider.model;

/**
 * Resident identity provider representation.
 */
public class ResidentIdentityProvider extends IdentityProvider {

    private ResidentIdentityProvider(ResidentIdentityProviderBuilder builder) {
        super(builder);
    }

    public static ResidentIdentityProviderBuilder newBuilder(int id, String name) {
        return new ResidentIdentityProviderBuilder(id, name);
    }

    protected static class ResidentIdentityProviderBuilder extends IdentityProviderBuilder<ResidentIdentityProvider> {

        protected ResidentIdentityProviderBuilder(int id, String name) {
            super(id, name);
        }

        @Override
        public ResidentIdentityProvider build() {
            return new  ResidentIdentityProvider(this);
        }
    }
}
