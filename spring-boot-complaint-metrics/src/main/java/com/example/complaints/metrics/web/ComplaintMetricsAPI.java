package com.example.complaints.metrics.web;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.example.complaints.metrics.eventlog.ComplaintMetrics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@RestController
public class ComplaintMetricsAPI {

    private final ActorRef complaintMetrics;

    private static final Timeout TIMEOUT = new Timeout(Duration.create(3, "seconds"));

    public ComplaintMetricsAPI(@Qualifier("complaintMetrics") ActorRef complaintMetrics) {
        this.complaintMetrics = complaintMetrics;
    }

    @GetMapping()
    public ResponseEntity findAll() throws Exception {
        Future<Object> future = Patterns.ask(complaintMetrics, new ComplaintMetrics.FindAll(), TIMEOUT);
        Object result = Await.result(future, TIMEOUT.duration());
        return ResponseEntity.ok(result);
    }

}
