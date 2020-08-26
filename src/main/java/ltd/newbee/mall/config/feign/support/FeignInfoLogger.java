package ltd.newbee.mall.config.feign.support;

import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Logs to SLF4J at the info level, if the underlying logger has info logging enabled.  The
 * underlying logger can be specified at construction-time, defaulting to the logger for {@link
 * feign.Logger}.
 */
@Component
public class FeignInfoLogger extends feign.Logger {

    private final Logger logger;

    public FeignInfoLogger() {
        this(feign.Logger.class);
    }

    public FeignInfoLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public FeignInfoLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }

    FeignInfoLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (logger.isInfoEnabled()) {
            super.logRequest(configKey, logLevel, request);
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response,
                                              long elapsedTime) throws IOException {
        if (logger.isInfoEnabled()) {
            return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
        }
        return response;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Not using SLF4J's support for parameterized messages (even though it would be more efficient) because it would
        // require the incoming message formats to be SLF4J-specific.
        if (logger.isInfoEnabled()) {
            logger.info(String.format(methodTag(configKey) + format, args));
        }
    }
}

