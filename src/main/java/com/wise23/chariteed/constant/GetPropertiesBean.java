package com.wise23.chariteed.constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GetPropertiesBean {

    private static String applicationserverURL;
    private static String testserverURL;

    @Autowired
    public GetPropertiesBean(@Value("${applicationserver.url}") String applicationserverURL,
            @Value("${testserver.url}") String testserverURL) {
        GetPropertiesBean.testserverURL = testserverURL;
        GetPropertiesBean.applicationserverURL = applicationserverURL;
    }

    public static String getApplicationserverURL() {
        return applicationserverURL;
    }

    public static String getTestserverURL() {
        return testserverURL;
    }

}
