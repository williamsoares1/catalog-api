package com.estudos.catalog.repository.mongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.estudos.catalog.entity.Category;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String>{
    List<Category> findByOwnerId(String ownerId);
}
