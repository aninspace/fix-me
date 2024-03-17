package edu.school42.fixme.market;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainMarket {
    public static void main(String[] args) {
        log.info("market started");

        Market market = new Market();
        market.start();
    }
}
