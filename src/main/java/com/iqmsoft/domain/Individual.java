package com.iqmsoft.domain;

import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.HashSet;
import java.util.Set;

import static org.neo4j.graphdb.Direction.*;

@NodeEntity
public class Individual {

    @GraphId Long id;
    public String name;

    public Individual(){}
    public Individual(String name){ this.name = name; }


    /*
    bi-directional relationship.
    Use @fetch to eagerly retrive teamates
     */
    @RelatedTo(type="TEAMAMTE", direction= BOTH)
    public @Fetch Set<Individual> teammates;

    public void worksWith(Individual person){
        if (teammates == null) teammates = new HashSet<Individual>();
        teammates.add(person);
    }

    @Override
    public String toString() {
        String results = name + "'s teammates include\n";
        if (teammates != null) {
            for (Individual person : teammates) {
                results += "\t- " + person.name + "\n";
            }
        }
        return results;
    }
}
