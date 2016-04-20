package com.vennetics.bell.sam.sms.common.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SmsMessageRepository extends CrudRepository<SmsMessage, Long> {

    List<SmsMessage> findByExternalRequestId(UUID externalRequestId);

    List<SmsMessage> findBySequenceNumber(Integer sequenceNumber);

    List<SmsMessage> findByMessageId(String messageId);
}
