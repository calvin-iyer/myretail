package org.demo.myretail.helper.model.target;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class TargetProductResponse {
	
	private TargetProduct product;

}
