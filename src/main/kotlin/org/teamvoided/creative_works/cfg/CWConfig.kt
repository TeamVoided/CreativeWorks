package org.teamvoided.creative_works.cfg

import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor
import org.teamvoided.creative_works.CreativeWorks.MODID
import org.teamvoided.creative_works.CreativeWorks.id
import java.awt.Color

class CWConfig : Config(id(MODID)) {

    @Suppress("unused")
    var textColors = ConfigGroup()
    var mainColor = ValidatedColor(Color(0xDDDDDD))
    var secondaryColor = ValidatedColor(Color(0xAAAAAA))

    @ConfigGroup.Pop
    var warningColor = ValidatedColor(Color(0xeb6666))

}