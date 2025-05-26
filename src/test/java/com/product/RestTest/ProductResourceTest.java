package com.product.RestTest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProductResourceTest {

	@Test
	public void testCreateProduct() {
		String productJson = """
				{
				    "name": "Test Product",
				    "description": "Test Description",
				    "price": 123.45,
				    "quantity": 10
				}
				""";

		RestAssured.given().contentType(ContentType.JSON).body(productJson).when().post("/products").then()
				.statusCode(201).body("name", is("Test Product")).body("quantity", is(10));
	}

	@Test
	public void testGetAllProducts() {
		RestAssured.given().when().get("/products").then().statusCode(200).body("size()", greaterThanOrEqualTo(0));
	}

	@Test
	public void testUpdateProduct() {
		String updateJson = """
				{
				    "name": "Updated Product",
				    "description": "Updated Description",
				    "price": 456.78,
				    "quantity": 20
				}
				""";

		// Make sure product with ID 1 exists
		RestAssured.given().contentType(ContentType.JSON).body(updateJson).when().put("/products/1").then()
				.statusCode(200).body("name", is("Updated Product"));
	}

	@Test
	public void testDeleteProduct() {
		// First create a product to delete
		String productJson = """
				{
				    "name": "Delete Product",
				    "description": "To be deleted",
				    "price": 100,
				    "quantity": 1
				}
				""";

		int id = RestAssured.given().contentType(ContentType.JSON).body(productJson).when().post("/products").then()
				.statusCode(201).extract().path("id");

		RestAssured.given().when().delete("/products/" + id).then().statusCode(204);
	}
}
