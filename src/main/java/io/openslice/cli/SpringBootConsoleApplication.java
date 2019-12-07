package io.openslice.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static java.util.Arrays.asList;

import io.openslice.tmf.common.model.service.Note;
import io.openslice.tmf.common.model.service.ServiceSpecificationRef;
import io.openslice.tmf.scm633.model.ServiceCatalog;
import io.openslice.tmf.scm633.model.ServiceSpecification;
import io.openslice.tmf.so641.model.ServiceOrder;
import io.openslice.tmf.so641.model.ServiceOrderCreate;
import io.openslice.tmf.so641.model.ServiceOrderItem;
import io.openslice.tmf.so641.model.ServiceRestriction;

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

	private ServiceSpecification specToOrder;

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
			System.out.printf(  "| %s | %s \n", s.getId(), s.getName() );
		});

		ResponseEntity<ServiceSpecification[]> response2 = restTemplate.getForEntity(
				"http://localhost:13082/tmf-api/serviceCatalogManagement/v4/serviceSpecification",
				ServiceSpecification[].class);

		ServiceSpecification[] sp = response2.getBody();

		System.out.printf(  "--------------SERVICE SPECS------------------ \n" );
		asList(sp).forEach((s) -> {
			System.out.printf(  "| %s | %s \n", s.getId(), s.getName() );
			if ( s.getName().equals("A VINNI Service Example")) {
				specToOrder = s;
			}
		});

		ServiceOrderCreate servOrder = new ServiceOrderCreate();
		Note noteItem = new Note();
		noteItem.text("test note");
		servOrder.addNoteItem(noteItem);
		
		ServiceOrderItem soi = new ServiceOrderItem();
		servOrder.getOrderItem().add(soi);
		
		ServiceRestriction serviceRestriction = new ServiceRestriction();
		ServiceSpecificationRef aServiceSpecificationRef = new ServiceSpecificationRef();
		aServiceSpecificationRef.setId( specToOrder.getId() );
		
		serviceRestriction.setServiceSpecification(aServiceSpecificationRef );
		soi.setService(serviceRestriction );
		
		HttpEntity<ServiceOrderCreate> request = new HttpEntity<>( servOrder );
		ResponseEntity<ServiceOrder> responseOrder = restTemplate.postForEntity(
				"http://localhost:13082/tmf-api/serviceOrdering/v4/serviceOrder",
				request,
				ServiceOrder.class);

		ServiceOrder sor = responseOrder.getBody();

		System.out.printf(  "--------------SERVICE ORDER------------------ \n" );
		System.out.printf(  "| %s | %s \n", sor.getId(), sor.getOrderItem().toString() );
		
	}
}