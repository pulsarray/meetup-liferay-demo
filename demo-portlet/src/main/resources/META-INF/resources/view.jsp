
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@ include file="/init.jsp"%>
<!-- BUILD RESOURCE URL -->
<portlet:resourceURL var="sychroneTask" id="/sychroneTask"></portlet:resourceURL>
<portlet:resourceURL var="asychroneTask" id="/asychroneTask"></portlet:resourceURL>

<!-- HEADER MESSAGES -->
<div id="succes-msg" style="display: none;">
	<clay:alert message='<%= LanguageUtil.get(resourceBundle, "demo.portlet.succes.msg") %>' title="success" style="success" />
</div>
<div id="error-msg" style="display: none;">
	<clay:alert  message='<%= LanguageUtil.get(resourceBundle, "demo.portlet.error.msg") %>' title="Error" style="danger" />
</div>


<!-- BUTTON TO RUN TASK AS SYN OR AYSYN WAY -->
<aui:button-row>
	<aui:button id="asynBtn" cssClass="btn btn-primary" type="button"
		name="demo.portlet.async.btn"
		value="demo.portlet.async.btn"></aui:button>
	<aui:button id="synBtn" cssClass="btn " type="button"
		name="demo.portlet.sync.btn"
		value="demo.portlet.sync.btn" href="<%=sychroneTask%>"></aui:button>
</aui:button-row>
<!-- WHEN USER CLICK ON ASYNC BUTTON WE SHOW SUCCES MESSAGE -->
<aui:script use="aui-io-request">
A.one('#<portlet:namespace />asynBtn').on(
		'click',
		function(event) {
			event.preventDefault();
			A.io.request(
					'<%=asychroneTask%>',
					{		
						on: {
							failure: function() {
								A.one('#error-msg').show(true);
								$("#error-msg").delay(1200).fadeOut(300);
								
							},
							success: function(event, id, obj) {
								var responseData = this.get('responseData');

								A.one('#succes-msg').show(true);
								$("#succes-msg").delay(1200).fadeOut(300);
							}
						}
					}
				);
		
		});
</aui:script>