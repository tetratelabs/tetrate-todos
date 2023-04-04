package io.todos.todosedge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TodosEdgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodosEdgeApplication.class, args);
	}

}
