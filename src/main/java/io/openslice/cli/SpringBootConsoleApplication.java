package io.openslice.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsProperties.AcknowledgeMode;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import static java.util.Arrays.asList;

import java.util.ArrayList;

import io.openslice.tmf.common.model.service.Note;
import io.openslice.tmf.common.model.service.ServiceSpecificationRef;
import io.openslice.tmf.scm633.model.ServiceCatalog;
import io.openslice.tmf.scm633.model.ServiceSpecification;
import io.openslice.tmf.so641.model.ServiceOrder;
import io.openslice.tmf.so641.model.ServiceOrderCreate;
import io.openslice.tmf.so641.model.ServiceOrderItem;
import io.openslice.tmf.so641.model.ServiceOrderStateType;
import io.openslice.tmf.so641.model.ServiceOrderUpdate;
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
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(); //this is used for pach with apache http, see pom
//		restTemplate.setRequestFactory(requestFactory);
//		
//		ResponseEntity<ServiceCatalog[]> response = restTemplate.getForEntity(
//				"http://localhost:13082/tmf-api/serviceCatalogManagement/v4/serviceCatalog", ServiceCatalog[].class);
//
//		ServiceCatalog[] sc = response.getBody();
//
//		//asList(sc).forEach(System.out::println);
//		asList(sc).forEach((s) -> {
//			System.out.printf(  "| %s | %s \n", s.getId(), s.getName() );
//		});
//
//		ResponseEntity<ServiceSpecification[]> response2 = restTemplate.getForEntity(
//				"http://localhost:13082/tmf-api/serviceCatalogManagement/v4/serviceSpecification",
//				ServiceSpecification[].class);
//
//		ServiceSpecification[] sp = response2.getBody();
//
//		System.out.printf(  "--------------SERVICE SPECS------------------ \n" );
//		System.out.printf(  "| %40s | %40s \n", "id/uuid", "service spec name" );
//		asList(sp).forEach((s) -> {
//			System.out.printf(  "| %40s | 40s \n", s.getId(), s.getName() );
//			if ( s.getName().equals("A VINNI Service Example")) {
//				specToOrder = s;
//			}
//		});
//
//		/**
//		 * create service order
//		 */
//		ServiceOrderCreate servOrderCre = new ServiceOrderCreate();
//		Note noteItemC = new Note();
//		noteItemC.text("test note");
//		servOrderCre.addNoteItem(noteItemC);
//		
//		ServiceOrderItem soi = new ServiceOrderItem();
//		servOrderCre.getOrderItem().add(soi);
//		
//		ServiceRestriction serviceRestriction = new ServiceRestriction();
//		ServiceSpecificationRef aServiceSpecificationRef = new ServiceSpecificationRef();
//		aServiceSpecificationRef.setId( specToOrder.getId() );
//		
//		serviceRestriction.setServiceSpecification(aServiceSpecificationRef );
//		soi.setService(serviceRestriction );
//		
//		HttpEntity<ServiceOrderCreate> request = new HttpEntity<>( servOrderCre );
//		ResponseEntity<ServiceOrder> responseOrder = restTemplate.postForEntity(
//				"http://localhost:13082/tmf-api/serviceOrdering/v4/serviceOrder",
//				request,
//				ServiceOrder.class);
//
//		//ServiceOrder sor = responseOrder.getBody();

		ResponseEntity<ServiceOrder[]> responseServiceOrderList = restTemplate.getForEntity(
				"http://localhost:13082/tmf-api/serviceOrdering/v4/serviceOrder",
				ServiceOrder[].class);

		ServiceOrder sor[] = responseServiceOrderList.getBody();
		System.out.printf(  "--------------SERVICE ORDER------------------ \n" );
		System.out.printf(  "| %40s | %40s | %40s \n", "id/uuid", "service spec id", "status" );
		asList(sor).forEach( (s) -> {
			System.out.printf(  "| %40s | %40s \n", s.getId(),  (new ArrayList<>(s.getOrderItem())).get(0).getService().getServiceSpecification().getId(), s.getState()  );
			if ( s.getState().equals( ServiceOrderStateType.INITIAL ) ) {
				ServiceOrderUpdate servOrder = new ServiceOrderUpdate();
				servOrder.setState( ServiceOrderStateType.ACKNOWLEDGED );
				Note noteItem = new Note();
				noteItem.text("Order accepted");
				servOrder.addNoteItem(noteItem);
				HttpEntity<ServiceOrderUpdate> requestSo = new HttpEntity<>( servOrder );
				ServiceOrder responseSoOrder = restTemplate.patchForObject(
						"http://localhost:13082/tmf-api/serviceOrdering/v4/serviceOrder/" + s.getId(),
						requestSo,
						ServiceOrder.class);
			}
		});
		
		responseServiceOrderList = restTemplate.getForEntity(
				"http://localhost:13082/tmf-api/serviceOrdering/v4/serviceOrder",
				ServiceOrder[].class);

		sor = responseServiceOrderList.getBody();
		System.out.printf(  "--------------SERVICE ORDER------------------ \n" );
		System.out.printf(  "| %40s | %40s | %40s \n", "id/uuid", "service spec id", "status" );
		asList(sor).forEach( (s) -> {
			System.out.printf(  "| %40s | %40s \n", s.getId(),  (new ArrayList<>(s.getOrderItem())).get(0).getService().getServiceSpecification().getId(), s.getState() );
		});
		
	}
}