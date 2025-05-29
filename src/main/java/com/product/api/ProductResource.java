package com.product.api;

import java.util.List;

import org.jboss.logging.Logger;

import com.product.model.CheckProductDTO;
import com.product.model.Product;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Sort;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.net.URI;

@ApplicationScoped
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
	
	private static final Logger LOG = Logger.getLogger(ProductResource.class);

	static {
		final String LOG_DIR = "D:/logs/";
		File dir = new File(LOG_DIR);
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			if (created) {
				System.out.println("Log directory created: " + dir.getAbsolutePath());
			} else {
				System.err.println("Failed to create log directory: " + dir.getAbsolutePath());
			}
		}
	}

	
	@GET
	@WithSession
	public Uni<List<Product>> getAllProducts(@QueryParam("asc") Boolean asc) {
		if (asc != null && asc) {
			LOG.info("Sorting based on the asc flag");
			return Product.<Product>listAll(Sort.by("price"));
		} else {
			LOG.info("getting all products");
			return Product.listAll();
		}
	}

	@POST
	public Uni<Response> create(Product product) {
		return product.persist().onItem().transform(inserted -> {
			LOG.info("Product Created successfully!");
			return Response.created(URI.create("/products/" + product.id)).build();
		});
	}

	@GET
	@Path("/{id}")
	@WithSession
	public Uni<Response> checkAvailability(@PathParam("id") Integer id, @QueryParam("count") Integer count) {
		return Product.<Product>findById(id).onItem().ifNotNull().transform(product -> {
			if (count == null) {
				LOG.info("find product by id");
				return Response.ok(product).build();
			} else {
				boolean isAvailable = product.getQuantity() >= count;
				int unavailableCount = Math.max(count - product.getQuantity(), 0);
				LOG.info("Product: " + product + ", Requested count: " + count + ", Available: " + isAvailable);
				return Response.ok(new CheckProductDTO(isAvailable, unavailableCount)).build();
			}
		}).onItem().ifNull().continueWith(Response.status(Response.Status.NO_CONTENT)::build);
	}

	@PUT
	@Path("/{id}")
	@WithTransaction
	public Uni<Response> update(@PathParam("id") Integer id, Product updatedProduct) {
		return Product.<Product>findById(id).onItem().ifNotNull().transformToUni(product -> {
			// Update in memory
			product.name = updatedProduct.name;
			product.description = updatedProduct.description;
			product.price = updatedProduct.price;
			product.quantity = updatedProduct.quantity;

			LOG.info("Updated product fields in memory: " + product);

			// Now persist the change explicitly
			return product.flush().replaceWith(Response.ok(product).build());
		}).onItem().ifNull().continueWith(Response.status(Response.Status.NO_CONTENT)::build);
	}

	@DELETE
	@Path("/{id}")
	@WithTransaction
	public Uni<Response> delete(@PathParam("id") Integer id) {
		LOG.info("Deleted product fields from DB");
		return Product.deleteById(id).onItem().transform(
				deleted -> deleted ? Response.noContent().build() : Response.status(Response.Status.OK).build());
	}
}
