package no.entitas.gradle.jaxb.antextension;

import static java.text.MessageFormat.format;

import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;

/**
 * @author Stig Kleppe-Jorgensen, 2012.03.09
 * @todo fill in
 */
class ClasspathCatalogResolver extends CatalogResolver {
	public static final String URI_SCHEME_CLASSPATH = "classpath";

	@Override
	public String getResolvedEntity(String publicId, String systemId) {
		final String result = super.getResolvedEntity(publicId, systemId);

		if (result == null) {
			System.err.println(format("Could not resolve publicId [{0}], systemId [{1}]", publicId, systemId));
			return null;
		}

		try {
			final java.net.URI uri = new java.net.URI(result);
			if (URI_SCHEME_CLASSPATH.equals(uri.getScheme())) {
				final String schemeSpecificPart = uri.getSchemeSpecificPart();

				final java.net.URL resource = Thread.currentThread()
						.getContextClassLoader()
						.getResource(schemeSpecificPart);
				if (resource == null) {
					return null;
				} else {
					return resource.toString();
				}
			} else {
				return result;
			}
		} catch (java.net.URISyntaxException urisex) {

			return result;
		}
	}
}
