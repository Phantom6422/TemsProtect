package net.coreprotect.utility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VersionUtilsTest {

    @Test
    void comparesNumericSegments() {
        assertTrue(VersionUtils.newVersion("26.2", "26.3"));
        assertTrue(VersionUtils.newVersion("26.3", "26.3.1"));
        assertFalse(VersionUtils.newVersion("26.3.1", "26.3"));
        assertFalse(VersionUtils.newVersion("26.3", "26.3"));
    }

    @Test
    void ignoresPreReleaseAndBuildMetadata() {
        assertFalse(VersionUtils.newVersion("26.3.0-beta.1", "26.3.0"));
        assertFalse(VersionUtils.newVersion("26.3.0+build.42", "26.3.0"));
        assertTrue(VersionUtils.newVersion("26.2.9", "26.3.0-rc.1"));
    }

    @Test
    void handlesMissingSegmentsAsZero() {
        assertFalse(VersionUtils.newVersion("26.3", "26.3.0"));
        assertTrue(VersionUtils.newVersion("26.3", "26.3.0.1"));
    }

    @Test
    void rejectsNonNumericVersions() {
        assertFalse(VersionUtils.newVersion("v26.3", "26.4"));
        assertFalse(VersionUtils.newVersion("", "26.4"));
        assertFalse(VersionUtils.newVersion("26.3", "latest"));
    }
}
