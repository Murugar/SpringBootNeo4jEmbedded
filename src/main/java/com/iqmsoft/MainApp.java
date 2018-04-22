package com.iqmsoft;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;
//import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;

import com.iqmsoft.domain.Individual;
import com.iqmsoft.repository.IndividualRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.lang.System.out;



@SpringBootApplication
public class MainApp implements CommandLineRunner {

    @Configuration
    @EnableNeo4jRepositories(basePackages = "com.iqmsoft")
    static class ApplicationConfig extends Neo4jConfiguration {

        public ApplicationConfig() {
            setBasePackage("com.iqmsoft");
        }

        @Bean
        GraphDatabaseService graphDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("accessingdataneo4j.db");
        }
    }

    @Autowired IndividualRepository personRepo;

    @Autowired GraphDatabase graphDatabase;

    public void run(String[] args) throws Exception {

        ArrayList<Individual> members = new ArrayList<>();
        members.add(new Individual("Test1"));
        members.add(new Individual("Test2"));
        members.add(new Individual("Test3"));
        members.add(new Individual("Test4"));

        Transaction tx = graphDatabase.beginTx();
        try {
            members.stream().forEach(b -> personRepo.save(b));

            //add relationships
            for(Individual p1 : members) {
                members.stream()
                        .filter(b -> !(b.name.equals(p1.name)))
                        .forEach(b -> {
                            Individual p = personRepo.findByName(p1.name);
                            p.worksWith(b);
                            personRepo.save(p);
                        });
            }

            //get info from neo4j
            out.println("Lookup each person by name...");
            members.stream()
                    .forEach(b -> out.println(personRepo.findByName(b.name)));


            out.println("Looking up who works with Test1...");
            personRepo.findByTeammatesName("Test1").forEach(out::println);

            tx.success();
        } finally {
            tx.close();
        }
    }
    public static void main(String[] args) throws Exception {
        FileUtils.deleteRecursively(new File("accessingdataneo4j.db"));
        SpringApplication.run(MainApp.class, args);
    }
}
