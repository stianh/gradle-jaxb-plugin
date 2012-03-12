package no.entitas.gradle.jaxb.antextension;

import com.sun.tools.xjc.XJC2Task;

/**
 * @author Stig Kleppe-Jorgensen, 2012.03.06
 * @todo fill in
 */
public class XJCTask extends XJC2Task {
    public XJCTask() {
        super();
        options.entityResolver = new ClasspathCatalogResolver();
        options.debugMode = true;
    }
}

