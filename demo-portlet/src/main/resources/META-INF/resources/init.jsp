<%@page import="com.liferay.portal.kernel.util.JavaConstants"%>
<%@page import="javax.portlet.PortletConfig"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%><%@
taglib
	uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%><%@
taglib
	uri="http://liferay.com/tld/theme" prefix="liferay-theme"%><%@
taglib
	uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@
taglib uri="http://liferay.com/tld/clay" prefix="clay"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@
page import="java.util.ResourceBundle"%>
<%@
page import="com.liferay.portal.kernel.util.ResourceBundleUtil"%>
<%@
page import="com.liferay.taglib.util.TagResourceBundleUtil"%>


<liferay-theme:defineObjects />

<portlet:defineObjects />
<%
ResourceBundle resourceBundle = TagResourceBundleUtil.getResourceBundle(request, locale);

pageContext.setAttribute("resourceBundle", resourceBundle);

%>

