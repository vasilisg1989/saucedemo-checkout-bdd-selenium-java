package infiterra.support;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StepLoggerPlugin implements ConcurrentEventListener {
    private static final Logger log = LogManager.getLogger("infiterra.StepLogger");

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, e ->
            log.info("=== START Scenario: {} ===", e.getTestCase().getName()));

        publisher.registerHandlerFor(TestStepStarted.class, e -> {
            if (e.getTestStep() instanceof PickleStepTestStep s) {
                log.info("STEP START: {}{}", s.getStep().getKeyword(), s.getStep().getText());
            }
        });

        publisher.registerHandlerFor(TestStepFinished.class, e -> {
            if (e.getTestStep() instanceof PickleStepTestStep s) {
                long ms = e.getResult().getDuration() == null ? -1
                        : e.getResult().getDuration().toMillis();
                log.info("STEP {} ({} ms): {}", e.getResult().getStatus(), ms, s.getStep().getText());
                if (e.getResult().getError() != null) {
                    log.error("STEP ERROR:", e.getResult().getError());
                }
            }
        });

        publisher.registerHandlerFor(TestCaseFinished.class, e ->
            log.info("=== END Scenario: {} (status: {}) ===",
                e.getTestCase().getName(), e.getResult().getStatus()));
    }
}

