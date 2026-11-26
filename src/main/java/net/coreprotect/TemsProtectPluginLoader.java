package net.coreprotect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Paper plugin loader.
 *
 * <p>Paper plugins do not read the {@code libraries} section from
 * {@code plugin.yml}. DuckDB is resolved here through Paper's Maven library
 * resolver so it is downloaded once by the server and added to the plugin
 * classpath, keeping the plugin jar small and avoiding a shaded native driver.</p>
 */
public final class TemsProtectPluginLoader implements PluginLoader {

    private static final String DUCKDB_GROUP = "org.duckdb";
    private static final String DUCKDB_ARTIFACT = "duckdb_jdbc";
    private static final String DEFAULT_DUCKDB_VERSION = "1.4.5.0";

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(new Dependency(
                new DefaultArtifact(DUCKDB_GROUP + ":" + DUCKDB_ARTIFACT + ":" + duckdbVersion()), null));
        resolver.addRepository(new RemoteRepository.Builder(
                "central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        classpathBuilder.addLibrary(resolver);
    }

    private static String duckdbVersion() {
        Properties properties = new Properties();
        try (InputStream input = TemsProtectPluginLoader.class.getResourceAsStream("/temsprotect.properties")) {
            if (input != null) {
                properties.load(input);
            }
        }
        catch (IOException ignored) {
            // Fall back to the build-time default below.
        }

        String version = properties.getProperty("duckdb.version", DEFAULT_DUCKDB_VERSION).trim();
        return version.isEmpty() ? DEFAULT_DUCKDB_VERSION : version;
    }
}
