package com.estudos.catalog.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.estudos.catalog.entity.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

}
