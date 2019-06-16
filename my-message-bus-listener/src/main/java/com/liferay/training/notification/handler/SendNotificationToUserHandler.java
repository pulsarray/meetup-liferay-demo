package com.liferay.training.notification.handler;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringUtil;

@Component(service = UserNotificationHandler.class)
public class SendNotificationToUserHandler extends BaseUserNotificationHandler {
	public static String PORTLET_ID = "com_liferay_training_message_bus_listener";

	public SendNotificationToUserHandler() {
		setPortletId(PORTLET_ID);
	}

	@Override
	protected String getBody(UserNotificationEvent userNotificationEvent, ServiceContext serviceContext)
			throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(userNotificationEvent.getPayload());
		String notificationText = jsonObject.getString("notificationText");
		String title = jsonObject.getString("title");
		String senderName = jsonObject.getString("senderName");
		String body = StringUtil.replace(getBodyTemplate(),
				new String[] { "[$TITLE$]", "[$SENDER_NAME$]", "[$NOTIFICATION_TEXT$]" },
				new String[] { title, senderName, notificationText });

		return body;
	}

	protected String getBodyTemplate() throws Exception {
		StringBuilder htmlResponse = new StringBuilder(5);
		htmlResponse.append("<div class=\"title\">Title::[$TITLE$]<div><div ");
		htmlResponse.append("class=\"body\">Sender::[$SENDER_NAME$]<br>Notification::[$NOTIFICATION_TEXT$]</div> ");
		return htmlResponse.toString();
	}

}
