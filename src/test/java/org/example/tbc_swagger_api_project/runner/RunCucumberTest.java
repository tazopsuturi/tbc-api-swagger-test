package org.example.tbc_swagger_api_project.runner;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Entry point for {@code mvn test}. Glue, plugins and other Cucumber options live in
 * {@code junit-platform.properties}; filter by tag with {@code -Dcucumber.filter.tags="@smoke"}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class RunCucumberTest {
}
