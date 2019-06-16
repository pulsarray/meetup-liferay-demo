package com.pulsarray.meetup.demo.portlet.action;

import java.io.IOException;
import java.util.Map;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.osgi.service.component.annotations.Component;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.pulsarray.meetup.demo.portlet.constants.DataUtil;
import com.pulsarray.meetup.demo.portlet.constants.DemoPortletKeys;

/**
 * 
 * @author thouroro
 *
 */
@Component(property = { "javax.portlet.name=" + DemoPortletKeys.Demo,
		"mvc.command.name=/sychroneTask" }, service = MVCResourceCommand.class)
public class DemoSynchroneMVCResourceCommand implements MVCResourceCommand {

	private static final Log log = LogFactoryUtil.getLog(DemoSynchroneMVCResourceCommand.class);

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {

		try {
			Map<String, Object> dataMap = DataUtil.getDataMap();
			PortletResponseUtil.sendFile(resourceRequest, resourceResponse, (String) dataMap.get(DataUtil.FILE_NAME),
					(byte[]) dataMap.get(DataUtil.FILE_CONTENT), (String) dataMap.get(DataUtil.MIME_TYPE));
		} catch (IOException e) {
			log.error(e, e);
		}

		return false;
	}

}