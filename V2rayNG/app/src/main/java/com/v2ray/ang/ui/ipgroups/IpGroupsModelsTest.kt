package com.v2ray.ang.ui.ipgroups

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class IpGroupsModelsTest {
    private fun v(guid: String, ping: Long?) = IpVariantUi(guid, guid, "VLESS", "443", ping)

    @Test fun equalPingUsesGuidTieBreak() {
        assertEquals("a", bestVariant(listOf(v("b", 80), v("a", 80)))?.guid)
    }

    @Test fun failedAndUntestedAreNotBest() {
        assertNull(bestVariant(listOf(v("a", null), v("b", -1), v("c", 0))))
    }

    @Test fun lowestPositiveWins() {
        assertEquals("b", bestVariant(listOf(v("a", 90), v("b", 40)))?.guid)
    }

    @Test fun sameIpDifferentSubscriptionsStaySeparate() {
        val groups = IpGroupsPrototypeData.subscriptions.flatMap { it.groups }.filter { it.ip == "192.0.2.10" }
        assertEquals(2, groups.size)
    }

    @Test fun customStaysOutsideGroups() {
        assertEquals("CUSTOM", IpGroupsPrototypeData.subscriptions.first().standaloneVariants.single().protocol)
    }
}
