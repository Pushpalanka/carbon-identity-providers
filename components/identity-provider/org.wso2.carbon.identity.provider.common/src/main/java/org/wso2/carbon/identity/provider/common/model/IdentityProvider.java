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

package org.wso2.carbon.identity.provider.common.model;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * Abstract representation of an Identity Provider
 */
public abstract class IdentityProvider implements Serializable {

    private static final long serialVersionUID = 690422066395820761L;

    private boolean isEnabled;
    protected final MetaIdentityProvider metaIdentityProvider;
    private ProvisioningConfig provisioningConfig;
    private AuthenticatorConfig authenticatorConfig;
    private Collection<IdentityProviderProperty> properties = new HashSet<IdentityProviderProperty>();

    protected IdentityProvider(IdentityProviderBuilder builder) {
        this.isEnabled = builder.isEnabled;
        this.metaIdentityProvider = builder.metaIdentityProvider;
        this.provisioningConfig =
                builder.provisioningConfigBuilder != null ? builder.provisioningConfigBuilder.build() : null;
        this.authenticatorConfig =
                builder.authenticatorConfigBuilder != null ? builder.authenticatorConfigBuilder.build() : null;
        this.properties = builder.properties;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public MetaIdentityProvider getMetaIdentityProvider() {
        return metaIdentityProvider;
    }

    void setMetaIdentityProvider(MetaIdentityProvider metaIdentityProvider) {}

    public ProvisioningConfig getProvisioningConfig() {
        return provisioningConfig;
    }

    public AuthenticatorConfig getAuthenticatorConfig() {
        return authenticatorConfig;
    }

    // provisioning filter must go under JIT and Outbound provisioning configs
    // Prov. filter is a example to have multiple values for one IDP based on SP

    public Collection<IdentityProviderProperty> getProperties() {
        return CollectionUtils.unmodifiableCollection(properties);
    }

    /**
     * Builds the representation of identity provider including mata details, provisioning configuration builder,
     * authenticators and other properties.
     */
    public static abstract class IdentityProviderBuilder {

        private boolean isEnabled = true;
        private MetaIdentityProvider metaIdentityProvider;
        private ProvisioningConfig.ProvisioningConfigBuilder provisioningConfigBuilder =
                new ProvisioningConfig.ProvisioningConfigBuilder();
        private AuthenticatorConfig.AuthenticatorConfigBuilder authenticatorConfigBuilder;
        private Collection<IdentityProviderProperty> properties = new HashSet<IdentityProviderProperty>();

        protected IdentityProviderBuilder(MetaIdentityProvider metaIdentityProvider) {
            this.metaIdentityProvider = metaIdentityProvider;
        }

        public IdentityProviderBuilder setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public IdentityProviderBuilder setMetaIdentityProvider(MetaIdentityProvider metaIdentityProvider) {
            this.metaIdentityProvider = metaIdentityProvider;
            return this;
        }

        public IdentityProviderBuilder setProvisioningClaims(Collection<ProvisioningClaim> provisioningClaims) {
            this.provisioningConfigBuilder.setProvisioningClaims(provisioningClaims);
            return this;
        }

        public IdentityProviderBuilder addProvisionClaim(ProvisioningClaim provisioningClaim) {
            this.provisioningConfigBuilder.addProvisionClaim(provisioningClaim);
            return this;
        }

        public IdentityProviderBuilder addProvisioningClaims(Collection<ProvisioningClaim> provisioningClaims) {
            this.provisioningConfigBuilder.addProvisioningClaims(provisioningClaims);
            return this;
        }

        public IdentityProviderBuilder setProvisioningIdPs(Collection<String> provisioningIdPs) {
            this.provisioningConfigBuilder.setProvisioningIdP(provisioningIdPs);
            return this;
        }

        public IdentityProviderBuilder addProvisioningIdP(String provisioningIdP) {
            this.provisioningConfigBuilder.addProvisioningIdP(provisioningIdP);
            return this;
        }

        public IdentityProviderBuilder addProvisioningIdPs(Collection<String> provisioningIdPs) {
            this.provisioningConfigBuilder.addProvisioningIdPs(provisioningIdPs);
            return this;
        }

        public IdentityProviderBuilder setProvisioners(Collection<ProvisionerConfig> provisioners) {
            this.provisioningConfigBuilder.setProvisioners(provisioners);
            return this;
        }

        public IdentityProviderBuilder addProvisioner(ProvisionerConfig provisioner) {
            this.provisioningConfigBuilder.addProvisioner(provisioner);
            return this;
        }

        public IdentityProviderBuilder addProvisioners(Collection<ProvisionerConfig> provisioners) {
            this.provisioningConfigBuilder.addProvisioners(provisioners);
            return this;
        }

        public IdentityProviderBuilder setAuthenticatorEnabled(boolean isEnabled) {
            this.authenticatorConfigBuilder.setEnabled(isEnabled);
            return this;
        }

        public IdentityProviderBuilder setAuthenticatorProperties(
                Collection<IdentityConnectorProperty> properties) {
            this.authenticatorConfigBuilder.setProperties(properties);
            return this;
        }

        public IdentityProviderBuilder addAuthenticatorProperty(IdentityConnectorProperty property) {
            this.authenticatorConfigBuilder.addProperty(property);
            return this;
        }

        public IdentityProviderBuilder addAuthenticatorProperties(
                Collection<IdentityConnectorProperty> properties) {
            this.authenticatorConfigBuilder.addProperties(properties);
            return this;
        }

        public IdentityProviderBuilder setIdentityProviderProperties(Collection<IdentityProviderProperty> properties) {
            if (!properties.isEmpty()) {
                this.properties.clear();
                this.properties.addAll(properties);
            }
            return this;
        }

        public IdentityProviderBuilder addIdentityProviderProperty(IdentityProviderProperty property) {
            if (property != null) {
                properties.add(property);
            }
            return this;
        }

        public IdentityProviderBuilder addIdentityProviderProperties(Collection<IdentityProviderProperty> properties) {
            if (!properties.isEmpty()) {
                properties.addAll(properties);
            }
            return this;
        }

        public abstract <T extends Object> T build() ;
    }
}
