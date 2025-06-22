package com.hustairline.airline_system.repository;

import java.util.List;

public interface BaseRepository<T, ID> {

    T save(T entity);
    
    T findById(ID id);
    
    List<T> findAll();
    
    boolean deleteById(ID id);
    
    boolean existsById(ID id);
    
    long count();
} 

