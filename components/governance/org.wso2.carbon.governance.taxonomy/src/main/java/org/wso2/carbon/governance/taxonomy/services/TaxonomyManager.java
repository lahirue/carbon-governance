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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.NodeList;
import org.wso2.carbon.governance.taxonomy.beans.TaxonomyDocumentBean;
import org.wso2.carbon.governance.taxonomy.exception.TaxonomyException;
import org.wso2.carbon.governance.taxonomy.util.CommonUtils;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import javax.xml.xpath.XPathExpressionException;

/**
 * This is the main class which manage all taxonomy operations including map storage and registry operations
 */
class TaxonomyManager {
    private static final Log log = LogFactory.getLog(TaxonomyManager.class);

    private StorageProvider storageProvider;
    private QueryProvider queryProvider;
    private ManagementProvider managementProvider;

    /**
     * Constructor will initialize new object instances for above attributes
     */
    TaxonomyManager() {
        this.storageProvider = new StorageProviderImpl();
        this.queryProvider = new QueryProviderImpl();
        this.managementProvider = new ManagementProviderImpl();
    }

    /**
     * This method will invoke add taxonomy methods for management provider and storage provider
     *
     * @param documentBean Taxonomy document bean object
     * @return return state of the operation
     * @throws RegistryException
     */
    boolean addTaxonomy(TaxonomyDocumentBean documentBean) throws RegistryException {
        boolean isSuccessPersisting = managementProvider.addTaxonomy(documentBean.getPayload());

        storageProvider.addTaxonomy(documentBean);
        return isSuccessPersisting;
    }

    /**
     * This method will invoke delete taxonomy methods for management provider and storage provider
     *
     * @param name of the taxonomy file
     * @return return state of the operation
     * @throws RegistryException
     */
    boolean deleteTaxonomy(String name) throws RegistryException {
        boolean isSuccessDeleting = managementProvider.deleteTaxonomy(name);
        storageProvider.removeTaxonomy(name);
        return isSuccessDeleting;
    }

    /**
     * This method will invoke update taxonomy methods for management provider and storage provider
     *
     * @param oldName      String value of old name
     * @param documentBean Taxonomy meta data contained object
     * @return return state of the operation
     * @throws RegistryException
     */
    boolean updateTaxonomy(String oldName, TaxonomyDocumentBean documentBean) throws RegistryException {
        boolean isSuccessDeleting = managementProvider.updateTaxonomy(oldName, documentBean.getPayload());
        storageProvider.updateTaxonomy(oldName, documentBean);
        return isSuccessDeleting;
    }

    /**
     * This method will return text content of given taxonomy
     *
     * @param name taxonomy file name
     * @return String content of taxonomy file name
     * @throws RegistryException
     */
    String getTaxonomy(String name) throws RegistryException {
        return managementProvider.getTaxonomy(name);
    }

    /**
     * This method will return all available taxonomy file list in specific registry location
     *
     * @return String array of taxonomy file names
     * @throws RegistryException
     */
    String[] getTaxonomyList() throws RegistryException {
        return managementProvider.getTaxonomyList();
    }

    /**
     * This method will retrieve the results for rest api queries
     *
     * @param query     String user query
     * @param startNode starting node (if we want to get specific node range)
     * @param endNode   ending node (if we want to get specific node range)
     * @param name      name of the taxonomy file
     * @return Json array with processed results
     * @throws XPathExpressionException
     * @throws JSONException
     * @throws TaxonomyException
     */
    JSONArray query(String query, int startNode, int endNode, String name)
            throws XPathExpressionException, JSONException, TaxonomyException {
        NodeList nodeList = queryProvider.query(query, storageProvider.getTaxonomy(name));
        return CommonUtils.toJson(query, queryProvider.getUpdatedQuery(query), startNode, endNode, nodeList);
    }
}
