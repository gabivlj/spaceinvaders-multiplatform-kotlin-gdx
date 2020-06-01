package architecture.game

import architecture.engine.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter.OutputType
import com.my.architecture.engine.structs.GameObject


class MapStatics {
    companion object {
        lateinit var currentMap: Map
    }
}

class SpawningBehaviour(val map: Map = MapStatics.currentMap) : GameObject(arrayOf(), 0.0f, 0.0f, Vector2(0.0f, 0.0f)) {
    private var currentSpawner = -1
    private var enemiesLeft = 0
    private var currentTime = 0.0f
    private lateinit var player: Player
    var randomFn: Array<() -> BasicEnemy?> = arrayOf()
    val currentSpawnerItem: SpawnerMap
        get() { return map.spawners[currentSpawner] }

    var posX = 0.0f
        get() {
            val maxX = player.position.x + 100
            val minX = player.position.y - 100
            val posX = Math.random().toFloat() * (maxX - minX) + minX
            return posX
        }
    override fun start() {
        super.start()
        val players = World.world.findGameObjects<Player>()
        if (players.isEmpty) return
        player = players[0]
        randomFn = arrayOf({
            if (currentSpawnerItem.nBosses <= 0) {
                return@arrayOf null
            }
            currentSpawnerItem.nBosses--
            val enemy = EnemyFollow(Vector2(posX, 0.0f))
            enemy.observeDestroy = { enemiesLeft-- }
            enemy.position.y = enemy.height + LevelManager.level.topBounds
            enemy
        }, {
            if (currentSpawnerItem.nEnemiesFollow <= 0) return@arrayOf null
            currentSpawnerItem.nEnemiesFollow--
            val enemy = EnemyFollow(Vector2(posX, 0.0f))
            enemy.observeDestroy = { enemiesLeft-- }
            enemy.position.y = enemy.height + LevelManager.level.topBounds
            enemy
        }, {
            if (currentSpawnerItem.nEnemiesNormal <= 0) return@arrayOf null
            currentSpawnerItem.nEnemiesNormal--
            // todo: Put here multipliers of dif
            val enemy = Enemy(Vector2(posX, 0.0f), 100.0f, Config.randomSetOfPoint())
            enemy.observeDestroy = { enemiesLeft-- }
            enemy.position.y = enemy.height + LevelManager.level.topBounds
            enemy
        })
    }

    fun finishedAllSpawns(): Boolean {
        return currentSpawner >= map.spawners.size
    }

    /**
     * Returns if the current spawner is finished
     * this function will always be true if you don't call nextSpawn()
     */
    fun finishedCurrentSpawn(): Boolean {
        if (currentSpawner < 0) {
            return true
        }
        val currentSpawnerItem = map.spawners[currentSpawner]
        return enemiesLeft <= 0 || currentSpawnerItem.nEnemiesFollow + currentSpawnerItem.nEnemiesNormal + currentSpawnerItem.nBosses == 0
    }

    fun passToNextSpawn(): Boolean {
        return enemiesLeft <= 0 || currentSpawner == -1
    }

    private fun nextSpawn() {
        // We reached the end
        if (currentSpawner == map.spawners.size - 1) {
            currentSpawner++
            return
        }
        // Else we keep going
        val currentSpawnerItem = map.spawners[++currentSpawner]
        LevelManager.level.cameraSpeed = currentSpawnerItem.velocityCamera
        enemiesLeft = currentSpawnerItem.nEnemiesFollow + currentSpawnerItem.nEnemiesNormal + currentSpawnerItem.nBosses
        currentTime = 0.0f
    }

    private fun handleSpawn() {
        var enemyEnd: BasicEnemy? = null
        // TODO: There might be a better algo
        while (enemyEnd == null) { enemyEnd = randomFn.random()() }
        World.world.instantiate(enemyEnd)
    }

    override fun update(dt: Float) {
        // This is when we finish the phase
        if (finishedAllSpawns()) {
            Gdx.app.log("Finished all spawns", "Yes, we did")
            return
        }
        // If we should pass to the next spawn (all the enemies are dead)
        if (passToNextSpawn()) {
            nextSpawn()
            return
        }
        // Check if the current spawn is finished spawning (don't let the game spawn more enemies when the user is still
        // killing!
        if (finishedCurrentSpawn()) {
            return
        }
        if (currentTime < currentSpawnerItem.timeBetweenSpawns) {
            currentTime += dt
            return
        }
        val currentSpawnerItem = map.spawners[currentSpawner]
        currentTime = 0.0f
        handleSpawn()
    }
}

/*
"{\n" +
                                "  \"class\": \"Maps\",\n" +
                                "  \"maps\": [\n" +
                                "    {\n" +
                                "      \"class\": \"Map\",\n" +
                                "      \"spawners\": [{\n" +
                                "        \"class\": \"SpawnerMap\",\n" +
                                "        \"nBosses\": 1,\n" +
                                "        \"nEnemiesFollow\": 10,\n" +
                                "        \"nEnemiesNormal\": 10,\n" +
                                "        \"timeBetweenSpawns\": 1.0,\n" +
                                "        \"velocityCamera\": 2.0\n" +
                                "      }],\n" +
                                "      \"image\": \"??\",\n" +
                                "      \"background\": \"assets/background/NebulaAqua-Pink.png\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}"
 */
class LoaderSpawner {
    companion object {
        fun Load(): Maps? {

            return try {
                val json = Json()
                json.setTypeName(null)
                json.setUsePrototypes(false)
                json.ignoreUnknownFields = true
                json.setOutputType(OutputType.json)
                val file = Gdx.files.internal("assets/m.json")
                val buff = file.file().readText()
                val maps: Maps = json.fromJson(
                        Maps::class.java,
                        buff
                )
                maps
            } catch (e: Throwable) {
                Gdx.app.log("$e", "$e")
                Gdx.app.log("LoaderSpawner $this","Error consuming from JSON from map. ${e.message}")
                null
            }
        }
    }
}

class Maps  {
    var maps: Array<Map> = arrayOf()

    constructor (mapz: Array<Map>) {
        maps = mapz
    }
    constructor() {}
}

class Map {
    var spawners: Array<SpawnerMap> = arrayOf()
    var image: String = ""
    var background: String = ""

    constructor(spawnersArr: Array<SpawnerMap>, imageNew: String, backgroundNew: String) {
        spawners = spawnersArr
        image = imageNew
        background = backgroundNew
    }
    constructor() {}
}

class SpawnerMap {
    var nEnemiesFollow: Int = 0
    var nEnemiesNormal: Int = 0
    var nBosses: Int = 0
    var velocityCamera: Float = 0f
    var timeBetweenSpawns: Float = 0f
    constructor(nEnemiesFollowParam: Int, nEnemiesNormalParam: Int, nBossesParam: Int, vel: Float, timeBetweenSpawnsParam: Float) {
        nEnemiesFollow = nEnemiesFollowParam
        nEnemiesNormal = nEnemiesNormalParam
        nBosses = nBossesParam
        velocityCamera = vel
        timeBetweenSpawns = timeBetweenSpawnsParam
    }
    constructor() {}
}