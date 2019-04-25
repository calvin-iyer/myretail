package org.demo.myretail.dbo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="product")
public class Product {

	@Id
	private String id;
	
	@Indexed(unique=true,name="productId")
	private long productId;
	
	private String name;
	
	private Price currentPrice;
}
