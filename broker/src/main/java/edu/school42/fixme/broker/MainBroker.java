package edu.school42.fixme.broker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainBroker {

    public static void main(String[] args) {
        log.info("broker is working");

        Broker broker = new Broker();
        broker.start();
    }
}
