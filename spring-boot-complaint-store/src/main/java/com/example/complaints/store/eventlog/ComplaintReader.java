package com.example.complaints.store.eventlog;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.example.complaints.events.ComplaintCreated;
import com.example.complaints.store.web.model.Complaint;
import com.rbmhtechnology.eventuate.AbstractEventsourcedView;
import javaslang.collection.List;
import javaslang.control.Option;

import java.util.Optional;

public class ComplaintReader extends AbstractEventsourcedView {

    private List<Complaint> complaints = List.empty();

    public ComplaintReader(String id, ActorRef eventLog) {
        super(id, eventLog);
        setOnEvent(ReceiveBuilder.match(
                ComplaintCreated.class, cmd -> complaints = complaints.append(new Complaint(cmd.id, cmd.company, cmd.description)))
                .build()
        );
        setOnCommand(ReceiveBuilder
                .match(FindOne.class, cmd -> sender().tell(findOne(cmd.id), self()))
                .match(FindAll.class, cmd -> sender().tell(complaints, self()))
                .build()
        );
    }

    private Option<Complaint> findOne(String id) {
        return complaints.toStream()
                .find(e -> e.getId().equals(id));
    }

    public static class FindOne {
        public final String id;

        public FindOne(String id) {
            this.id = id;
        }
    }

    public static class FindAll {
    }
}
