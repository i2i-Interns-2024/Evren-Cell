package com.ikbalci;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        logger.info("Hello, World!");
        logger.error("This is an error message");
        logger.fatal("This is a fatal message");


    }
}