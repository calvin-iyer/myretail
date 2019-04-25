package org.demo.myretail.repository;

import java.util.Optional;

import org.demo.myretail.dbo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>{

	Optional<Product> findByProductId(long id);

}
