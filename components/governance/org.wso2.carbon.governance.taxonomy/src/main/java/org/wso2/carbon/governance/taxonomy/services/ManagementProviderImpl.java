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

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.common.services.RegistryAbstractAdmin;
import org.wso2.carbon.registry.core.*;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import javax.xml.namespace.QName;
import static org.wso2.carbon.governance.taxonomy.util.CommonUtils.buildOMElement;

class ManagementProviderImpl extends RegistryAbstractAdmin implements ManagementProvider {

    private static final Log log = LogFactory.getLog(TaxonomyManager.class);
    private static final String TAXONOMY_MEDIA_TYPE = "application/taxo+xml";
    private static String PATH = "/taxonomy/";

    /**
     * This method will add user defined taxonomy content into the registry
     *
     * @param payload String
     * @return boolean
     * @throws RegistryException
     */
    @Override
    public boolean addTaxonomy(String payload) throws RegistryException {
        Registry registry = getGovernanceUserRegistry();
        Resource resource;
        String name;
        OMElement element = null;
        element = buildOMElement(payload);
        name = element.getAttributeValue(new QName("name"));

        if (!getGovernanceUserRegistry().resourceExists(PATH + name)) {
            resource = new ResourceImpl();
            resource.setMediaType(TAXONOMY_MEDIA_TYPE);
            resource.setContent(payload);

            try {
                registry.beginTransaction();
                registry.put(PATH + name, resource);
                registry.commitTransaction();
            } catch (Exception e) {
                registry.rollbackTransaction();
                throw new RegistryException("Error while adding taxonomy", e);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * This method will delete the given taxonomy file from registry
     *
     * @return boolean
     * @throws RegistryException
     */
    @Override
    public boolean deleteTaxonomy(String name) throws RegistryException {
        Registry registry = getGovernanceUserRegistry();

        try {
            registry.beginTransaction();
            registry.delete(PATH + name);
            registry.commitTransaction();
            return true;
        } catch (Exception e) {
            registry.rollbackTransaction();
            throw new RegistryException("Error while deleting taxonomy", e);
        }
    }

    /**
     * This method will update the taxonomy file from the registry
     *
     * @param payload String
     * @return boolean
     * @throws RegistryException
     */
    @Override
    public boolean updateTaxonomy(String oldName, String payload) throws RegistryException {
        Registry registry = getGovernanceUserRegistry();

        String newName = null;
        OMElement element = null;
        element = buildOMElement(payload);

        if (element != null) {
            newName = element.getAttributeValue(new QName("name"));
        }

        if (newName == null || newName.equals("")) {
            return false; // invalid configuration
        }

        // add new resource with new name
        if (oldName == null || oldName.equals("")) {
            try {
                Resource resource;
                resource = new ResourceImpl();
                resource.setContent(payload);
                registry.beginTransaction();
                registry.put(PATH + newName, resource);
                registry.commitTransaction();
                return true;
            } catch (Exception e) {
                registry.rollbackTransaction();
                throw new RegistryException("Error while updating taxonomy", e);
            }
        }

        if (oldName.equals(newName)) {
            try {
                // add resource with same old name
                Resource resource;
                resource = registry.get(PATH + oldName);
                resource.setContent(payload);
                registry.beginTransaction();
                registry.put(PATH + oldName, resource);
                registry.commitTransaction();
                return true;
            } catch (Exception e) {
                registry.rollbackTransaction();
                throw new RegistryException("Error while updating taxonomy", e);
            }
        } else {
            try {
                // add new resource and remove old one
                Resource resource;
                resource = new ResourceImpl();
                resource.setContent(payload);
                registry.beginTransaction();
                registry.put(PATH + newName, resource);
                registry.delete(PATH + oldName);
                registry.commitTransaction();
                return true;
            } catch (Exception e) {
                registry.rollbackTransaction();
                throw new RegistryException("Error while updating taxonomy", e);
            }
        }
    }

    /**
     * This method will retrieve the given taxonomy data from registry
     *
     * @return String type data
     * @throws RegistryException
     */
    @Override
    public String getTaxonomy(String name) throws RegistryException {
        Registry registry = getGovernanceUserRegistry();

        if (getGovernanceUserRegistry().resourceExists(PATH + name)) {
            Resource resource;
            resource = registry.get(PATH + name);
            return RegistryUtils.decodeBytes((byte[]) resource.getContent());
        } else {
            return null;
        }
    }

    /**
     * This method will return all taxonomy file list inside given path of the registry
     *
     * @return String[] array
     * @throws RegistryException
     */
    @Override
    public String[] getTaxonomyList() throws RegistryException {
        String FULL_PATH = "/taxonomy/taxonomy.xml";
        Collection collection;
        Registry registry = getGovernanceUserRegistry();
        try {
            collection = (Collection) registry.get("/taxonomy");
        } catch (Exception e) {
            return null;
        }

        if (collection == null) {
            CollectionImpl taxonomyCollection = new CollectionImpl();
            registry.put(FULL_PATH, taxonomyCollection);
            return null;
        } else {
            if (collection.getChildCount() == 0) {
                return null;
            }

            String[] childrenList = collection.getChildren();
            String[] taxonomyNameList = new String[collection.getChildCount()];
            for (int i = 0; i < childrenList.length; i++) {
                String path = childrenList[i];
                taxonomyNameList[i] = path.substring(path.lastIndexOf(RegistryConstants.PATH_SEPARATOR) + 1);
            }
            return taxonomyNameList;
        }
    }

}
