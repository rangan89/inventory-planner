package fk.retail.ip.manager.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.util.Size;
import io.dropwizard.validation.ValidationMethod;

import java.util.TimeZone;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Class declaration for size based rotation
 */
@JsonTypeName("file-size-rolled")
public class SizeBasedRollingFileAppenderFactory extends AbstractAppenderFactory {

    @NotNull
    private String currentLogFilename;

    private boolean archive = true;

    private String archivedLogFilenamePattern;

    @Min(1)
    private int archivedFileCount = 5;

    private Size maxFileSize;

    @NotNull
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");

    @JsonProperty
    public String getCurrentLogFilename() {
        return currentLogFilename;
    }

    @JsonProperty
    public void setCurrentLogFilename(String currentLogFilename) {
        this.currentLogFilename = currentLogFilename;
    }

    @JsonProperty
    public boolean isArchive() {
        return archive;
    }

    @JsonProperty
    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    @JsonProperty
    public String getArchivedLogFilenamePattern() {
        return archivedLogFilenamePattern;
    }

    @JsonProperty
    public void setArchivedLogFilenamePattern(String archivedLogFilenamePattern) {
        this.archivedLogFilenamePattern = archivedLogFilenamePattern;
    }

    @JsonProperty
    public int getArchivedFileCount() {
        return archivedFileCount;
    }

    @JsonProperty
    public void setArchivedFileCount(int archivedFileCount) {
        this.archivedFileCount = archivedFileCount;
    }

    @JsonProperty
    public Size getMaxFileSize() {
        return maxFileSize;
    }

    @JsonProperty
    public void setMaxFileSize(Size maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @JsonProperty
    public TimeZone getTimeZone() {
        return timeZone;
    }

    @JsonProperty
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @JsonIgnore
    @ValidationMethod(message = "must have archivedLogFilenamePattern if archive is true")
    public boolean isValidArchiveConfiguration() {
        return !archive || (archivedLogFilenamePattern != null);
    }

    @JsonIgnore
    @ValidationMethod(
            message = "when specifying maxFileSize, archivedLogFilenamePattern must contain %i")
    public boolean isValidForMaxFileSizeSetting() {
        return !archive || maxFileSize == null
                || (archivedLogFilenamePattern != null && archivedLogFilenamePattern.contains("%i"));
    }

    /**
     * isMaxFileSizeSettingSpecified.
     *
     * @return boolean isMaxFileSizeSettingSpecified
     */
    @JsonIgnore
    @ValidationMethod(
            message = "when archivedLogFilenamePattern contains %i, maxFileSize must be specified")
    public boolean isMaxFileSizeSettingSpecified() {
        return !archive || !(archivedLogFilenamePattern != null && archivedLogFilenamePattern
                .contains("%i"))
                || maxFileSize != null;
    }

    @Override
    public Appender<ILoggingEvent> build(
            LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
        final FileAppender<ILoggingEvent> appender = buildAppender(context);
        appender.setName("file-appender");

        appender.setAppend(true);
        appender.setContext(context);

        final LayoutWrappingEncoder<ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(layout == null ? buildLayout(context, timeZone) : layout);
        appender.setEncoder(layoutEncoder);

        appender.setPrudent(false);
        addThresholdFilter(appender, threshold);
        appender.stop();
        appender.start();

        return wrapAsync(appender);
    }

    protected FileAppender<ILoggingEvent> buildAppender(LoggerContext context) {
        if (archive) {
            final RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
            appender.setFile(currentLogFilename);

            if (maxFileSize != null && !archivedLogFilenamePattern.contains("%d")) {
                final FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
                final SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy =
                        new SizeBasedTriggeringPolicy<>();
                triggeringPolicy.setMaxFileSize(String.valueOf(maxFileSize.toBytes()));
                triggeringPolicy.setContext(context);
                rollingPolicy.setContext(context);
                rollingPolicy.setMaxIndex(getArchivedFileCount());
                rollingPolicy.setFileNamePattern(getArchivedLogFilenamePattern());
                appender.setRollingPolicy(rollingPolicy);
                appender.setTriggeringPolicy(triggeringPolicy);
                rollingPolicy.setParent(appender);
                rollingPolicy.start();
                return appender;
            } else {
                final TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> triggeringPolicy;
                if (maxFileSize == null) {
                    triggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<>();
                } else {
                    final SizeAndTimeBasedFNATP<ILoggingEvent> maxFileSizeTriggeringPolicy =
                            new SizeAndTimeBasedFNATP<>();
                    maxFileSizeTriggeringPolicy.setMaxFileSize(String.valueOf(maxFileSize.toBytes()));
                    triggeringPolicy = maxFileSizeTriggeringPolicy;
                }
                triggeringPolicy.setContext(context);

                final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
                rollingPolicy.setContext(context);
                rollingPolicy.setFileNamePattern(archivedLogFilenamePattern);
                rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(
                        triggeringPolicy);
                triggeringPolicy.setTimeBasedRollingPolicy(rollingPolicy);
                rollingPolicy.setMaxHistory(archivedFileCount);

                appender.setRollingPolicy(rollingPolicy);
                appender.setTriggeringPolicy(triggeringPolicy);

                rollingPolicy.setParent(appender);
                rollingPolicy.start();
                return appender;
            }
        }

        final FileAppender<ILoggingEvent> appender = new FileAppender<>();
        appender.setFile(currentLogFilename);
        return appender;
    }
}
