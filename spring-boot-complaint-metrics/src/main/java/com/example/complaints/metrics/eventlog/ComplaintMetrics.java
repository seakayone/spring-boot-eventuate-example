package com.example.complaints.metrics.eventlog;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.example.complaints.events.ComplaintCreated;
import com.rbmhtechnology.eventuate.AbstractEventsourcedView;

import java.util.HashMap;
import java.util.Map;

public class ComplaintMetrics extends AbstractEventsourcedView {

    private Map<String, Integer> complaints = new HashMap<>();

    public ComplaintMetrics(String id, ActorRef eventLog) {
        super(id, eventLog);
        setOnEvent(ReceiveBuilder.match(
                ComplaintCreated.class, evt -> {
                    if (complaints.get(evt.company) != null) {
                        complaints.put(evt.company, complaints.get(evt.company) + 1);
                    } else {
                        complaints.put(evt.company, 1);
                    }
                })
                .build()
        );
        setOnCommand(ReceiveBuilder
                .match(FindAll.class, cmd -> sender().tell(complaints, self()))
                .build()
        );
    }

    public static class FindAll {
    }
}
