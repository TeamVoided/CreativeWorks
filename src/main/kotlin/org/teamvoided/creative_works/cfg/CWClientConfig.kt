package org.teamvoided.creative_works.cfg

import me.fzzyhmstrs.fzzy_config.config.Config
import org.teamvoided.creative_works.CreativeWorks.MODID
import org.teamvoided.creative_works.CreativeWorks.id

class CWClientConfig : Config(id("${MODID}_client")) {

    var enableBaseComponents = true

    var particleHitboxes = ParticleHitboxState.SYNC_WITH_HITBOXES

}