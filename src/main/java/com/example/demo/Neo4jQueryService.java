package com.example.demo;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.summary.Notification;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.summary.GqlStatusObject;
import org.springframework.stereotype.Service;

@Service
public class Neo4jQueryService {

    public String executeQuery(String url, String username, String password, String query) {
        StringBuilder resultBuilder = new StringBuilder();

        try (Driver driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
             Session session = driver.session()) {

            Result result = session.run(query);
            ResultSummary summary = result.consume();

            resultBuilder.append(processNotifications(summary.notifications()));
            resultBuilder.append(processGQLStatusObjects(summary.gqlStatusObjects()));

        } catch (Neo4jException e) {
            // Specific handling for Neo4j exceptions
            String errorCode = e.code(); // Neo4j specific error code
            String errorMessage = e.getMessage();

            resultBuilder.append("Error Code: ").append(errorCode != null ? errorCode : "Unknown").append("\n");
            resultBuilder.append("Error Message: ").append(errorMessage.replaceAll("\n", " ").replaceAll("\r", " ")).append("\n");

        } catch (Exception e) {
            // General exception handling
            String errorCode = "Unknown";
            String errorMessage = e.getMessage();

            if (e.getCause() != null) {
                errorCode = e.getCause().getClass().getSimpleName();
                errorMessage += " | Cause: " + e.getCause().getMessage();
            }

            resultBuilder.append("Error Code: ").append(errorCode).append("\n");
            resultBuilder.append("Error Message: ").append(errorMessage.replaceAll("\n", " ").replaceAll("\r", " ")).append("\n");
        }

        return resultBuilder.toString();
    }

    private String processNotifications(Iterable<Notification> notifications) {
        StringBuilder notificationBuilder = new StringBuilder();
        notificationBuilder.append("<span class=\"notification-api\">").append("\nNotification API\n\n").append("</span>");
        notifications.forEach(notification -> {
            notificationBuilder.append(String.format("%s: %s\n", notification.code(), notification.description()));
            notificationBuilder.append(String.format("          Position: %s\n", notification.position()));
        });
        return notificationBuilder.toString();
    }

    private String processGQLStatusObjects(Iterable<GqlStatusObject> gqlStatusObjects) {
        StringBuilder gqlStatusBuilder = new StringBuilder();
        gqlStatusBuilder.append("<span class=\"gqlstatusobject-api\">").append("\nGQLStatusObject API\n\n").append("</span>");
        gqlStatusObjects.forEach(gqlStatusObject -> {
            gqlStatusBuilder.append(String.format("%s: %s", gqlStatusObject.gqlStatus(), gqlStatusObject.statusDescription()));
            gqlStatusBuilder.append(String.format("      %s\n\n", gqlStatusObject.diagnosticRecord()));
        });
        return gqlStatusBuilder.toString();
    }
}