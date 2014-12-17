package com.googlecode.jslint4java.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;

/**
 * Write a report formatted by a {@link JSLintResultFormatter} to a file. You
 * should call the methods in this order:
 * <ol>
 * <li>{@link #open()}
 * <li>{@link #report(JSLintResult)}
 * <li>{@link #close()}
 * </ol>
 *
 * <p>
 * If you don't call close(), you won't see any output.
 */
public class ReportWriterImpl implements ReportWriter {

    private final File reportFile;
    private final JSLintResultFormatter formatter;
    private BufferedWriter writer;

    public ReportWriterImpl(File reportFile, JSLintResultFormatter formatter) {
        this.reportFile = reportFile;
        this.formatter = formatter;
    }

    public void close() {
        try {
            // writer may be null if we exploded whilst creating it.
            if (formatter.footer() != null && writer != null) {
                writer.write(formatter.footer());
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            Closeables.closeQuietly(writer);
        }
    }

    /** The file the report is written to. */
    public File getReportFile() {
        return reportFile;
    }

    public void open() {
        try {
            Files.createParentDirs(reportFile);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile),
                    Charset.forName("UTF-8")));
            if (formatter.header() != null) {
                writer.write(formatter.header());
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public void report(JSLintResult result) {
        try {
            writer.write(formatter.format(result));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
