package org.sk.i2i.evren.TGF.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import com.typesafe.config.Config;
import org.sk.i2i.evren.TGF.DTO.DataTransaction;
import org.sk.i2i.evren.TGF.DTO.SmsTransaction;
import org.sk.i2i.evren.TGF.DTO.VoiceTransaction;

public class AkkaActor extends AbstractActor {

    Config config = getContext().getSystem().settings().config();
    ActorSelection remoteActor = getContext().getSystem().actorSelection(config.getString("CHF.path"));

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match( DataTransaction.class, trans -> remoteActor.tell(trans, getSelf()) )
                .match( VoiceTransaction.class, trans -> remoteActor.tell(trans, getSelf()) )
                .match( SmsTransaction.class, trans -> remoteActor.tell(trans, getSelf()) )
                .matchAny(o -> System.out.println("received unknown message"))
                .build();
    }
}
