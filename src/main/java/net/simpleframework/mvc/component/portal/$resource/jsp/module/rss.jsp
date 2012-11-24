<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.component.portal.module.RssUtils"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%
	final PageRequestResponse rRequest = PageRequestResponse.get(
			request, response);
	final String id = "rss_" + StringUtils.hash(rRequest);
%>
<div id="<%=id%>" class="rss_">
  <%
  	out.write(RssUtils.rssRender(rRequest));
  %>
</div>
<script type="text/javascript">
  $ready(function() {
    $Actions["ajaxRssTooltip"].createTip("#<%=id%> .rss a");
  });
</script>