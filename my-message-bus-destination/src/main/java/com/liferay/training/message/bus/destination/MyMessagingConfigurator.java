package com.liferay.training.message.bus.destination;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.concurrent.CallerRunsPolicy;
import com.liferay.portal.kernel.concurrent.RejectedExecutionHandler;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;

@Component (
	    immediate = true,
	    service = MyMessagingConfigurator .class
	)
	public class MyMessagingConfigurator {

	    private static final String ASYNCHRONE_TASK = "ASYNCHRONE_TASK";

		@Activate
	    protected void activate(BundleContext bundleContext) {

	        _bundleContext = bundleContext;

	        // Create a DestinationConfiguration for parallel destinations.

	        DestinationConfiguration destinationConfiguration =
	            new DestinationConfiguration(
	                DestinationConfiguration.DESTINATION_TYPE_SERIAL,
	                    ASYNCHRONE_TASK);

	        // Set the DestinationConfiguration's max queue size and
	        // rejected execution handler.

	        destinationConfiguration.setMaximumQueueSize(_MAXIMUM_QUEUE_SIZE);

	        RejectedExecutionHandler rejectedExecutionHandler =
	            new CallerRunsPolicy() {

	                @Override
	                public void rejectedExecution(
	                    Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

	                    if (_log.isWarnEnabled()) {
	                        _log.warn(
	                            "The current thread will handle the request " +
	                                "because the graph walker's task queue is at " +
	                                    "its maximum capacity");
	                    }

	                    super.rejectedExecution(runnable, threadPoolExecutor);
	                }

	            };

	       destinationConfiguration.setRejectedExecutionHandler(
	            rejectedExecutionHandler);

	       // Create the destination

	       Destination destination = _destinationFactory.createDestination(
	            destinationConfiguration);

	        // Add the destination to the OSGi service registry

	        Dictionary<String, Object> properties = new HashMapDictionary<>();

	        properties.put("destination.name", destination.getName());

	        ServiceRegistration<Destination> serviceRegistration =
	            _bundleContext.registerService(
	                Destination.class, destination, properties);

	        // Track references to the destination service registrations 

	        _serviceRegistrations.put(destination.getName(),    
	            serviceRegistration);
	    }

	    @Deactivate
	    protected void deactivate() {

	        // Unregister and destroy destinations this component unregistered

	        for (ServiceRegistration<Destination> serviceRegistration : 
	        _serviceRegistrations.values()) {

	            Destination destination = _bundleContext.getService(
	                serviceRegistration.getReference());

	            serviceRegistration.unregister();

	            destination.destroy();

	        }

	        _serviceRegistrations.clear();

	     }

	    @Reference
	    private DestinationFactory _destinationFactory;
	    private BundleContext _bundleContext;
	    private static final Log _log = LogFactoryUtil.getLog(
	    		MyMessagingConfigurator.class);
	    private static final int _MAXIMUM_QUEUE_SIZE = 20;

	    private final Map<String, ServiceRegistration<Destination>>
	        _serviceRegistrations = new HashMap<>();
	}