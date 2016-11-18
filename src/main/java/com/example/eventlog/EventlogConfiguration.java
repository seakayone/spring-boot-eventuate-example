package com.example.eventlog;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.rbmhtechnology.eventuate.ReplicationConnection;
import com.rbmhtechnology.eventuate.ReplicationEndpoint;
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class EventlogConfiguration {

    @Bean
    public ActorSystem system() {
        return ActorSystem.create(ReplicationConnection.DefaultRemoteSystemName(), ConfigFactory.load());
    }

    @Bean
    public ReplicationEndpoint replicationEndpoint(ActorSystem system) {
        Function<String, Props> eventLogProps = id -> LeveldbEventLog.props(id, "j", true);
        ReplicationEndpoint endpoint = ReplicationEndpoint.create(eventLogProps, system);
        endpoint.activate();
        return endpoint;
    }

    @Bean
    public ActorRef eventLog(ReplicationEndpoint endpoint) {
        return endpoint.logs().get("default").get();
    }

    @Bean
    @Qualifier("complaintWriter")
    public ActorRef complaintWriter(final ActorSystem system, @Qualifier("eventLog") final ActorRef eventLog) {
        return system.actorOf(Props.create(ComplaintWriter.class, () -> new ComplaintWriter("cw", eventLog)));
    }

    @Bean
    @Qualifier("complaintReader")
    public ActorRef complaintReader(final ActorSystem system, @Qualifier("eventLog") final ActorRef eventLog) {
        return system.actorOf(Props.create(ComplaintReader.class, () -> new ComplaintReader("cr", eventLog)));
    }

}
