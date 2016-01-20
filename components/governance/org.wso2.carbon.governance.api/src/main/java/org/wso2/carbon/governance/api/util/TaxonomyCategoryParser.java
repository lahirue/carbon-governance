/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.governance.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.internal.RegistryCoreServiceComponent;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/***
 * This class is use to populate category list from admin defined XML document
 */
public class TaxonomyCategoryParser {
    /**
     * Log variable use to log.
     */
    private static final Log log = LogFactory.getLog(TaxonomyCategoryParser.class);
    private static List<String> categories = new ArrayList<String>();
    private static Stack<String> elementStack = new Stack<String>();

    /***
     * This method is use to generate the path from the stack of element names
     */
    private static void addPathToCategories() {
        StringBuilder path = new StringBuilder();
        for (String ele : elementStack) {
            path.append("/").append(ele);
        }
        categories.add(path.toString()); //add generated path into a list
    }

    /***
     * This method is use to go through xml DOM and push the elements names into a stack
     *
     * @param childNodes
     */
    private static void loopNodes(NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node node = childNodes.item(i);
            String nodeName = node.getNodeName();
            //if xml document contains line breaks
            if (!"#text".equals(nodeName)) {
                elementStack.push(nodeName);
                if (node.hasChildNodes()) {
                    loopNodes(node.getChildNodes());

                } else {
                    addPathToCategories();
                }

                if (elementStack.size() > 0) {
                    elementStack.pop();
                }

            }
        }
    }

    /***
     * This method is use to populate the paths from XML document
     *
     * @return List of Strings
     */
    public static List getPathCategories() throws RegistryException {
        Registry registry = RegistryCoreServiceComponent.getRegistryService().getGovernanceSystemRegistry();
        List<String> tempCategories = new ArrayList<String>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(registry.get("/_system/input.xml").getContentStream());
            NodeList childNodes = doc.getChildNodes();
            loopNodes(childNodes);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Error occur while parsing the xml document ", e);
        }
        tempCategories.addAll(categories);
        categories.clear();
        return tempCategories;
    }

}


