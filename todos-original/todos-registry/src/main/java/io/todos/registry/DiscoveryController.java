package io.todos.registry;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class DiscoveryController {
    @Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/registry")
	public List<String> serviceInstancesByApplicationName() {
		return this.discoveryClient.getServices();
	}

    @RequestMapping("/registry/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(
			@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

}