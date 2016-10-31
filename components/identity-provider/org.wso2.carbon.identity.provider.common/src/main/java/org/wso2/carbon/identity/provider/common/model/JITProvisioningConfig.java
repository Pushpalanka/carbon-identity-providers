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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * Configuration of just-in-time provisioning.
 */
public class JITProvisioningConfig implements Serializable {

    private static final long serialVersionUID = -3886962663799281628L;

    private boolean jitEnabled;
    //IDPs list who is provisioning to this IDP.
    private Collection<String> provisioningIdPs = new HashSet<String>();

    private JITProvisioningConfig(JITProvisioningConfigBuilder builder) {
        this.jitEnabled = builder.jitEnabled;
        this.provisioningIdPs = builder.provisioningIdPs;
    }

    public boolean isJitEnabled() {
        return jitEnabled;
    }

    public Collection<String> getProvisioningIdPs() {
        return CollectionUtils.unmodifiableCollection(provisioningIdPs);
    }

    /**
     * Builds the configuration for just-in-time provisioning.
     */
    public static class JITProvisioningConfigBuilder {

        private boolean jitEnabled;
        private Collection<String> provisioningIdPs = new HashSet<String>();

        public JITProvisioningConfigBuilder() {

        }

        public JITProvisioningConfigBuilder setJitEnabled(boolean jitEnabled) {
            this.jitEnabled = jitEnabled;
            return this;
        }

        public JITProvisioningConfigBuilder setProvisioningIdPs(
                Collection<String> provisioningIdPs) {
            if (!provisioningIdPs.isEmpty()) {
                this.provisioningIdPs.clear();
                this.provisioningIdPs.addAll(provisioningIdPs);
            }
            return this;
        }

        public JITProvisioningConfigBuilder addProvisioningIdP(String provisioningIdP) {
            if (StringUtils.isNotBlank(provisioningIdP)) {
                this.provisioningIdPs.add(provisioningIdP);
            }
            return this;
        }

        public JITProvisioningConfigBuilder addProvisioningIdPs(
                Collection<String> provisioningIdPs) {
            if (!provisioningIdPs.isEmpty()) {
                this.provisioningIdPs.addAll(provisioningIdPs);
            }
            return this;
        }

        public JITProvisioningConfig build() {
            return new JITProvisioningConfig(this);
        }
    }
}
