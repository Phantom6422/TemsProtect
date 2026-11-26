package net.coreprotect.bukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BukkitAdapterTest {

    @Test
    void mapsLegacyOneDotTwentyOneVersions() {
        assertEquals(21, BukkitAdapter.getAdapterVersion(1, 21, 0));
        assertEquals(21005, BukkitAdapter.getAdapterVersion(1, 21, 5));
        assertEquals(21011, BukkitAdapter.getAdapterVersion(1, 21, 11));
    }

    @Test
    void mapsCalendarVersions() {
        assertEquals(260000, BukkitAdapter.getAdapterVersion(26, 0, 0));
        assertEquals(260100, BukkitAdapter.getAdapterVersion(26, 1, 0));
        assertEquals(260200, BukkitAdapter.getAdapterVersion(26, 2, 0));
        assertEquals(260300, BukkitAdapter.getAdapterVersion(26, 3, 0));
        assertEquals(260301, BukkitAdapter.getAdapterVersion(26, 3, 1));
    }

    @Test
    void ordersPaperTwentySixThreeAtOrAboveTheAdapterThreshold() {
        assertEquals(BukkitAdapter.BUKKIT_V26_3, BukkitAdapter.getAdapterVersion(26, 3, 0));
    }
}
