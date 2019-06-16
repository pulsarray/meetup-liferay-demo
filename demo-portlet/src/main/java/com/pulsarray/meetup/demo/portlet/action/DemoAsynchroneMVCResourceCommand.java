package com.pulsarray.meetup.demo.portlet.action;

import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.taglibs.standard.lang.jstl.test.PageContextImpl;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.pulsarray.meetup.demo.portlet.constants.DemoPortletKeys;

/**
 * 
 * @author thouroro
 *
 */
@Component(property = { "javax.portlet.name=" + DemoPortletKeys.Demo,
		"mvc.command.name=/asychroneTask" }, service = MVCResourceCommand.class)
public class DemoAsynchroneMVCResourceCommand implements MVCResourceCommand {

	private static final Log log = LogFactoryUtil.getLog(DemoAsynchroneMVCResourceCommand.class);

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		try {

			ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
			PortletConfig portletConfig = (PortletConfig) resourceRequest
					.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
			ResourceBundle resourceBundle = portletConfig.getResourceBundle(themeDisplay.getLocale());
			// BUILD MSG AND SEND IT TO DESTINATION
			Message message = new Message();
			message.put("userId", themeDisplay.getUserId());
			message.put("groupId", themeDisplay.getCompanyGroupId());
			message.put("themeDisplay", themeDisplay);
			message.put("downloadUrl", themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/"
					+ themeDisplay.getCompanyGroupId() + "/");

			message.put("notificationText", LanguageUtil.get(resourceBundle, "demo.portlet.notification.title"));
			message.put("notificationBody", LanguageUtil.get(resourceBundle, "demo.portlet.notification.body"));
			message.put("serviceContext", ServiceContextFactory.getInstance(resourceRequest));
			_messageBus.sendMessage("ASYNCHRONE_TASK", message);

		} catch (PortalException | SystemException e) {
			log.error(e, e);

		}
		return false;
	}

	@Reference
	private MessageBus _messageBus;
}