package com.atlassian.bitbucket.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.List;

public class CodecacheJmx extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(CodecacheJmx.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/html");
        resp.getWriter().write("<html><body><pre>" + getMemoryPoolReport() + "</pre></body></html>");
    }

    private String getMemoryPoolReport()
    {
        // generate heap state report
        String report = "";

        report += "\n" + new Date(System.currentTimeMillis()).toString();
        report += "\n";

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : memoryPoolMXBeans) {
            report += "\nMemory Pool: " + pool.getName();
            MemoryUsage usage = pool.getUsage();
            report += "\n   Init : " + usage.getInit() / 1024000 + "MB (" + usage.getInit() + ")";
            report += "\n   Used: " + usage.getUsed() / 1024000 + "MB (" + usage.getUsed() + ")";
            report += "\n   Max : " + usage.getMax() / 1024000 + "MB (" + usage.getMax() + ")";
            report += "\n   Committed: " + usage.getCommitted() / 1024000 + "MB (" + usage.getCommitted() + ")";
            report += "\n";
        }

        return report;
    }
}