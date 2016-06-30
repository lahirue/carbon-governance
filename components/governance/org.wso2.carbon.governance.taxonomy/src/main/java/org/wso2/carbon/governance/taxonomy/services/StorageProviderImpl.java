/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.governance.taxonomy.services;

import org.apache.commons.io.IOUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.taxonomy.beans.TaxonomyDocumentBean;
import org.wso2.carbon.governance.taxonomy.internal.ServiceHolder;
import org.wso2.carbon.governance.taxonomy.util.CommonUtils;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.user.api.UserStoreException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

/**
 * this class will implements methods to manage tenant specific taxonomy data map
 */
public class StorageProviderImpl implements StorageProvider {
    private static Map<Integer, Map<String, TaxonomyDocumentBean>> tenantTaxonomyMap;
    private int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();

    /**
     * This method will return taxonomy document bean object for a given taxonomy name from the map
     *
     * @param name String name of the taxonomy (this must be unique for a tenant)
     * @return Taxonomy document bean which contains taxonomy meta data
     */
    @Override
    public TaxonomyDocumentBean getTaxonomy(String name) {
        if (tenantTaxonomyMap != null) {
            if (tenantTaxonomyMap.containsKey(tenantId)) {
                return tenantTaxonomyMap.get(tenantId).get(name);
            }
        }
        return null;
    }

    /**
     * This method will remove taxonomy document bean object from the map for a given taxonomy name
     *
     * @param name String taxonomy name
     */
    @Override
    public void removeTaxonomy(String name) {
        if (tenantTaxonomyMap != null) {
            if (tenantTaxonomyMap.containsKey(tenantId)) {
                tenantTaxonomyMap.get(tenantId).remove(name);
            }
        }
    }

    /**
     * This method will add taxonomy document bean object to the map for a given taxonomy name
     *
     * @param documentBean Taxonomy document bean which contains taxonomy meta data
     */
    @Override
    public void addTaxonomy(TaxonomyDocumentBean documentBean) {
        if (tenantTaxonomyMap != null) {
            Map<String, TaxonomyDocumentBean> taxonomyMaps = tenantTaxonomyMap.get(tenantId);
            if (taxonomyMaps != null) {
                taxonomyMaps.put(documentBean.getTaxonomyName(), documentBean);
                tenantTaxonomyMap.put(tenantId, taxonomyMaps);
            } else {
                Map<String, TaxonomyDocumentBean> tempTaxonomyMap = new HashMap<>();
                tempTaxonomyMap.put(documentBean.getTaxonomyName(), documentBean);
                tenantTaxonomyMap.put(tenantId, tempTaxonomyMap);
            }
        } else {
            tenantTaxonomyMap = new HashMap<>();
            Map<String, TaxonomyDocumentBean> tempTaxonomyMap = new HashMap<>();
            tempTaxonomyMap.put(documentBean.getTaxonomyName(), documentBean);
            tenantTaxonomyMap.put(tenantId, tempTaxonomyMap);
        }
    }

    /**
     * This method will update taxonomy document bean object in the map for a given taxonomy name
     *
     * @param oldName      String name of the existing name
     * @param documentBean Taxonomy document bean object
     */
    @Override
    public void updateTaxonomy(String oldName, TaxonomyDocumentBean documentBean) {
        if (tenantTaxonomyMap != null) {
            Map<String, TaxonomyDocumentBean> taxonomyMaps = tenantTaxonomyMap.get(tenantId);

            if (taxonomyMaps.containsKey(oldName)) {
                taxonomyMaps.put(documentBean.getTaxonomyName(), taxonomyMaps.get(oldName));
                taxonomyMaps.remove(oldName);
            }

        }
    }

    /**
     * This method will initialize taxonomy maps with all taxonomy data.
     *
     * @throws UserStoreException           throws while getting RealmConfigurations
     * @throws RegistryException            throws getting files from registry
     * @throws IOException                  throws when reading file
     * @throws SAXException                 throws when parsing content stream
     * @throws ParserConfigurationException
     */
    public void initializeTaxonomyStorage()
            throws UserStoreException, RegistryException, IOException, SAXException, ParserConfigurationException {
        String path = "/taxonomy";
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        String adminName = ServiceHolder.getRealmService().getTenantUserRealm(tenantId).getRealmConfiguration()
                .getAdminUserName();
        Registry registry = ServiceHolder.getRegistryService().getGovernanceUserRegistry(adminName, tenantId);

        Collection collection = (Collection) registry.get(path);
        String[] childrenList = collection.getChildren();
        for (String child : childrenList) {
            String myString = IOUtils.toString(registry.get(child).getContentStream(), "UTF-8");
            TaxonomyDocumentBean taxonomyDocumentBean = CommonUtils.documentBeanBuilder(myString);
            addTaxonomy(taxonomyDocumentBean);
        }

    }

    @Override
    public List<String> getTaxonomiesByRXT(String name) {
        return null;
    }
}
