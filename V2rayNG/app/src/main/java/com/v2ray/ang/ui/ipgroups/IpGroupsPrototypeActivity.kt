package com.v2ray.ang.ui.ipgroups

import android.content.Intent
import androidx.compose.runtime.Composable
import com.v2ray.ang.ui.base.BaseComponentActivity
import com.v2ray.ang.ui.perappproxy.PerAppProxyActivity
import com.v2ray.ang.ui.routing.RoutingSettingActivity
import com.v2ray.ang.ui.settings.SettingsActivity

/**
 * Isolated Stage-1 entry point (debug builds only, via app/src/debug/AndroidManifest.xml).
 * It does not replace MainActivity and does not start the VPN core.
 * Existing settings screens are opened only for navigation preview; their behavior is unchanged.
 */
class IpGroupsPrototypeActivity : BaseComponentActivity() {
    @Composable override fun ScreenContent() {
        IpGroupsPrototypeScreen(
            onOpenRouting = { startActivity(Intent(this, RoutingSettingActivity::class.java)) },
            onOpenApps = { startActivity(Intent(this, PerAppProxyActivity::class.java)) },
            onOpenSettings = { startActivity(Intent(this, SettingsActivity::class.java)) },
            onExitPrototype = { finish() },
        )
    }
}
