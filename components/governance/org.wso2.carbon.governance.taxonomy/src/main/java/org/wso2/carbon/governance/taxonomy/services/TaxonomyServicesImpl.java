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
import org.wso2.carbon.governance.taxonomy.beans.TaxonomyDocumentBean;
import org.wso2.carbon.governance.taxonomy.exception.TaxonomyException;
import org.wso2.carbon.governance.taxonomy.util.CommonUtils;
import org.wso2.carbon.registry.common.services.RegistryAbstractAdmin;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class TaxonomyServicesImpl extends RegistryAbstractAdmin implements TaxonomyServices {
    private static final Log log = LogFactory.getLog(TaxonomyServicesImpl.class);

    /**
     * This method will add user defined taxonomy into taxonomy.xml
     *
     * @param payload String
     * @return boolean
     * @throws RegistryException
     */
    public boolean addTaxonomy(String payload) throws RegistryException {
        TaxonomyManager taxonomyManager = new TaxonomyManager();
        try {
            TaxonomyDocumentBean documentBean = CommonUtils.documentBeanBuilder(payload);
            return taxonomyManager.addTaxonomy(documentBean);
        } catch (ParserConfigurationException e) {
            throw new RegistryException("Error occurred while parsing payload ", e);
        } catch (IOException e) {
            throw new RegistryException("Error occurred while building document ", e);
        } catch (SAXException e) {
            throw new RegistryException("Error occurred while creating document instance ", e);
        }
    }

    /**
     * This method will delete the taxonomy.xml file
     *
     * @return boolean
     * @throws RegistryException
     */
    public boolean deleteTaxonomy(String name) throws RegistryException {
        TaxonomyManager taxonomyManager = new TaxonomyManager();
        return taxonomyManager.deleteTaxonomy(name);
    }

    /**
     * This method will update the taxonomy.xml file with user defined data
     *
     * @param payload String
     * @return boolean
     * @throws RegistryException
     */
    public boolean updateTaxonomy(String oldName, String payload) throws RegistryException {
        TaxonomyManager taxonomyManager = new TaxonomyManager();
        try {
            TaxonomyDocumentBean documentBean = CommonUtils.documentBeanBuilder(payload);
            return taxonomyManager.updateTaxonomy(oldName, documentBean);
        } catch (ParserConfigurationException e) {
            throw new RegistryException("Error occurred while parsing payload ", e);
        } catch (IOException e) {
            throw new RegistryException("Error occurred while building document ", e);
        } catch (SAXException e) {
            throw new RegistryException("Error occurred while creating document instance ", e);
        }
    }

    /**
     * This method will retrieve the taxonomy data from taxonomy.xml
     *
     * @return String type data
     * @throws RegistryException
     */
    public String getTaxonomy(String name) throws RegistryException {
        TaxonomyManager taxonomyManager = new TaxonomyManager();
        return taxonomyManager.getTaxonomy(name);
    }

    /**
     * This method will retrieve all taxonomy file list
     *
     * @return Array of Strings which contains taxonomy file list
     * @throws RegistryException
     */
    public String[] getTaxonomyList() throws RegistryException {
        TaxonomyManager taxonomyManager = new TaxonomyManager();
        return taxonomyManager.getTaxonomyList();
    }

    /**
     * This method will return result set for user entered query
     *
     * @param query     String user entered query
     * @param startNode Integer start node
     * @param endNode   Integer start node
     * @param name      Name of the taxonomy
     * @return
     * @throws TaxonomyException
     */
    public JSONArray query(String query, int startNode, int endNode, String name) throws TaxonomyException {
        TaxonomyManager taxonomyManager = new TaxonomyManager();
        try {
            return taxonomyManager.query(query, startNode, endNode, name);
        } catch (XPathExpressionException e) {
            throw new TaxonomyException("Error occurred while compiling xpath, when processing query", e);
        } catch (JSONException e) {
            throw new TaxonomyException("Error occurred while parsing to json, when processing query ", e);
        }
    }

}
