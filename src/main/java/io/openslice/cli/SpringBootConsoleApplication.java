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
import io.openslice.tmf.common.model.service.ServiceRef;
import io.openslice.tmf.common.model.service.ServiceSpecificationRef;
import io.openslice.tmf.scm633.model.ServiceCatalog;
import io.openslice.tmf.scm633.model.ServiceSpecification;
import io.openslice.tmf.sim638.model.Service;
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

	private static final String HOST = "portal.openslice.io";// "localhost:13082";
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
//				"http://"+HOST+"/tmf-api/serviceCatalogManagement/v4/serviceCatalog", ServiceCatalog[].class);
//
//		ServiceCatalog[] sc = response.getBody();
//
//		//asList(sc).forEach(System.out::println);
//		asList(sc).forEach((s) -> {
//			System.out.printf(  "| %s | %s \n", s.getId(), s.getName() );
//		});
//
//		ResponseEntity<ServiceSpecification[]> response2 = restTemplate.getForEntity(
//				"http://"+HOST+"/tmf-api/serviceCatalogManagement/v4/serviceSpecification",
//				ServiceSpecification[].class);
//
//		ServiceSpecification[] sp = response2.getBody();
//
//		System.out.printf(  "--------------SERVICE SPECS------------------ \n" );
//		System.out.printf(  "| %40s | %40s \n", "id/uuid", "service spec name" );
//		asList(sp).forEach((s) -> {
//			System.out.printf(  "| %40s | %40s \n", s.getId(), s.getName() );
//			if ( s.getName().equals("A VINNI Service Example")) {
//				specToOrder = s;
//			}
//		});
////
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
//				"http://"+HOST+"/tmf-api/serviceOrdering/v4/serviceOrder",
//				request,
//				ServiceOrder.class);
//
//
//		ResponseEntity<ServiceOrder[]> responseServiceOrderList = restTemplate.getForEntity(
//				"http://"+HOST+"/tmf-api/serviceOrdering/v4/serviceOrder",
//				ServiceOrder[].class);
//
//		ServiceOrder sor[] = responseServiceOrderList.getBody();
//		System.out.printf(  "--------------SERVICE ORDER------------------ \n" );
//		System.out.printf(  "| %40s | %40s | %40s \n", "id/uuid", "service spec id", "status" );
//		asList(sor).forEach( (s) -> {
//			System.out.printf(  "| %40s | %40s | %40s \n", s.getId(),  (new ArrayList<>(s.getOrderItem())).get(0).getService().getServiceSpecification().getId(), s.getState()  );
//			if ( s.getState().equals( ServiceOrderStateType.INITIAL ) ) {
//				ServiceOrderUpdate servOrder = new ServiceOrderUpdate();
//				servOrder.setState( ServiceOrderStateType.ACKNOWLEDGED );
//				Note noteItem = new Note();
//				noteItem.text("Order accepted");
//				servOrder.addNoteItem(noteItem);
//				HttpEntity<ServiceOrderUpdate> requestSo = new HttpEntity<>( servOrder );
//				ServiceOrder responseSoOrder = restTemplate.patchForObject(
//						"http://"+HOST+"/tmf-api/serviceOrdering/v4/serviceOrder/" + s.getId(),
//						requestSo,
//						ServiceOrder.class);
//			}
//		});
		
		ResponseEntity<ServiceOrder[]> responseServiceOrderList2 = restTemplate.getForEntity(
				"http://"+HOST+"/tmf-api/serviceOrdering/v4/serviceOrder",
				ServiceOrder[].class);

		ServiceOrder sor2[] = responseServiceOrderList2.getBody();
		asList(sor2).forEach( (s) -> {
			System.out.printf(  "|--------------SERVICE ORDER------------------ \n" );
			System.out.printf(  "| %40s | %40s \n", "id/uuid", "status" );
			System.out.printf(  "| %40s | %40s \n", s.getId(),   s.getState() );
			
			System.out.printf(  "\t|--------------ORDER ITEM------------------ \n" );
			System.out.printf(  "\t| %40s | %40s | %40s \n", "id/uuid",  "status", "ServiceName" );
			for (ServiceOrderItem oi : s.getOrderItem()) {
				System.out.printf(  "\t\t| %40s | %40s | %40s \n", oi.getId(),  oi.getState(), oi.getService().getName() );
				System.out.printf(  "\t\t|--------------Supporting Services------------------ \n" );
				System.out.printf(  "\t\t| %40s | %40s | %40s \n", "uuid", "id", "name" );
				for (ServiceRef sups : oi.getService().getSupportingService()) {
					System.out.printf(  "\t\t| %40s | %40s | %40s  \n", sups.getUuid(),  sups.getId(),  sups.getName() );
				}
			}
			System.out.printf(  "---------------------------------------- \\n\\n" );
			
			
		});
		
		
		//getservices
		///serviceInventory/v4/
		ResponseEntity<Service[]> responseServiceList = restTemplate.getForEntity(
				"http://"+HOST+"/tmf-api/serviceInventory/v4/service",
				Service[].class);
		Service services[] = responseServiceList.getBody();
		System.out.printf(  "--------------SERVICES ------------------ \n" );
		System.out.printf(  "| %40s | %40s | %40s | %40s \n", "id/uuid", "name", "order id", "status" );
		asList( services ).forEach( (s) -> {
			System.out.printf(  "| %40s | %40s | %40s | %40s \n", s.getId(), s.getName(),  (new ArrayList<>(s.getServiceOrder())).get(0).getId() , s.getState()  );
		});
		
	}
}