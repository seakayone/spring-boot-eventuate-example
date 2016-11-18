package com.example.eventlog;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.example.eventlog.events.ComplaintCreated;
import com.example.web.model.Complaint;
import com.rbmhtechnology.eventuate.AbstractEventsourcedView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComplaintReader extends AbstractEventsourcedView {

    private Map<String, Complaint> complaints = new HashMap<>();

    public ComplaintReader(String id, ActorRef eventLog) {
        super(id, eventLog);
        setOnEvent(ReceiveBuilder.match(
                ComplaintCreated.class, cmd -> complaints.put(cmd.id, new Complaint(cmd.id, cmd.company, cmd.description)))
                .build()
        );
        setOnCommand(ReceiveBuilder
                .match(FindOne.class, cmd -> sender().tell(findOne(cmd.id), self()))
                .match(FindAll.class, cmd -> sender().tell(complaints.entrySet(), self()))
                .build()
        );
    }

    private Optional<Complaint> findOne(String id) {
        return complaints.entrySet().stream()
                .filter(e -> e.getKey().equals(id))
                .findFirst()
                .map(r -> r.getValue());
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
