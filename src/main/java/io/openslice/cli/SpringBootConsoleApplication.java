package io.openslice.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static java.util.Arrays.asList;

import io.openslice.tmf.scm633.model.ServiceCatalog;
import io.openslice.tmf.scm633.model.ServiceSpecification;

/**
 * 1. Act as main class for spring boot application 2. Also implements
 * CommandLineRunner, so that code within run method is executed before
 * application startup but after all beans are effectively created
 * 
 *
 */
@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		ConfigurableApplicationContext ctx = SpringApplication.run(SpringBootConsoleApplication.class, args);
		LOG.info("APPLICATION FINISHED");
		ctx.close();
	}

	/**
	 * This method will be executed after the application context is loaded and
	 * right before the Spring Application main method is completed.
	 */
	@Override
	public void run(String... args) throws Exception {
		LOG.info("EXECUTING : command line runner");
		for (int i = 0; i < args.length; ++i) {
			LOG.info("args[{}]: {}", i, args[i]);
		}

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ServiceCatalog[]> response = restTemplate.getForEntity(
				"http://localhost:13082/tmf-api/serviceCatalogManagement/v4/serviceCatalog", ServiceCatalog[].class);

		ServiceCatalog[] sc = response.getBody();

		//asList(sc).forEach(System.out::println);
		asList(sc).forEach((s) -> {
			System.out.println( s.getName());
		});

		ResponseEntity<ServiceSpecification[]> response2 = restTemplate.getForEntity(
				"http://localhost:13082/tmf-api/serviceCatalogManagement/v4/serviceSpecification",
				ServiceSpecification[].class);

		ServiceSpecification[] sp = response2.getBody();

		asList(sp).forEach((s) -> {
			System.out.println( s.getName());
		});

	}
}