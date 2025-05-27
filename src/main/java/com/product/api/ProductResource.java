package com.product.api;

import java.util.List;

import org.jboss.logging.Logger;

import com.product.model.Product;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

	private static final Logger LOG = Logger.getLogger(ProductResource.class);

	@GET
	public Uni<List<Product>> getAllProducts() {
		return Product.listAll();
	}

	@GET
	@Path("/{id}")
	public Uni<Response> getById(@PathParam("id") Integer id) {
		return Product.findById(id).onItem().ifNotNull().transform(person -> Response.ok(person).build()).onItem()
				.ifNull().continueWith(Response.status(Response.Status.NO_CONTENT).build());
	}

	@POST
	public Uni<Response> create(Product product) {
		return product.persist().onItem()
				.transform(inserted -> Response.created(URI.create("/products/" + product.id)).build());
	}

	@PUT
	@Path("/{id}")
	public Uni<Response> update(@PathParam("id") Integer id, Product updatedProduct) {
		System.out.println("Starting product fields in memory: " + updatedProduct.toString());
		return Product.<Product>findById(id).onItem().ifNotNull().transformToUni(product -> {
			// Update in memory
			product.name = updatedProduct.name;
			product.description = updatedProduct.description;
			product.price = updatedProduct.price;
			product.quantity = updatedProduct.quantity;

			LOG.info("Updated product fields in memory: " + product);

			// Now persist the change explicitly
			return product.flush().replaceWith(Response.ok(product).build());
		}).onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build);
	}

	@DELETE
	@Path("/{id}")
	@WithTransaction
	public Uni<Response> delete(@PathParam("id") Integer id) {
		return Product.deleteById(id).onItem().transform(
				deleted -> deleted ? Response.noContent().build() : Response.status(Response.Status.OK).build());
	}
}
