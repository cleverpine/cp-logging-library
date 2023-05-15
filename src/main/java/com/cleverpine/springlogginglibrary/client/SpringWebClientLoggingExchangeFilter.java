package com.cleverpine.springlogginglibrary.client;

import com.cleverpine.springlogginglibrary.models.LoggingInfoContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;


import static com.cleverpine.springlogginglibrary.util.Constants.TRACE_ID;

public class SpringWebClientLoggingExchangeFilter {

    public static ExchangeFilterFunction cpLoggingFilter(){
        return (clientRequest, next) -> {
            ClientRequest newRequest = ClientRequest.from(clientRequest)
                    .header(TRACE_ID, LoggingInfoContext.getInstance().getTraceId())
                    .build();

            return next.exchange(newRequest);
        };
    }
}
