package com.liferay.training.notification.handler;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;
/**
 * NOTIFICATION HANDLER : HTML MSG FORMAT 
 * @author thouroro
 *
 */
@Component(service = UserNotificationHandler.class)
public class SendNotificationToUserHandler extends BaseUserNotificationHandler {
	public static String PORTLET_ID = "com_liferay_training_custom_notification";

	public SendNotificationToUserHandler() {
		setPortletId(PORTLET_ID);
	}

	@Override
	protected String getBody(UserNotificationEvent userNotificationEvent, ServiceContext serviceContext)
			throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(userNotificationEvent.getPayload());
		String notificationText = jsonObject.getString("notificationBody");
		String title = jsonObject.getString("notificationText");
		String senderName = jsonObject.getString("senderName");
		String downloadUrl = jsonObject.getString("downloadUrl");
		
		String body = StringUtil.replace(getBodyTemplate(),
				new String[] { "[$TITLE$]", "[$SENDER_NAME$]", "[$NOTIFICATION_TEXT$]","[$DOWNLOAD_URL$]" },
				new String[] { title, senderName, notificationText ,downloadUrl});

		return body;
	}

	protected String getBodyTemplate() throws Exception {
		StringBuilder htmlResponse = new StringBuilder(5);
		htmlResponse.append("<div class=\"title\"><h6>[$TITLE$](<i>[$SENDER_NAME$]</i>)</h6><div><div ");
		htmlResponse.append("class=\"body\"><br><p>[$NOTIFICATION_TEXT$]</p><a href=\"[$DOWNLOAD_URL$]\" style=\"color:blue;\">( Download file here )</a></div> ");
		return htmlResponse.toString();
	}

}
