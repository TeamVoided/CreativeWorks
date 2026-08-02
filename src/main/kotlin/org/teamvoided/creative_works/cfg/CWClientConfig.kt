package org.teamvoided.creative_works.cfg

import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt
import org.teamvoided.creative_works.CreativeWorks.MODID
import org.teamvoided.creative_works.CreativeWorks.id

class CWClientConfig : Config(id("${MODID}_client")) {

    var enableBaseComponents = true

    var maxLengthBeforeWrap = ValidatedInt(64, 100, 16)
    var maxLengthBeforeCollapse = ValidatedInt(256, 1024, 16)
    var allowCollapse = true

    var particleHitboxes = ParticleHitboxState.SYNC_WITH_HITBOXES

}