package com.gayuh.personalproject.service;

import java.util.List;

public interface GenericService<T, K> {
    List<T> getAll();

    T getById(Long id);

    T create(K request);

    T update(K request, Long id);

    void deleteById(Long id);
}
