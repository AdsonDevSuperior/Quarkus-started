package org.acme;

import javax.enterprise.context.ApplicationScoped;

public class GreetingService {

    @ApplicationScoped
    public String greeting(String name) {
        return "hello " + name;
    }
}
