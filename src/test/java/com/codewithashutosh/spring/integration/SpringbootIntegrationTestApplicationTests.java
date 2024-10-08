package com.codewithashutosh.spring.integration;

import com.codewithashutosh.spring.integration.entity.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootCrudExample2ApplicationTests {

	@LocalServerPort
	private int port;

	private String baseUrl = "http://localhost";

	private static RestTemplate restTemplate;

	@Autowired
	private TestH2Repository h2Repository;

	@BeforeAll
	public static void init() {
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setUp() {
		baseUrl = baseUrl.concat(":").concat(port + "").concat("/products");
	}


	@Test
	public void testAddProduct() {
		Product product = new Product("headset", 2, 7999);
		Product response = restTemplate.postForObject(baseUrl, product, Product.class);
		assertEquals("headset", response.getName());
		assertEquals(1, h2Repository.findAll().size());
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (4,'AC', 1, 34000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE name='AC'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testGetProducts() {
		List<Product> products = restTemplate.getForObject(baseUrl, List.class);
		assertEquals(1, products.size());
		assertEquals(1, h2Repository.findAll().size());
	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (1,'CAR', 1, 334000)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testFindProductById() {
		Product product = restTemplate.getForObject(baseUrl + "/{id}", Product.class, 1);
		assertAll(
				() -> assertNotNull(product),
				() -> assertEquals(1, product.getId()),
				() -> assertEquals("CAR", product.getName())
		);

	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (2,'shoes', 1, 999)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = "DELETE FROM PRODUCT_TBL WHERE id=1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void testUpdateProduct(){
		Product product = new Product("shoes", 1, 1999);
		restTemplate.put(baseUrl+"/update/{id}", product, 2);
		Product productFromDB = h2Repository.findById(2).get();
		assertAll(
				() -> assertNotNull(productFromDB),
				() -> assertEquals(1999, productFromDB.getPrice())
		);



	}

	@Test
	@Sql(statements = "INSERT INTO PRODUCT_TBL (id,name, quantity, price) VALUES (8,'books', 5, 1499)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	public void testDeleteProduct(){
		int recordCount=h2Repository.findAll().size();
		assertEquals(1, recordCount);
		restTemplate.delete(baseUrl+"/delete/{id}", 8);
		assertEquals(0, h2Repository.findAll().size());

	}

	/*
	1. @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT):
This annotation is used in integration tests
to start the entire Spring Boot application context.
 RANDOM_PORT ensures that the application runs
 on a random port to avoid port conflicts during tests.

2. @LocalServerPort:
This annotation is used to inject the
random port that was assigned to the
test instance. It allows the test to
reference the actual port the Spring Boot
 application is running on during the test.

3. H2 Database:
H2 is an in-memory database used
mainly for testing purposes.
It is lightweight, fast, and can be embedded
 within the application. Since it's in-memory,
  the data is not persisted after the
  application stops, making it ideal for testing.

4. spring.datasource.url=jdbc:h2:mem:testdb:
This configures the application to use
 an H2 database with the URL jdbc:h2:mem:testdb.mem
 indicates an in-memory database,
 and testdb is the database name.

5. spring.h2.console.enabled=true:
This enables the H2 database web console,
allowing developers to view and interact
with the H2 database using a browser.
You can typically access it via
http://localhost:{port}/h2-console.

6. spring.jpa.show-sql=true:
This configuration logs SQL
statements executed by Hibernate
(the JPA provider) to the console.
It helps developers see the actual SQL
 queries being generated and run by Hibernate.

7. spring.jpa.properties.hibernate.format_sql=true:
This formats the SQL output
for readability. It adds indentation
and line breaks to make the logged SQL easier to read.
	 */


}
