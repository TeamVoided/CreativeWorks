package org.teamvoided.creative_works

import me.fzzyhmstrs.fzzy_config.annotations.NonSync
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import org.teamvoided.creative_works.CreativeWorks.MODID
import org.teamvoided.creative_works.CreativeWorks.id

class CWConfig : Config(id(MODID)) {
    @Suppress("unused")
    var clientGroup = ConfigGroup("client", false)

    @NonSync
    @ConfigGroup.Pop
    var enableBaseComponents = true
}