package org.demo.myretail.helper.model.target;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class TargetItem {

	@JsonProperty(value="product_description")
	private TargetItemDescription productDescription;
	
}
