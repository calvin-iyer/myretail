package org.demo.myretail.controller.model;

import org.demo.myretail.dbo.Price;
import org.demo.myretail.dbo.Product;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductWebModel {

	private long id;
	private String name;
	
	@JsonProperty("current_price")
	private Price currentPrice;
	
	public static ProductWebModel buildFromProduct(Product product) {
		ProductWebModel productResponse = new ProductWebModel();
		productResponse.setId(product.getProductId());
		productResponse.setCurrentPrice(product.getCurrentPrice());
		productResponse.setName(product.getName());
		return productResponse;
	}

}
