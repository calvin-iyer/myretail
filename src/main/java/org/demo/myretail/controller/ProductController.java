package org.demo.myretail.controller;

import org.demo.myretail.controller.model.ProductWebModel;
import org.demo.myretail.service.ProductService;
import org.demo.myretail.service.exception.ServiceResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@GetMapping(value="/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity fetchProductById(@PathVariable("id")long id) {
		try {
			return ResponseEntity.ok(productService.fetchProductById(id));
		}catch(ServiceResponseException e) {
			return e.getResponseEntity();
		}
	}
	
	@RequestMapping(value="/{id}",method= {RequestMethod.POST,RequestMethod.PUT},consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateProductById(@PathVariable("id")long id, @RequestBody ProductWebModel productResponse) {
		try {
			return ResponseEntity.ok(productService.updateProductById(id,productResponse));
		}catch(ServiceResponseException e) {
			return e.getResponseEntity();
		}
	}
	
}
