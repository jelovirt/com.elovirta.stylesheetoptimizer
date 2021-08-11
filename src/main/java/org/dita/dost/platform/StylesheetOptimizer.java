package org.dita.dost.platform;

import org.dita.dost.exception.DITAOTException;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.util.FileUtils;
import org.dita.dost.util.XMLUtils;
import org.xml.sax.XMLFilter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static java.lang.Boolean.parseBoolean;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.dita.dost.util.Configuration.configuration;

public class StylesheetOptimizer implements CustomIntegrator {

    public static final String XSL_EXTENSION = ".xsl";
    public static final String TEMPLATE_PREFIX = "_template.";
    public static final String TEMPLATE_XSL_EXTENSION = TEMPLATE_PREFIX + "xsl";
    public static final String ORIGINAL_XSL_EXTENSION = "_original" + XSL_EXTENSION;
    public static final String OPTIMIZED_XSL_EXTENSION = ".optimized" + XSL_EXTENSION;

    private boolean override;
    private DITAOTLogger logger;
    private File ditaDir;
    private XMLUtils xmlUtils;

    public StylesheetOptimizer() {
        this.override = parseBoolean(configuration.getOrDefault("stylesheet.override", "true"));
    }

    @Override
    public void setLogger(final DITAOTLogger logger) {
        this.logger = logger;
    }

    @Override
    public void setDitaDir(File ditaDir) {
        this.ditaDir = ditaDir;
    }

    @Override
    public void process() throws Exception {
        xmlUtils = new XMLUtils();
        xmlUtils.setLogger(logger);
        generateOptimizedStylesheets(ditaDir);
    }

    private void generateOptimizedStylesheets(final File dir) throws IOException {
        for (final File child : dir.listFiles()) {
            if (child.isDirectory()) {
                generateOptimizedStylesheets(child);
            } else if (child.getName().endsWith(XSL_EXTENSION)
                    && !child.getName().endsWith(ORIGINAL_XSL_EXTENSION)
                    && !child.getName().endsWith(TEMPLATE_XSL_EXTENSION)
                    && !child.getName().endsWith(OPTIMIZED_XSL_EXTENSION)) {
                generateOptimizedStylesheet(child);
            }
        }
    }

    private void generateOptimizedStylesheet(final File input) throws IOException {
        logger.debug("Optimize " + input.getAbsolutePath());
        final StylesheetOptimizerFilter f = new StylesheetOptimizerFilter(override);
        f.setLogger(logger);

        if (override) {
            final File backup = new File(FileUtils.replaceExtension(input.getAbsolutePath(), ORIGINAL_XSL_EXTENSION));
            final File template = new File(FileUtils.replaceExtension(input.getAbsolutePath(), TEMPLATE_XSL_EXTENSION));
            if (template.exists()) {
                copyFile(input, backup);
            } else if (backup.exists()) {
                copyFile(backup, input);
            } else {
                copyFile(input, backup);
            }

            try {
                xmlUtils.transform(backup, input, Collections.singletonList((XMLFilter) f));
            } catch (final DITAOTException e) {
                throw new IOException("Failed to process " + input.getAbsolutePath(), e);
            }
        } else {
            final File output = new File(FileUtils.replaceExtension(input.getAbsolutePath(), OPTIMIZED_XSL_EXTENSION));
            try {
                xmlUtils.transform(input, output, Collections.singletonList((XMLFilter) f));
            } catch (final DITAOTException e) {
                throw new IOException("Failed to process " + input.getAbsolutePath(), e);
            }
        }
    }

}
