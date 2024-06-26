package com.github.sylvainlaurent.maven.swaggervalidator;

import com.github.sylvainlaurent.maven.swaggervalidator.instrumentation.Instrumentation;
import com.github.sylvainlaurent.maven.swaggervalidator.service.ValidationService;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.Collections;

@Mojo(name = "validate", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.TEST)
public class ValidateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}", required = true, readonly = true)
    private File basedir;

    @Parameter
    private String[] includes;

    @Parameter
    private String[] excludes;

    @Parameter(defaultValue = "true")
    private boolean verbose;

    @Parameter
    private String customModelValidatorsPackage;

    @Parameter
    private String customPathValidatorsPackage;

    @Parameter(defaultValue = "true")
    private boolean failOnErrors;

    @Parameter
    private String[] customMimeTypes;

    private final ValidationService validationService = new ValidationService();

    @Override
    public void execute() throws MojoExecutionException {

        Instrumentation.init();

        validationService.setCustomModelValidatorsPackage(customModelValidatorsPackage);
        validationService.setCustomPathValidatorsPackage(customPathValidatorsPackage);
        validationService.setCustomMimeTypes(customMimeTypes);

        final File[] files = getFiles();
        boolean encounteredError = false;
        int errorCount = 0;

        for (final File file : files) {
            if (verbose) {
                getLog().info("Processing file " + file);
            }
            final ValidationResult result = validationService.validate(file);
            if (result.hasError()) {
                encounteredError = true;
            }

            Collections.sort(result.getMessages());

            for (final String msg : result.getMessages()) {
                errorCount++;
                getLog().error(msg);
            }
        }

        if (encounteredError && failOnErrors) {
            throw new MojoExecutionException("Some files are not valid, see previous logs. Encountered " + errorCount + " errors.");
        }
    }

    private File[] getFiles() {

        final DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(basedir);
        if (includes != null && includes.length > 0) {
            ds.setIncludes(includes);
        }
        if (excludes != null && excludes.length > 0) {
            ds.setExcludes(excludes);
        }
        ds.scan();
        final String[] filePaths = ds.getIncludedFiles();
        final File[] files = new File[filePaths.length];

        for (int i = 0; i < filePaths.length; i++) {
            files[i] = new File(basedir, FilenameUtils.normalize(filePaths[i]));
        }

        return files;
    }
}
