package com.example.eventlog;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.example.eventlog.events.ComplaintCreated;
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
        public final String id;

        public CreateComplaintSuccess(String id) {
            this.id = id;
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
                                        evt -> sender().tell(new CreateComplaintSuccess(evt.id), self()),
                                        err -> sender().tell(new CreateComplaintFailure(err), self())
                                )))
                .build());
    }
}