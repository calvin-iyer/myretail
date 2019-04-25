package org.demo.myretail.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.UUID;

import org.demo.myretail.controller.model.ProductWebModel;
import org.demo.myretail.dbo.CurrencyCode;
import org.demo.myretail.dbo.Price;
import org.demo.myretail.dbo.Product;
import org.demo.myretail.helper.HelperRestTemplate;
import org.demo.myretail.helper.model.target.TargetItem;
import org.demo.myretail.helper.model.target.TargetItemDescription;
import org.demo.myretail.helper.model.target.TargetListPrice;
import org.demo.myretail.helper.model.target.TargetPrice;
import org.demo.myretail.helper.model.target.TargetProduct;
import org.demo.myretail.helper.model.target.TargetProductResponse;
import org.demo.myretail.repository.ProductRepository;
import org.demo.myretail.service.exception.ServiceResponseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {
	
	@Autowired
	private ProductService productService;
	
	@MockBean
	private ProductRepository productRepository;
	
	@MockBean
	private HelperRestTemplate restTemplate;
	
	@Test
	public void testValidProductResponse() {
		Product product = getMockProduct(1234,"TestProd",CurrencyCode.USD, 100d);
		
		Mockito.when(productRepository.findByProductId(1234)).thenReturn(Optional.of(product));
		
		try {
			ProductWebModel response = productService.fetchProductById(product.getProductId());
			assertEquals(product.getProductId(),response.getId());
			assertEquals(product.getName(),response.getName());
			assertNotNull(response.getCurrentPrice());
			assertEquals(product.getCurrentPrice().getCurrencyCode(),response.getCurrentPrice().getCurrencyCode());
			assertEquals(product.getCurrentPrice().getValue(),response.getCurrentPrice().getValue());
		}catch(Exception e) {
			fail();
		}
	}
	
	@Test
	public void testProductFetchFromURLSuccess() {
		TargetProductResponse productResponse = getMockProductResponse("TestProd", 100d);
		
		Mockito.when(restTemplate.fetchProductFromTarget(1234)).thenReturn(productResponse);
		Mockito.when(productRepository.save(Mockito.any(Product.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		
		try {
			ProductWebModel response = productService.fetchProductById(1234);
			assertEquals(1234,response.getId());
			assertEquals(productResponse.getProduct().getItem().getProductDescription().getTitle(),response.getName());
			assertNotNull(response.getCurrentPrice());
			assertEquals(CurrencyCode.USD,response.getCurrentPrice().getCurrencyCode());
			assertEquals(productResponse.getProduct().getPrice().getListPrice().getPrice(),response.getCurrentPrice().getValue());
		}catch(Exception e) {
			fail();
		}
	}
	
	@Test
	public void testProductFetchFromURLFailure() {
		TargetProductResponse targetProductResponse = new TargetProductResponse();
		TargetProduct targetProduct = new TargetProduct();
		targetProductResponse.setProduct(targetProduct);
		
		Mockito.when(restTemplate.fetchProductFromTarget(Mockito.anyLong())).thenReturn(targetProductResponse);
		
		try {
			ProductWebModel response = productService.fetchProductById(1234);
			fail();
		} catch(ServiceResponseException e) {
			assertEquals(e.getHttpStatus(),HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			fail();
		}
	}
	
	@Test
	public void testProductPriceUpdateSuccess() {
		Product savedProduct = getMockProduct(1234,"TestProd",CurrencyCode.INR, 100d);
		Mockito.when(productRepository.save(Mockito.any(Product.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		Mockito.when(productRepository.findByProductId(1234)).thenReturn(Optional.of(savedProduct));
		
		try {
			ProductWebModel updateRequest = new ProductWebModel();
			updateRequest.setId(1234);
			updateRequest.setName("TestProd");
			Price newPrice = new Price();
			newPrice.setCurrencyCode(CurrencyCode.INR);
			newPrice.setValue(120d);
			updateRequest.setCurrentPrice(newPrice);
			ProductWebModel response = productService.updateProductById(1234, updateRequest);
			assertEquals(response.getId(),1234);
			assertEquals(response.getName(),savedProduct.getName());
			assertEquals(response.getCurrentPrice().getCurrencyCode(),newPrice.getCurrencyCode());
			assertEquals(response.getCurrentPrice().getValue(),newPrice.getValue());
		}catch(Exception e) {
			fail();
		}
	}
	
	@Test
	public void testProductUpdateFailureInvalidRequest() {
		Product savedProduct = getMockProduct(1234,"TestProd",CurrencyCode.INR, 100d);
		Mockito.when(productRepository.save(Mockito.any(Product.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
		Mockito.when(productRepository.findByProductId(1234)).thenReturn(Optional.of(savedProduct));
		
		try {
			ProductWebModel updateRequest = new ProductWebModel();
			updateRequest.setId(1234);
			updateRequest.setName("TestProd");
			productService.updateProductById(1234, updateRequest);
			fail();
		}catch(ServiceResponseException e) {
			assertEquals(e.getHttpStatus(),HttpStatus.BAD_REQUEST);
		}catch(Exception e) {
			fail();
		}
	}
	
	private Product getMockProduct(long productId,String name,CurrencyCode code,double priceVal) {
		Product product = new Product();
		product.setId(UUID.randomUUID().toString());
		product.setProductId(productId);
		product.setName(name);
		Price price = new Price();
		price.setCurrencyCode(code);
		price.setValue(priceVal);
		product.setCurrentPrice(price);
		return product;
	}
	
	private TargetProductResponse getMockProductResponse(String name, double price) {
		TargetProductResponse targetProductResponse = new TargetProductResponse();
		TargetProduct targetProduct = new TargetProduct();
		TargetItem targetItem = new TargetItem();
		TargetItemDescription targetItemDescription = new TargetItemDescription();
		targetItemDescription.setTitle(name);
		targetItem.setProductDescription(targetItemDescription);
		targetProduct.setItem(targetItem);
		TargetPrice targetPrice = new TargetPrice();
		TargetListPrice targetListPrice = new TargetListPrice();
		targetListPrice.setPrice(price);
		targetPrice.setListPrice(targetListPrice);
		targetProduct.setPrice(targetPrice);
		targetProductResponse.setProduct(targetProduct);
		return targetProductResponse;
	}
}
