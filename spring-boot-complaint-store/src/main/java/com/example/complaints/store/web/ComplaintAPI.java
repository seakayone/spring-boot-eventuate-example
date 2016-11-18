package com.example.complaints.store.web;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.example.complaints.store.eventlog.ComplaintReader;
import com.example.complaints.store.eventlog.ComplaintWriter;
import com.example.complaints.store.web.model.Complaint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Optional;

@RestController
public class ComplaintAPI {

    private final ActorRef complaintWriter;
    private final ActorRef complaintReader;

    private static final Timeout TIMEOUT = new Timeout(Duration.create(3, "seconds"));

    public ComplaintAPI(@Qualifier("complaintWriter") ActorRef complaintWriter, @Qualifier("complaintReader") ActorRef complaintReader) {
        this.complaintWriter = complaintWriter;
        this.complaintReader = complaintReader;
    }

    @PostMapping()
    public ResponseEntity createComplaint(@RequestBody Complaint complaint) throws Exception {
        ComplaintWriter.CreateComplaint createCompl = new ComplaintWriter.CreateComplaint(complaint.getId(), complaint.getCompany(), complaint.getDescription());
        Future<Object> future = Patterns.ask(complaintWriter, createCompl, TIMEOUT);
        Object result = Await.result(future, TIMEOUT.duration());
        if (result instanceof ComplaintWriter.CreateComplaintSuccess) {
            return ResponseEntity.ok(((ComplaintWriter.CreateComplaintSuccess) result).id);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(((ComplaintWriter.CreateComplaintFailure) result).cause.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity findOne(@PathVariable String id) throws Exception {
        Future<Object> future = Patterns.ask(complaintReader, new ComplaintReader.FindOne(id), TIMEOUT);
        Object result = Await.result(future, TIMEOUT.duration());
        if (((Optional) result).isPresent()) {
            return ResponseEntity.ok(((Optional) result).get());
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping()
    public ResponseEntity findAll() throws Exception {
        Future<Object> future = Patterns.ask(complaintReader, new ComplaintReader.FindAll(), TIMEOUT);
        Object result = Await.result(future, TIMEOUT.duration());
        return ResponseEntity.ok(result);
    }

}
