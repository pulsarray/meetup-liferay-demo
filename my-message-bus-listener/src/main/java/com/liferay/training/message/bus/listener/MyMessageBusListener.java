package com.liferay.training.message.bus.listener;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.pulsarray.meetup.demo.portlet.constants.DataUtil;

/**
 * @author thouroro
 * 
 */
@Component(immediate = true, property = { "destination.name=ASYNCHRONE_TASK" }, service = MessageListener.class)
public class MyMessageBusListener implements MessageListener {

	private static final Log log = LogFactoryUtil.getLog(MyMessageBusListener.class);

	@Override
	public void receive(Message message) throws MessageListenerException {

		// GET PARAMS FROM MESSAGE OBJECT
		long userId = (long) message.get("userId");
		long groupId = (long) message.get("groupId");
		ServiceContext serviceContext = (ServiceContext) message.get("serviceContext");
		String downloadUrl = (String) message.get("downloadUrl");
		User user = null;
		String notificationText=(String)message.get("notificationText");
		String notificationBody=(String)message.get("notificationBody");
		try {
			// FETCH USER BY USERID
			if (userId > 0) {
				user = userLocalService.fetchUser(userId);
			} else {
				user = userLocalService.getDefaultUser(serviceContext.getCompanyId());
			}

			// GET DATA AS BYTE[]
			byte[] tBytes = DataUtil.getData();

			File file = FileUtil.createTempFile(tBytes);
			// CREATE FOLDER IN DL
			DLFolder folder = createFolder(groupId, serviceContext);
			// CREATE FILE ENTRY IN DL
			FileEntry fileEntry = createFileEntry(file, tBytes, groupId, serviceContext, folder);
			// URL TO DOWNLOAD FILE
			downloadUrl += fileEntry.getFileEntryId() + "/" + fileEntry.getFileName() + "/" + fileEntry.getUuid() + "";
			// CREATE JSON PLAYLOAD FILE
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
			jsonObject.put("notificationBody", notificationBody);
			jsonObject.put("notificationText", notificationText);
			jsonObject.put("senderName", user.getFullName());
			jsonObject.put("downloadUrl", downloadUrl);
			// SEND NOTIFICATION
			userNotificationEventLocalService.addUserNotificationEvent(userId,
					"com_liferay_training_custom_notification", (new Date()).getTime(),
					UserNotificationDeliveryConstants.TYPE_WEBSITE, userId, jsonObject.toJSONString(), false,
					serviceContext);

		} catch (IOException | PortalException e1) {
			log.error(e1, e1);
		}

	}

	/**
	 * CREATE FOLDER IN LIFERAY DL
	 * 
	 * @param globalGroupId
	 * @param serviceContext
	 * @return
	 * @throws PortalException
	 */
	private DLFolder createFolder(long globalGroupId, ServiceContext serviceContext) throws PortalException {
		DLFolder folder = dLFolderLocalService.fetchFolder(globalGroupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				"EXPORT_FILES_FOLDER");
		if (folder == null) {
			folder = dLFolderLocalService.addFolder(serviceContext.getUserId(), globalGroupId,
					serviceContext.getScopeGroupId(), false, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					"EXPORT_FILES_FOLDER", "EXPORT_FILES_FOLDER", false, serviceContext);
		}
		return folder;
	}

	/**
	 * CREATE FILE IN LIFERAY DL
	 * 
	 * @param file
	 * @param tBytes
	 * @param globalGroupId
	 * @param serviceContext
	 * @param folder
	 * @return
	 * @throws PortalException
	 */
	private FileEntry createFileEntry(File file, byte[] tBytes, long globalGroupId, ServiceContext serviceContext,
			DLFolder folder) throws PortalException {
		String fileName = "demo" + System.currentTimeMillis() + ".xls";
		String contentType = MimeTypesUtil.getContentType(file);
		FileEntry fileEntry = dLAppLocalService.addFileEntry(serviceContext.getUserId(), globalGroupId,
				folder.getFolderId(), fileName, contentType, fileName, fileName, "", tBytes, serviceContext);
		return fileEntry;
	}

	@Reference(unbind = "-")
	public void setUserNotificationEventLocalService(
			UserNotificationEventLocalService userNotificationEventLocalService) {
		this.userNotificationEventLocalService = userNotificationEventLocalService;
	}

	private UserNotificationEventLocalService userNotificationEventLocalService;

	@Reference(unbind = "-")
	public void setDLFolderLocalService(DLFolderLocalService dLFolderLocalService) {
		this.dLFolderLocalService = dLFolderLocalService;
	}

	private DLFolderLocalService dLFolderLocalService;

	@Reference(unbind = "-")
	public void setDLAppLocalService(DLAppLocalService dLAppLocalService) {
		this.dLAppLocalService = dLAppLocalService;
	}

	private DLAppLocalService dLAppLocalService;

	@Reference(unbind = "-")
	public void setUserLocalService(UserLocalService userLocalService) {
		this.userLocalService = userLocalService;
	}

	private UserLocalService userLocalService;

}