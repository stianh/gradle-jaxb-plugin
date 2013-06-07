package no.entitas.gradle.jaxb.antextension;

import com.sun.tools.xjc.XJC2Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

/**
 * @author Stig Kleppe-Jorgensen, 2012.03.06
 */
public final class XJCTask extends XJC2Task {
    public XJCTask() {
        super();
        options.entityResolver = new ClasspathCatalogResolver();
        options.debugMode = true;
    }

    public void setBinding(final String binding) {
        if (binding == null) {
            return;
        }
        File dir = new File(binding);
        if (!dir.exists()) {
            return;
        }
        FileSet fs = new FileSet();
        fs.setDir(dir);
        fs.setIncludes("*.xjb");
        addConfiguredBinding(fs);
    }

}

