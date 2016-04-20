package com.vennetics.bell.sam.adapters.messaging.smpp;

import rx.Observable;

/**
 * Abstracts SMPP operations for the service layer.
 */
public interface ISmppAdapter {

    /**
     * Submit a binary message.
     *
     * @param senderAddress      the sender address.
     * @param destinationAddress the destination address.
     * @param message            the binary message content
     * @return sequence number for the message
     */
    Observable<SequenceNumberPair> submitSmBinary(String senderAddress,
                                                  String destinationAddress,
                                                  byte[] message);

    /**
     * Submit a text message.
     *
     * @param senderAddress      the sender address.
     * @param destinationAddress the destination address.
     * @param message            the text message content
     * @return sequence number for the message
     */
    Observable<SequenceNumberPair> submitSmText(String senderAddress,
                                                String destinationAddress,
                                                String message);

    /**
     * Creates an observer that will emit events when an inbound message is
     * received e.g. deliver_sm.
     *
     * @return the observable event emitter.
     */
    Observable<SmppInboundEvent> getEventBus();

    /**
     * Creates an observer that will emit events of type supplied when an inbound message is
     * received that matches that type e.g. deliver_sm.
     *
     * @return the observable event emitter.
     */
    <E extends SmppInboundEvent> Observable<E> getFilteredEventBus(Class<E> eventClass);
}
