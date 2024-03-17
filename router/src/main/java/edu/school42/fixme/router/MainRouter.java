package edu.school42.fixme.router;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainRouter {

    public static void main(String[] args) {
        log.info("router started!");

        Router router = new Router();
        router.start();
    }
}
