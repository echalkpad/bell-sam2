package com.vennetics.microservices.common.core.soap.gateway.adapter.errors;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

@ErrorTypeSet("/parlayx-adapters-errors-messages.yml")
public enum ParlayxAdaptersErrorMessageType {

    INVALID_SERVICE_IDENTIFIER;
}
