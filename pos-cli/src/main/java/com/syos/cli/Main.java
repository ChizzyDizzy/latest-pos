package com.syos.cli;

import com.syos.infrastructure.persistence.connection.DatabaseConnectionPool;
import com.syos.cli.ui.cli.CLIApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the POS CLI application
 * This is a thin wrapper around the pos-core business logic
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting SYOS POS System - CLI Application...");

        try {
            // Initialize database connection pool
            DatabaseConnectionPool.getInstance();
            logger.info("Database connection pool initialized");

            // Start CLI application
            CLIApplication app = new CLIApplication();
            app.start();

        } catch (Exception e) {
            logger.error("Failed to start CLI application", e);
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
        } finally {
            // Shutdown connection pool on exit
            DatabaseConnectionPool.getInstance().shutdown();
            logger.info("Application shutdown complete");
        }
    }
}
