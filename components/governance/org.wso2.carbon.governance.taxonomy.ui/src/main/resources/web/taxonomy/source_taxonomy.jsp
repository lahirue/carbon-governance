<!--
 ~ Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="carbon" uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.governance.taxonomy.ui.clients.TaxonomyManagementClient" %>

<link type="text/css" rel="stylesheet" href="css/menu.css"/>
<link type="text/css" rel="stylesheet" href="css/style.css"/>

<!--Yahoo includes for dom event handling-->
<script src="../yui/build/yahoo-dom-event/yahoo-dom-event.js" type="text/javascript"></script>

<!--EditArea javascript syntax hylighter -->
<script language="javascript" type="text/javascript" src="../editarea/edit_area_full.js"></script>

<script type="text/javascript" src="../ajax/js/prototype.js"></script>
<carbon:jsi18n
		resourceBundle="org.wso2.carbon.governance.taxonomy.ui.i18n.JSResources"
		request="<%=request%>" namespace="org.wso2.carbon.governance.taxonomy.ui"/>
<script type="text/javascript" src="js/taxonomy.js"></script>
<%
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
    String temp = "";
    boolean isNew = true;

    boolean viewMode = false;
    try{
        TaxonomyManagementClient client = new TaxonomyManagementClient(cookie, config, session);
        viewMode = request.getParameter("view") != null;

        if (request.getParameter("taxonomyName") != null) {
            temp = client.getTaxonomy(request);
            isNew = false;
        } else {
            temp = "<taxonomy id=\"SampleTaxonomy\" name=\"SampleTaxonomy\">\n" + "\n" + "\t<artifactTypes>\n"
                    + "\t<artifactType shortName=\"restservice\"> </artifactType>\n" + "\t</artifactTypes>\n" + "\n"
                    + "\t<root id=\"TaxonomyRoot\" displayName=\"TaxonomyRoot\">\n" + "\n"
                    + "\t\t<node id=\"taxonomyA1\" displayName=\"taxonomy A1\">\n"
                    + "\t\t\t<node id=\"taxonomyA2\" displayName=\"taxonomy A2\">\n"
                    + "\t\t\t\t<node id=\"taxonomyA3\" displayName=\"taxonomy A3\">\n"
                    + "\t\t\t\t<node id=\"taxonomyA4\" displayName=\"taxonomy A4\"></node>\n" + "\t\t\t</node>\n"
                    + "\t\t\t</node>\n" + "\t\t\t<node id=\"taxonomyA5\" displayName=\"taxonomy A5\"></node>\n"
                    + "\t\t\t<node id=\"taxonomyA6\" displayName=\"taxonomy A6\"></node>\n"
                    + "\t\t\t<node id=\"taxonomyA7\" displayName=\"taxonomy A7\"></node>\n"
                    + "                <node id=\"taxonomyA8\" displayName=\"taxonomy A8\"></node>\n" + "\t\t</node>\n"
                    + "\t\n" + "\t\t<node id=\"taxonomyA9\" displayName=\"taxonomy A9\">\n"
                    + "\t\t\t<node id=\"taxonomyA10\" displayName=\"taxonomy A10\">\n"
                    + "\t\t\t\t<node id=\"taxonomyA11\" displayName=\"taxonomy A11\"></node>\n" + "\t\t\t</node>\n"
                    + "\t\t</node>\n" + "\t\n" + "\t\t<node id=\"taxonomyA12\" displayName=\"taxonomy A12\"></node>\n"
                    + "\t\t<node id=\"taxonomyA3\" displayName=\"taxonomy A13\"></node>\n" + "\t</root>\n"
                    + "</taxonomy>";

        }



    } catch (Exception e){
        response.setStatus(500);
        CarbonUIMessage uiMsg = new CarbonUIMessage(CarbonUIMessage.ERROR, e.getMessage(), e);
        session.setAttribute(CarbonUIMessage.ID, uiMsg);
%>
        <jsp:include page="../admin/error.jsp?<%=e.getMessage()%>"/>
<%
        return;
    }
%>
<fmt:bundle basename="org.wso2.carbon.governance.taxonomy.ui.i18n.Resources">
<carbon:breadcrumb
        label="taxonomy.source"
        resourceBundle="org.wso2.carbon.governance.taxonomy.ui.i18n.Resources"
        topPage="false"
        request="<%=request%>"/>
<script type="text/javascript">

    function cancelSequence() {
        document.location.href = "lcm.jsp?region=region3&item=governance_lcm_menu";
    }

    YAHOO.util.Event.onDOMReady(function() {
        editAreaLoader.init({
            id : "payload"        // textarea id
            ,syntax: "xml"            // syntax to be uses for highgliting
            ,start_highlight: true        // to display with highlight mode on start-up
            ,allow_resize: "both"
            ,min_height:250
        });
    });


</script>


    <div id="middle">
        <h2><fmt:message key="taxonomy.source"/></h2>
        <div id="workArea">
            <form id="life.cycle.source.form" method="post" action="save_lcm-ajaxprocessor.jsp">
                <table class="styledLeft" cellspacing="0" cellpadding="0">
                    <thead>
                    <tr>
                        <th>
                            <span style="float: left; position: relative; margin-top: 2px;"><fmt:message key="taxonomy.source"/></span>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            <textarea id="payload" style="border: 0px solid rgb(204, 204, 204); width: 99%; height: 275px; margin-top: 5px;" name="payload" rows="30" class="codepress html linenumbers-on" wrap="off"><%=temp%></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td class="buttonRow">
                            <% if (!viewMode) { %>
                            <input class="button registryWriteOperation" type="button" onclick="saveTaxonomy('<%=request.getParameter("taxonomyName")%>', <%=Boolean.toString(isNew)%>,'false')" value="<fmt:message key="save"/>"/>
                            <input class="button registryNonWriteOperation" type="button" disabled="disabled" value="<fmt:message key="save"/>"/>
                            <input class="button" type="button" value="<fmt:message key="cancel"/>" onclick="javascript: cancelSequence(); return false;"/>
                            <% } else { %>
                            <input class="button registryWriteOperation" type="button" onclick="saveTaxonomy('<%=request.getParameter("taxonomyName")%>', <%=Boolean.toString(isNew)%>,'false')" value="<fmt:message key="save"/>"/>
                            <input class="button registryNonWriteOperation" type="button" disabled="disabled" value="<fmt:message key="save"/>"/>
                            <input class="button" type="button" value="<fmt:message key="cancel"/>" onclick="javascript: cancelSequence(); return false;"/>
                            <% } %>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</fmt:bundle>

