package architecture.game


import architecture.engine.Game
import architecture.engine.Renderer
import architecture.engine.World
import architecture.engine.structs.PhysicalJoystick
import architecture.engine.structs.ToListen
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.my.architecture.engine.structs.GameObject

class Spawn (val positionToSpawn: Float, val enemies: Array<BasicEnemy>, val timeBetweenSpawns: Float, val offsetPlayerX: Float): GameObject(arrayOf(), 0.0f, 0.0f) {
    var time = timeBetweenSpawns
    var currentEnemy = 0

    lateinit var player: Player

    var timeBetweenSpawnsOfPlanetsAcc = 5.0f
    val timeBetweenSpawnsOfPlanets = 10f

    override fun start() {
        player = World.world.findGameObjects<Player>().last()
    }

    override fun update(dt: Float) {
        time += dt
        timeBetweenSpawnsOfPlanetsAcc += dt

        if (timeBetweenSpawnsOfPlanetsAcc > timeBetweenSpawnsOfPlanets) {
            val posX = Math.random().toFloat() * (LevelManager.level.rightBounds - LevelManager.level.leftBounds) + LevelManager.level.leftBounds
            val planet = Planet(Vector2())
            val posY = LevelManager.level.topBounds + planet.height
            World.world.instantiate(planet)
            planet.position.x = posX
            planet.position.y = posY
            timeBetweenSpawnsOfPlanetsAcc = 0.0f
        }

        if (timeBetweenSpawns <= time) {
            time = 0.0f
//            val enemy = World.world.instantiate(enemies[currentEnemy])
//            enemy.position = Vector2(player.position.x + offsetPlayerX, LevelManager.level.topBounds)
            currentEnemy++
        }

        if (currentEnemy >= enemies.size) {
            World.world.destroy(this)
        }

    }
}


/**
 * @NOTE THIS class should be the last thing to be initialized.
 */
class LevelManager : GameObject(arrayOf(), 0.0f, 0.0f){

    val enemiesToSpawn: MutableList<Spawn> = mutableListOf(
            Spawn(
                    1500.0f,
                    Config.randomConfig(10),
                    2.2f,
                    -200f
            ),
            Spawn(
                    1800.0f,
                    Config.randomConfig(10),
                    4.2f,
                    200f
            ),
            Spawn(
                    2000.0f,
                    Config.randomConfig(20),
                    2.2f,
                    -200f
            ),
            Spawn(
                    2300.0f,
                    Config.randomConfig(20),
                    2.2f,
                    400f
            ),
            Spawn(
                    2800.0f,
                    Config.randomConfig(20),
                    2.2f,
                    400f
            ),
            Spawn(
                    3000.0f,
                    Config.randomConfig(20),
                    2.2f,
                    -200f
            )
    )

    var topBounds: Float = 0.0f
    var bottomBounds: Float = 0.0f
    var rightBounds: Float = 0.0f
    var leftBounds: Float = 0.0f

    lateinit var players: com.badlogic.gdx.utils.Array<Player>
    lateinit var camera: Camera
    var currentSpawner = 0

    init {
        World.world.instantiate(this)
        level = this
//        addPlayer()
    }

    companion object {
        lateinit var level: LevelManager

        fun outOfBounds(position: Vector2, height: Float, width: Float): Boolean {
            return position.y + height < LevelManager.level.bottomBounds || position.y > LevelManager.level.topBounds
                    || position.x > LevelManager.level.rightBounds || position.x + width < LevelManager.level.leftBounds
        }
    }

    override fun update(dt: Float) {
        cameraWork(dt)
        spawner()
    }

    override fun start() {
        players = World.world.findGameObjects()
        camera = Game.camera
        for ((i, camera) in Game.renderer.cameras.withIndex()) {
            camera.position.x = (Background.currentBackground.width / 2) - camera.viewportWidth / 2
            camera.position.y = camera.viewportHeight
            players[i].position = Vector2(camera.position.x, camera.position.y)
            camera.update()
        }
        level = this
    }

    private fun spawner() {
        if (currentSpawner >= enemiesToSpawn.size) return
//        Gdx.app.log("SPAWNER", "${camera.position.y + camera.viewportHeight / 2}")
        if (camera.position.y + camera.viewportHeight / 2 >= enemiesToSpawn[currentSpawner].positionToSpawn) {
            World.world.instantiate(enemiesToSpawn[currentSpawner])
            currentSpawner++
        }
    }

    var cameraSpeed = 20.0f

    private fun cameraWork(dt: Float) {
        for ((i, camera) in Game.renderer.cameras.withIndex()) {
            camera.position.y += dt * cameraSpeed
            topBounds = camera.viewportHeight / 2 + camera.position.y
            leftBounds = Background.currentBackground.position.x
            rightBounds = Background.currentBackground.position.x + Background.currentBackground.width
            bottomBounds = camera.position.y - camera.viewportHeight / 2
            val lerp = 3f
            val position: Vector3 = camera.position
            position.x += (players[i].position.x - position.x) * lerp * dt
            camera.position.x = MathUtils.clamp(camera.position.x, leftBounds + camera.viewportWidth / 2, rightBounds - camera.viewportWidth / 2)
            camera.update()
        }
    }

    fun addPlayer() {
        val camera = Game.renderer.addCamera()
        val pos = camera.position
        pos.x = Game.camera.position.x
        pos.y = Game.camera.position.y
        pos.z = Game.camera.position.z

        val joystick01 = World.world.instantiate(PhysicalJoystick(
                ToListen.NONE,
                arrayOf(Input.Keys.H, Input.Keys.F, Input.Keys.T, Input.Keys.G),
                2.5f
        ))
        val joystick02 = World.world.instantiate(PhysicalJoystick(
                ToListen.NONE,
                arrayOf(Input.Keys.L, Input.Keys.J, Input.Keys.I, Input.Keys.K),
                2.5f
        ))
        val joystick03 = World.world.instantiate(PhysicalJoystick(
                ToListen.NONE,
                arrayOf(Input.Keys.Y, -1, Input.Keys.Y, -1),
                2.5f
        ))
        val player = World.world.instantiate(Player(SpaceInvaders.sprites.slice(0..2).toTypedArray(), joystick01, joystick02, joystick03))
        player.position.x = camera.position.x
        player.position.y = camera.position.y
        player.tint = Color.PURPLE
    }

}
