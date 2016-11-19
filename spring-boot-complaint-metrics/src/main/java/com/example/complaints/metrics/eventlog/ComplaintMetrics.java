package com.example.complaints.metrics.eventlog;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.example.complaints.events.ComplaintCreated;
import com.rbmhtechnology.eventuate.AbstractEventsourcedView;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.control.Option;


public class ComplaintMetrics extends AbstractEventsourcedView {

    private Map<String, Integer> nrOfComplaintsPerCompany = HashMap.empty();

    public ComplaintMetrics(String id, ActorRef eventLog) {
        super(id, eventLog);
        setOnEvent(ReceiveBuilder.match(
                ComplaintCreated.class, evt -> {
                    Option<Integer> nrOfComplaints = nrOfComplaintsPerCompany.get(evt.company);
                    nrOfComplaintsPerCompany = nrOfComplaintsPerCompany.put(evt.company, nrOfComplaints.getOrElse(0) + 1);
                })
                .build()
        );
        setOnCommand(ReceiveBuilder
                .match(FindAll.class, cmd -> sender().tell(nrOfComplaintsPerCompany, self()))
                .build()
        );
    }

    public static class FindAll {
    }
}
