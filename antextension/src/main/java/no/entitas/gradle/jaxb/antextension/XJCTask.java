package no.entitas.gradle.jaxb.antextension;

import com.sun.tools.xjc.XJC2Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

/**
 * @author Stig Kleppe-Jorgensen, 2012.03.06
 */
public class XJCTask extends XJC2Task {

    public XJCTask() {
        super();
        options.entityResolver = new ClasspathCatalogResolver();
        options.debugMode = true;
    }

    public void setBinding(final String bindingSource) {
        if (isEmpty(bindingSource)) {
            return;
        }
        File bindingFile = getProject().resolveFile(bindingSource);
        if (!bindingFile.exists()) {
            return;
        }
        if (bindingFile.isDirectory()) {
            FileSet fs = new FileSet();
            fs.setDir(bindingFile.getAbsoluteFile());
            fs.setIncludes("**/*.xjb");
            addConfiguredBinding(fs);
        } else {
            super.setBinding(bindingFile.getAbsolutePath());
        }
    }

    public void setCatalog(final File catalog) {
        if (catalog == null) {
            return;
        }
        if (!catalog.exists()) {
            return;
        }
        super.setCatalog(catalog);
    }

    public void setPackageName(final String packageName) {
        if (isEmpty(packageName)) {
            return;
        }
        log("package name is [" + packageName + "].");
        super.setPackage(packageName);
    }

    private boolean isEmpty(final String value) {
        return value == null || value.isEmpty();
    }

}

