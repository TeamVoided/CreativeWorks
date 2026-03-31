package org.teamvoided.creative_works.comands

//import com.mojang.brigadier.arguments.StringArgumentType.getString
//import com.mojang.brigadier.arguments.StringArgumentType.word
//import com.mojang.brigadier.context.CommandContext
//import com.mojang.brigadier.suggestion.Suggestions
//import com.mojang.brigadier.suggestion.SuggestionsBuilder
//import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
//import java.util.concurrent.CompletableFuture
//import java.util.function.Function
//import kotlin.math.sqrt

//object DuskDebug {
//    fun init() {
//        CommandRegistrationCallback.EVENT.register { dispatch, ctx, _ ->
//            val root = literal("dusk").build()
//            dispatch.root.addChild(root)
//
//            // (ender) just found the lang code I will move this later
//            val lang = literal("lang").executes(::lang).build()
//            root.addChild(lang)
//
//            val features = literal("features").executes { 0 }.build()
//            root.addChild(features)
//            val featNamespace = argument("namespace", word()).suggests { cont, builder ->
//                builder.listSuggestions(
//                    cont.source.server.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE)
//                        ?.keySet()?.map(Identifier::getNamespace)?.toSet() ?: setOf()
//                )
//            }.executes {
//                placeAll(it, getString(it, "namespace"), BlockPos.containing(it.getSource().position))
//            }.build()
//            features.addChild(featNamespace)
//            val featPos = argument("pos", BlockPosArgument.blockPos()).executes {
//                placeAll(it, getString(it, "namespace"), BlockPosArgument.getLoadedBlockPos(it, "pos"))
//            }.build()
//            featNamespace.addChild(featPos)
//
//        }
//    }
//
//    private fun <T> checkTranslations(iterable: Iterable<T>, function: Function<T, String>, set: MutableSet<String>) {
//        val language = Language.getInstance()
//        iterable.forEach {
//            val string = function.apply(it)
//            if (!language.has(string)) set.add(string)
//        }
//    }
//
//    fun lang(ctx: CommandContext<CommandSourceStack>): Int {
//        val src = ctx.source ?: return -1
//        val server = src.server ?: return -1
//        val set = Bootstrap.getMissingTranslations()
//
//        checkTranslations(
//            server.registryAccess().lookupOrThrow(Registries.BIOME).entrySet(),
//            { it.key.identifier().toLanguageKey("biome") },
//            set
//        )
//        set.forEach { log.error("Missing translations: {}", it) }
//        println("No lang?")
//        return 1
//    }
//
//    var SIZE = 14
//    fun placeAll(ctx: CommandContext<CommandSourceStack>, namespace: String, pos: BlockPos): Int {
//        val src = ctx.source ?: return -1
//        val world = src.level ?: return -1
//        val server = src.server ?: return -1
//        val registry = server.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
//        val allFeatures = registry?.entrySet()
//            ?.mapNotNull { if (it.key.identifier().namespace == namespace) it else null }
//        if (allFeatures == null || allFeatures.isEmpty()) {
//            src.sendFailure(Component.literal("Failed to find any features in namespace: $namespace"))
//            return -1
//        }
//
//        var failed = 0
//
//        val rowLen = sqrt(allFeatures.size.toDouble()).toInt()
//
//        for ((rowIdx, row) in allFeatures.chunked(rowLen).withIndex()) {
//            for ((colIdx, feature) in row.withIndex()) {
//                if (!placeFeature(world, pos.offset(rowIdx * SIZE, 0, colIdx * SIZE), feature.toPair())) {
//                    failed++
//                }
//            }
//        }
//
//        src.sendSystemMessage(Component.literal("Placed ${allFeatures.size} features"))
//
//        if (failed > 0) {
//            src.sendFailure(Component.literal("Failed to place $failed features"))
//            return failed
//        }
//
//        return 0
//    }
//
//    fun placeFeature(
//        world: ServerLevel,
//        pos: BlockPos,
//        feature: Pair<ResourceKey<ConfiguredFeature<*, *>>, ConfiguredFeature<*, *>>,
//    ): Boolean {
//        val oneLess = (SIZE - 1)
//        for (x in 0..<SIZE) {
//            for (z in 0..<SIZE) {
//                val isUnderground = feature.second.feature is ReefMonsterRoomFeature
//                for (y in -16..16) {
//                    world.setBlock(
//                        pos.offset(x, y, z),
//                        if (isUnderground) Blocks.STONE.defaultBlockState()
//                        else Blocks.AIR.defaultBlockState(),
//                        0
//                    )
//                }
//
//                if (x >= oneLess || z >= oneLess || x == 0 || z == 0) {
//                    world.setBlock(pos.offset(x, -1, z), Blocks.RED_CONCRETE.defaultBlockState(), 3)
//                } else {
//                    if (!isUnderground) world.setBlock(pos.offset(x, -1, z), Blocks.GRASS_BLOCK.defaultBlockState(), 3)
//                }
//            }
//        }
//        val center = pos.offset(oneLess / 2, 0, oneLess / 2)
//        var sign = Blocks.PALE_OAK_SIGN.defaultBlockState()
//        val placeResult =
//            feature.second.place(world, world.chunkSource.generator, world.getRandom(), center)
//        if (!placeResult) {
//            for (y in 0..16) {
//                world.setBlock(center.offset(0, y, 0), Blocks.RED_STAINED_GLASS.defaultBlockState(), 3)
//            }
//            sign = Blocks.MANGROVE_SIGN.defaultBlockState()
//        }
//        val infoPos = pos.offset(oneLess / 2, 0, 0)
//        world.setBlock(infoPos, sign, 3)
//        val be = world.getBlockEntity(infoPos)
//        if (be is SignBlockEntity) {
//            val txt = Component.literal("${feature.first.identifier().path}")
//            be.setText(SignText().setMessage(0, txt), true)
//            be.setText(SignText().setMessage(0, txt), false)
//        }
//        return placeResult
//    }
//
//
//    // (ender) stolen from CW
//    fun SuggestionsBuilder.listSuggestions(list: Iterable<String>?)
//            : CompletableFuture<Suggestions> {
//        val query = this.remainingLowerCase.trim().lowercase()
//        list?.filter { filterByQuery(it.trim().lowercase(), query) }?.forEach(this::suggest)
//        return this.buildFuture()
//    }
//
//    fun filterByQuery(entry: String, query: String): Boolean {
//        if (query.contains(":") && entry.contains(":")) {
//            val splitQuery = query.split(":")
//            val queryN = splitQuery[0].trim()
//            val queryP = splitQuery.drop(1).joinToString("").trim()
//            val splitEntry = entry.split(":")
//            val entryN = splitEntry[0]
//            val entryP = splitEntry.drop(1).joinToString("")
//
//            var namespaceMatches = true
//            if (!queryN.isEmpty()) {
//                namespaceMatches = entryN.contains(queryN)
//            }
//            return namespaceMatches && entryP.contains(queryP)
//        }
//        return entry.contains(query)
//    }
//}
//
//
