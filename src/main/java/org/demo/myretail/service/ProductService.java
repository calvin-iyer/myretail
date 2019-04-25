package org.demo.myretail.service;

import java.util.Optional;

import org.demo.myretail.controller.model.ProductWebModel;
import org.demo.myretail.dbo.CurrencyCode;
import org.demo.myretail.dbo.Price;
import org.demo.myretail.dbo.Product;
import org.demo.myretail.helper.HelperRestTemplate;
import org.demo.myretail.helper.model.target.TargetProductResponse;
import org.demo.myretail.repository.ProductRepository;
import org.demo.myretail.service.exception.ServiceResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {

	@Autowired
	private HelperRestTemplate restTemplate;
	
	@Autowired
	private ProductRepository productRepository;

	public ProductWebModel fetchProductById(long id) throws ServiceResponseException {
		Optional<Product> savedProduct = productRepository.findByProductId(id);
		if(!savedProduct.isPresent()) {
			Product newProduct = fetchProductFromUrl(id);
			try {
				newProduct = productRepository.save(newProduct);
			}catch(Exception e) {
				log.error("Error saving product to db",e);
				throw ServiceResponseException.internalServerError().message("Error fetching product!");
			}
			return ProductWebModel.buildFromProduct(newProduct);
		} else {
			return ProductWebModel.buildFromProduct(savedProduct.get());
		}
	}

	private Product fetchProductFromUrl(long id) throws ServiceResponseException {
		try {
			TargetProductResponse targetProductResponse = restTemplate.fetchProductFromTarget(id);
			if(targetProductResponse == null || targetProductResponse.getProduct() == null || targetProductResponse.getProduct().getItem() == null) {
				throw ServiceResponseException.badRequest().message("Invalid Product ID specified!");
			}
			Product product = new Product();
			product.setProductId(id);
			product.setName(targetProductResponse.getProduct().getItem().getProductDescription().getTitle());
			Price price = new Price();
			if(targetProductResponse.getProduct().getPrice() == null || targetProductResponse.getProduct().getPrice().getListPrice() == null) {
				price.setValue(null);
				price.setCurrencyCode(null);
			} else {
				price.setValue(targetProductResponse.getProduct().getPrice().getListPrice().getPrice());
				price.setCurrencyCode(CurrencyCode.USD);
			}
			product.setCurrentPrice(price);
			return product;
		}catch(ServiceResponseException e) {
			throw e;
		}catch(Exception e) {
			log.error("Error fetching product with id {} from url",id,e);
			throw ServiceResponseException.internalServerError().message("Error fetching product with id : "+id);
		}
	}

	public ProductWebModel updateProductById(long id,ProductWebModel updateRequest) throws ServiceResponseException {
		Optional<Product> savedProduct = productRepository.findByProductId(id);
		if(!savedProduct.isPresent()) {
			fetchProductById(id);
			savedProduct = productRepository.findByProductId(id);
		}
		
		if(updateRequest.getCurrentPrice() == null || updateRequest.getCurrentPrice().getCurrencyCode() == null || updateRequest.getCurrentPrice().getValue() == null) {
			throw ServiceResponseException.badRequest().message("Invalid Product pricing data!");
		}
		
		savedProduct.get().getCurrentPrice().setCurrencyCode(updateRequest.getCurrentPrice().getCurrencyCode());
		savedProduct.get().getCurrentPrice().setValue(updateRequest.getCurrentPrice().getValue());
		
		try {
			return ProductWebModel.buildFromProduct(productRepository.save(savedProduct.get()));
		}catch(Exception e) {
			log.error("Error saving product data!",e);
			throw ServiceResponseException.internalServerError().message("Error updating product!");
		}
	}
	
}
