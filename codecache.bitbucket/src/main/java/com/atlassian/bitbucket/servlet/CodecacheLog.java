package com.atlassian.bitbucket.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CodecacheLog extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(CodecacheLog.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // requires the following runtime settings
        //-XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation

        // either determine the value from this setting
        //-XX:LogFile=jvm-compilation.log
        // via ManagementFactory.getRuntimeMXBean().getInputArguments();
        //OR assume the default location see https://docs.oracle.com/en/java/javase/12/tools/java.html#GUID-3B1CE181-CD30-4178-9602-230B800D4FAE NB it now includes to PID in the log name.

        //get the runtime folder

        resp.setContentType("text/html");
        StringBuilder buffer = new StringBuilder();
        buffer.append(new Date(System.currentTimeMillis()).toString()).append("\n");

        buffer.append(String.join(", ", getArgs())).append("\n");

        String logFilePath = hotspotLogFilePath();
        buffer.append(logFilePath).append("\n");

        if(!logFilePath.contains("Compilation Logging is not active."))
        {
            Optional<String> codeCachefailures = Files.lines(Paths.get(logFilePath))
                    .filter(s -> s.contains("<failure reason='CodeCache is full'"))
                    .findAny();
            Optional<String> compilationDisabledfailures = Files.lines(Paths.get(logFilePath))
                    .filter(s -> s.contains("<failure reason='compilation is disabled'"))
                    .findAny();


            Optional<String> codeCacheFull = Files.lines(Paths.get(logFilePath))
                    .filter(s -> s.contains("code_cache_full"))
                    .findAny();
            Optional<String> compilationDisabled = Files.lines(Paths.get(logFilePath))
                                    .filter(s -> s.contains("compilation: disabled"))
                .findAny();

            if(codeCachefailures.isPresent())
            {
                buffer.append(codeCachefailures.get().replace("<","&lt;").replace(">","&gt;")).append("\n");
            }
            else
            {
                buffer.append("no failure reason='CodeCache is full'").append("\n");
            }

            if(compilationDisabledfailures.isPresent())
            {
                buffer.append(compilationDisabledfailures.get().replace("<","&lt;").replace(">","&gt;")).append("\n");
            }
            else
            {
                buffer.append("no failure reason='compilation is disabled'").append("\n");
            }

            if(codeCacheFull.isPresent())
            {
                buffer.append(codeCacheFull.get().replace("<","&lt;").replace(">","&gt;")).append("\n");
            }
            else
            {
                buffer.append("no code_cache_full").append("\n");
            }

            if(compilationDisabled.isPresent())
            {
                buffer.append(compilationDisabled.get().replace("<","&lt;").replace(">","&gt;")).append("\n");
            }
            else
            {
                buffer.append("no compilation: disabled").append("\n");
            }
        }


        resp.getWriter().write("<html><body><pre>" + buffer.toString() + "</pre></body></html>");
    }

    private List<String> getArgs()
    {
        return ManagementFactory.getRuntimeMXBean().getInputArguments();
    }

    private String hotspotLogFilePath()
    {
        Optional<String> unlockDiagnosticVMOptionsIsOn = getArgs()
                .stream()
                .filter(a -> a.equals("-XX:+UnlockDiagnosticVMOptions")).findAny();
        Optional<String> logCompilationIsOn = getArgs()
                .stream()
                .filter(a -> a.equals("-XX:+LogCompilation")).findAny();
        Optional<String> logFileSet = getArgs()
                .stream()
                .filter(a -> a.startsWith("-XX:LogFile=")).findAny();

        if(!unlockDiagnosticVMOptionsIsOn.isPresent())
        {
            return "UnlockDiagnosticVMOptions is not set. Compilation Logging is not active.";
        }

        if(!logCompilationIsOn.isPresent())
        {
            return "LogCompilation is not set. Compilation Logging is not active.";
        }

        if(logFileSet.isPresent())
        {
            return logFileSet.get().split("=")[1];
        }

        // fragile see http://docs.oracle.com/javase/6/docs/api/java/lang/management/RuntimeMXBean.html#getName%28%29
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        String path = System.getProperty("user.dir");

        Path defaultHotspotLogFilePath = Paths.get(path, "hotspot.log");
        if(defaultHotspotLogFilePath.toFile().exists())
        {
            return defaultHotspotLogFilePath.toFile().getAbsolutePath();
        }

        Path pidHotspotLogFilePath = Paths.get(path, "hotspot_pid" + pid + ".log");
        if(pidHotspotLogFilePath.toFile().exists())
        {
            return pidHotspotLogFilePath.toFile().getAbsolutePath();
        }

        return "Unable to find [" + defaultHotspotLogFilePath.toFile().getAbsolutePath() + "] or [" + pidHotspotLogFilePath.toFile().getAbsolutePath() + "]. Compilation Logging is not active";

    }

}