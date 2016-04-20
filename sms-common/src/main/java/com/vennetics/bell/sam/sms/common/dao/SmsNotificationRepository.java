package com.vennetics.bell.sam.sms.common.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsNotificationRepository extends CrudRepository<SmsNotification, Long> {

}
