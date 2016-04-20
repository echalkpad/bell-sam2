package com.vennetics.bell.sam.adapters.messaging.smpp.support;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.address.IAddressFormatter;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.commands.SendBinarySubmitSmRequestCommand;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.commands.SendTextSubmitSmRequestCommand;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.ISmppConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.subjects.Subject;

@Service
public class SmppAdapter implements ISmppAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SmppAdapter.class);

    private ISmppConnectionHandler connectionHandler;

    /**
     * Subject that can be observed by subscribers. On subscription a
     * PublishSubject is used to ensure only subsequently observed items are
     * emitted.
     */
    private Subject<SmppInboundEvent, SmppInboundEvent> inboundMessageBus;

    private IAddressFormatter addressNormalizer;

    @Autowired
    public SmppAdapter(final ISmppConnectionHandler connectionHandler,
                       final Subject<SmppInboundEvent, SmppInboundEvent> inboundMessageBus,
                       final IAddressFormatter addressNormalizer) {
        this.connectionHandler = connectionHandler;
        this.inboundMessageBus = inboundMessageBus;
        this.addressNormalizer = addressNormalizer;
    }

    @Override
    public Observable<SmppInboundEvent> getEventBus() {
        logger.debug("Registering for all inbound message events");
        return inboundMessageBus;
    }

    @Override
    public <E extends SmppInboundEvent> Observable<E> getFilteredEventBus(final Class<E> eventClass) {
        logger.debug("Registering for inbound message events of type {}",
                     eventClass.getCanonicalName());
        return inboundMessageBus.ofType(eventClass);
    }

    @Override
    public Observable<SequenceNumberPair> submitSmBinary(final String senderAddress,
                                                         final String destinationAddress,
                                                         final byte[] message) {
        return new SendBinarySubmitSmRequestCommand(connectionHandler,
                                                    senderAddress,
                                                    destinationAddress,
                                                    message,
                                                    addressNormalizer).observe();
    }

    @Override
    public Observable<SequenceNumberPair> submitSmText(final String senderAddress,
                                                       final String destinationAddress,
                                                       final String message) {
        return new SendTextSubmitSmRequestCommand(connectionHandler,
                                                  senderAddress,
                                                  destinationAddress,
                                                  message,
                                                  addressNormalizer).observe();
    }
}
