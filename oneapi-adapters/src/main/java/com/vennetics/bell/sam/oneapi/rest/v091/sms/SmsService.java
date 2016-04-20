package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.oneapi.rest.v091.commands.GetSmsDeliveryStatusCommand;
import com.vennetics.bell.sam.oneapi.rest.v091.commands.SendSmsCommand;
import com.vennetics.bell.sam.oneapi.rest.v091.config.OneApiAdaptersConfig;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

/**
 * Default implementation of {@link ISmsService}.
 */

@Service("oneApiSmsService")
@EnableHystrix
public class SmsService implements ISmsService, InitializingBean {

    private RestTemplate restTemplate;
    @Autowired
    private OneApiAdaptersConfig templateFactory;

    @Override
    public Observable<ResponseEntity<SendSmsResponse>> sendSms(final String serviceIdentifier,
                                                               final MultiValueMap<String, String> params) {
        return new SendSmsCommand(serviceIdentifier,
                                  params,
                                  restTemplate).observe();
    }
    
    @Override
    public Observable<ResponseEntity<DeliveryStatusResponse>> getSmsDeliveryStatus(final String serviceIdentifier,
                                                                       final String messageId) {
        return new GetSmsDeliveryStatusCommand(serviceIdentifier,
                                               messageId,
                                               restTemplate).observe();
    }

    @Override
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "squid:S00112"})
    public void afterPropertiesSet() throws Exception {
        restTemplate = templateFactory.oneapiRestTemplate();
    }
}

