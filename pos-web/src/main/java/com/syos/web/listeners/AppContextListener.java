package com.syos.web.listeners;

import com.syos.infrastructure.persistence.connection.DatabaseConnectionPool;
import com.syos.infrastructure.factories.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Context Listener
 * Initializes resources when the web application starts and cleans up when it stops.
 *
 * This listener:
 * 1. Initializes the database connection pool (thread-safe for concurrent requests)
 * 2. Sets up the ServiceFactory as a singleton for all servlets to use
 * 3. Configures application-wide attributes for thread pool monitoring
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        logger.info("=== SYOS POS Web Application Starting ===");

        try {
            // Initialize database connection pool (thread-safe singleton)
            DatabaseConnectionPool connectionPool = DatabaseConnectionPool.getInstance();
            logger.info("✓ Database connection pool initialized and ready for concurrent access");

            // Initialize ServiceFactory (singleton) - thread-safe services
            ServiceFactory serviceFactory = ServiceFactory.getInstance();
            ctx.setAttribute("serviceFactory", serviceFactory);
            logger.info("✓ ServiceFactory initialized with thread-safe services");

            // Store application metadata
            ctx.setAttribute("appVersion", "1.0-SNAPSHOT");
            ctx.setAttribute("appStartTime", System.currentTimeMillis());

            // Log Tomcat thread pool configuration (informational)
            logger.info("=== Tomcat Concurrency Configuration ===");
            logger.info("Application is ready to handle concurrent requests via Tomcat's thread pool");
            logger.info("Configure thread pool in server.xml or context.xml:");
            logger.info("  - maxThreads: Maximum concurrent request threads (default: 200)");
            logger.info("  - minSpareThreads: Minimum idle threads (default: 25)");
            logger.info("  - maxConnections: Maximum concurrent connections (default: 10000)");

            logger.info("=== Application Ready for Concurrent Access ===");
            logger.info("All services are thread-safe and ready to handle multiple simultaneous users");

        } catch (Exception e) {
            logger.error("✗ Failed to initialize application", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== SYOS POS Web Application Shutting Down ===");

        try {
            // Shutdown database connection pool
            DatabaseConnectionPool.getInstance().shutdown();
            logger.info("✓ Database connection pool shut down");

            logger.info("=== Application Shutdown Complete ===");

        } catch (Exception e) {
            logger.error("✗ Error during application shutdown", e);
        }
    }
}
