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

import org.w3c.dom.NodeList;
import org.wso2.carbon.governance.taxonomy.beans.TaxonomyDocumentBean;
import java.util.List;
import javax.xml.xpath.*;

/**
 * This class will implements the methods for query operations
 */
class QueryProviderImpl implements QueryProvider {
    /**
     * This method will return list of nodes for a given query
     *
     * @param query rest api query that front end will provided
     * @param documentBean Taxonomy bean object which contains taxonomy meta data
     * @return NodeList (XML) for specific taxonomy file
     * @throws XPathExpressionException
     */
    @Override
    public NodeList query(String query, TaxonomyDocumentBean documentBean)
            throws XPathExpressionException {
        XPath xPathInstance = XPathFactory.newInstance().newXPath();
        XPathExpression exp = xPathInstance.compile(getUpdatedQuery(query));
        return (NodeList) exp.evaluate(documentBean.getDocument(), XPathConstants.NODESET);
    }

    /**
     * This method will update the given query to xpath compilable query
     *
     * @param query String front end rest api provided query
     * @return String update query
     */
    @Override
    public String getUpdatedQuery(String query) {
        StringBuilder updatedStringBuilder = new StringBuilder();
        // this will execute long queries with /children
        if (query.contains("/") && !query.contains("/*")) {
             String[] listIds = query.split("/");
            updatedStringBuilder.append("/taxonomy/root[@id='").append(listIds[0]).append("']");
            for (int ids = 1; ids < listIds.length; ids++) {
                if (!(ids == listIds.length - 1 && listIds[ids].equals("children"))) {
                    updatedStringBuilder.append("/node[@id='").append(listIds[ids]).append("']");
                }
            }
            if (query.contains("children")) {
                updatedStringBuilder.append("/*");
            }

        } else {
            updatedStringBuilder.append("/taxonomy/root/*");
        }
        return updatedStringBuilder.toString();
    }

    @Override
    public List<String> getTaxonomiesByRXT(String artifactType) {
        return null;
    }
}
