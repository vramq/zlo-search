<%@ include file="WEB-INF/jsp/import.jsp" %>
<%@ page contentType="text/html; charset=windows-1251" %>
<link rel="stylesheet" type="text/css" href="main.css" />

<c:set var="localIps"><fmt:message key="history.localIps" /></c:set>
<c:set var="isLocalIp" value="<%= RequestUtils.isLocalIp(request, (String) pageContext.getAttribute("localIps")) %>" />
<c:set var="showAll" value="${ param['all'] != null and isLocalIp }" />

<sql:setDataSource dataSource="<%= DbAccessor.getInstance("search_log").getDataSource() %>" />

<c:set var="lastHours" value="6" />
<c:if test="${isLocalIp and not empty param['n']}">
    <c:set var="lastHours" value="${param['n']}" />
</c:if>

<sql:query var="totalNum">
    SELECT MAX(id) last FROM request_log
</sql:query>

<c:choose>
    <c:when test="${showAll}">
        <sql:query var="res">
            SELECT * FROM request_log
            WHERE req_date > NOW() - INTERVAL ? HOUR
            order by id DESC
            <sql:param value="${lastHours}" />
        </sql:query>
    </c:when>
    <c:otherwise>
        <sql:query var="res">
            SELECT * FROM request_log
            WHERE host not in ${xonix:mysqlRange(localIps)}
            AND is_rss_req = 0
            AND req_date > NOW() - INTERVAL ? HOUR
            order by id DESC
            <sql:param value="${lastHours}" />
        </sql:query>
    </c:otherwise>
</c:choose>

<title>������� ��������</title>

<tiles:insertDefinition name="header.history" />

<div align="center" class="content">
    <h3>������� ��������
            <c:if test="${isLocalIp}">
                <c:choose>
                    <c:when test="${!showAll}"><a href="history.jsp?all" class="search">(��������)</a></c:when>
                    <c:otherwise><a href="history.jsp" class="search">(������)</a></c:otherwise>    
                </c:choose>
            </c:if>
    </h3>
    <small>(����� ��������: ${totalNum.rows[0].last}, �������� ${res.rowCount} ${
        xonix:plural(res.rowCount, '������', '�������', '��������')}, �� ��������� ${lastHours} ${
        xonix:plural(lastHours, '���', '����', '�����')})</small>

    <display:table name="${res.rows}" id="row" htmlId="resultTable" decorator="info.xonix.zlo.web.decorators.HistoryTableDecorator">
        <display:column title="�">
            <a href="search?<c:out value="${row.req_query_str}"/>" class="search">${row_rowNum}</a>
        </display:column>
        <display:column property="searchText" title="�����" />
        <display:column property="searchNick" title="��� ������" />
        <display:column property="searchHost" title="���� ������" />
        <display:column title="����">
            <% Site site = Site.getSite((Integer)((TreeMap)row).get("site")); %>
            <a href="http://<%= site.getSITE_URL() %>">
                <%= site.getName() %></a>
        </display:column>
        <display:column property="reqDate" title="����" class="small" />
        <c:if test="${showAll}">
            <display:column property="host" title="����" class="small" />
            <display:column property="user_agent" title="User-Agent" class="small" />
        </c:if>
        <display:column property="userAgentSmall" title="�������" class="small center" />
    </display:table>
</div>

<tiles:insertDefinition name="ga" />