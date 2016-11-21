package com.example.complaints.store.eventlog;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.example.complaints.events.ComplaintCreated;
import com.example.complaints.store.web.model.Complaint;
import com.rbmhtechnology.eventuate.AbstractEventsourcedActor;
import com.rbmhtechnology.eventuate.ResultHandler;

public class ComplaintWriter extends AbstractEventsourcedActor {

    public static class CreateComplaint {
        public final String id;
        public final String company;
        public final String description;

        public CreateComplaint(String id, String company, String description) {
            this.id = id;
            this.company = company;
            this.description = description;
        }
    }

    public static class CreateComplaintSuccess {
        public final Complaint created;

        public CreateComplaintSuccess(Complaint created) {
            this.created = created;
        }
    }

    public static class CreateComplaintFailure {
        public final Throwable cause;

        public CreateComplaintFailure(Throwable cause) {
            this.cause = cause;
        }
    }

    public ComplaintWriter(String id, ActorRef eventLog) {
        super(id, eventLog);

        setOnCommand(ReceiveBuilder
                .match(CreateComplaint.class,
                        cmd -> persist(new ComplaintCreated(cmd.id, cmd.company, cmd.description),
                                ResultHandler.on(
                                        evt -> sender().tell(new CreateComplaintSuccess(new Complaint(evt)), self()),
                                        err -> sender().tell(new CreateComplaintFailure(err), self())
                                )))
                .build());
    }
}