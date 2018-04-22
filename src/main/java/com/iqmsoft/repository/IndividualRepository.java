package com.iqmsoft.repository;

import org.springframework.data.repository.CrudRepository;

import com.iqmsoft.domain.Individual;


public interface IndividualRepository extends CrudRepository <Individual, String> {
    Individual findByName(String name);
    Iterable<Individual> findByTeammatesName(String name);
}
