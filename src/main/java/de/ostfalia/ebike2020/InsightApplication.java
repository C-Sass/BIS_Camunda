package de.ostfalia.ebike2020;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;

/**
 * Process Application exposing this application's resources the process engine.
 */
@SuppressWarnings("deprecation")
@ProcessApplication("insight")
public class InsightApplication extends ServletProcessApplication {
    public InsightApplication() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
    }
}
