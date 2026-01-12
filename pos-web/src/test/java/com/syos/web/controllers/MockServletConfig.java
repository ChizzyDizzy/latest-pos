package com.syos.web.controllers;

import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Mock implementation of ServletConfig for testing servlet initialization
 */
public class MockServletConfig implements ServletConfig {
    private final ServletContext servletContext;
    private final String servletName;

    public MockServletConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.servletName = "MockServlet";
    }

    public MockServletConfig(ServletContext servletContext, String servletName) {
        this.servletContext = servletContext;
        this.servletName = servletName;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new Vector<String>().elements();
    }
}
