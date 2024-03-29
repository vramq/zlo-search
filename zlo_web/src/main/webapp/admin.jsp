<%@ page import="info.xonix.utils.daemon.DaemonManager" %>
<%@ page import="info.xonix.zlo.search.config.forums.GetForum" %>
<%@ page import="info.xonix.zlo.search.index.IndexManager" %>
<%@ page import="info.xonix.zlo.search.utils.SysUtils" %>
<%@ page import="info.xonix.zlo.web.logic.AdminLogic" %>
<%@ page import="info.xonix.zlo.search.logic.AppLogic" %>
<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ include file="WEB-INF/jsp/restrictAccess.jsp" %>

<%@ include file="WEB-INF/jsp/commonJsCss.jsp" %>
<link rel="stylesheet" type="text/css" href="css/admin.css?${version}"/>

<%!
    private static final DaemonManager daemonManager = AppSpringContext.get(DaemonManager.class);
    private static final AppLogic appLogic = AppSpringContext.get(AppLogic.class);
%>

<title>Admin area</title>

<tiles:insertDefinition name="header.admin"/>

<div class="content">
    <%
        request.setAttribute("mb", AdminLogic.MEGABYTE);
    %>

    <form action="admin.jsp" method="post">
        <%--<input type="submit" name="command" value="Optimize"/>--%>
        <input type="submit" name="command" value="GC"/>
    </form>
    
    <a href="detectspam.jsp">Spam?</a>
    <a href="db.jsp">DB</a>

    <%
        if ("POST".equals(request.getMethod())) {
            final String command = request.getParameter("command");
            if ("GC".equals(command)) {
                SysUtils.gc();
            } else if ("Reindex".equals(command)) {
                AdminLogic.reindex(request.getParameter("forumId"));
            }
            response.sendRedirect("admin.jsp");
            return;
        }
    %>

    <table>
        <tr>
            <th>Memory usage</th>
            <th>Versions</th>
        </tr>
        <tr>
            <td align="left" valign="top">
                <display:table id="line" name="<%= AdminLogic.memoryReport().entrySet() %>">
                    <display:setProperty name="css.table">reportTbl</display:setProperty>

                    <display:column property="key"/>
                    <display:column property="value" format="{0,number,#.##}"/>
                </display:table>
            </td>
            <td>
                <display:table name="<%= AdminLogic.versionsReport(application).entrySet()%>" class="reportTbl">
                    <display:column property="key"/>
                    <display:column property="value"/>
                </display:table>
            </td>
        </tr>
    </table>

    <br/>
    <display:table id="daemon" name="<%= daemonManager.listDaemons() %>">
        <display:caption>Daemons</display:caption>

        <display:column title="#">${daemon_rowNum}</display:column>
        <display:column title="Forum">${daemon.id}</display:column>
        <display:column title="Type">
            <%= daemon.getClass().getSimpleName() %>
        </display:column>
        <display:column title="State">${daemon.daemonState}</display:column>
        <display:column title="Last Exception">
            <c:if test="${not empty daemon.lastException}">
                ${daemon.lastException.message} (<fmt:formatDate value="${daemon.lastExceptionTime}" pattern="dd.MM.yy hh:mm"/>)
            </c:if>
        </display:column>
    </display:table>

    <br/>
    <display:table id="forumId" name="<%= GetForum.ids() %>">
        <c:set var="adapter" value="<%= GetForum.adapter((String) forumId) %>"/>
        <c:set var="indexManager" value="<%= IndexManager.get((String) forumId) %>"/>

        <display:caption>Forums</display:caption>

        <display:column title="#">${forumId_rowNum}</display:column>
        <display:column title="Name">${forumId}</display:column>
        <display:column title="Url"><a href="${adapter.forumUrl}">${adapter.forumUrl}</a></display:column>
        <display:column title="Index">${indexManager.indexDir}</display:column>
        <display:column title="Index Size">
            <fmt:formatNumber value="${indexManager.indexSize / mb}" pattern="#.##"/>
        </display:column>
        <display:column title="Last Saved">
            <%= appLogic.getLastSavedMessageNumber((String) forumId) %>
        </display:column>
        <display:column title="Last Indexed">
            <%= appLogic.getLastIndexedNumber((String) forumId) %>
        </display:column>
        <display:column>
            <form action="admin.jsp" method="post" onclick="return confirm('Are you sure want to reindex ${forumId}?')">
                <input type="hidden" name="forumId" value="${forumId}">
                <input type="submit" name="command" value="Reindex"/>
            </form>
        </display:column>
    </display:table>
</div>