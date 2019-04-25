package org.demo.myretail.helper;

import org.demo.myretail.helper.model.target.TargetProductResponse;
import org.demo.myretail.util.ProductURL;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HelperRestTemplate {

	private RestTemplate restTemplate = new RestTemplate();

	public TargetProductResponse fetchProductFromTarget(long id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", "application/json");
		HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
		ResponseEntity<TargetProductResponse> response = restTemplate.exchange(ProductURL.TARGET_PRODUCT_URL.replace("{id}", String.valueOf(id)), HttpMethod.GET, httpEntity, TargetProductResponse.class);
		return response.getBody();
	}

}
