package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class Neo4jController {

    private final Neo4jQueryService neo4jQueryService;

    @Autowired
    public Neo4jController(Neo4jQueryService neo4jQueryService) {
        this.neo4jQueryService = neo4jQueryService;
    }

    @PostMapping("/runQuery")
    public String runQuery(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        String username = request.get("username");
        String password = request.get("password");
        String query = request.get("query");

        // Running the query using our service instance
        return neo4jQueryService.executeQuery(url, username, password, query);
    }
}