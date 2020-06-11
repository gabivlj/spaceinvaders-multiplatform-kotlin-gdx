package architecture.game

import architecture.engine.Game
import architecture.engine.World
import architecture.engine.structs.Text
import architecture.engine.structs.V2V3
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.my.architecture.engine.structs.GameObject

class UIManagerInGame(var secondPlayer: Player? = null) : GameObject(arrayOf(), 0.0f, 0.0f) {
    var shape: ShapeRenderer? = null
    var player: Player? = null
    lateinit var players: com.badlogic.gdx.utils.Array<Player>
    var text: Text = Text()

    init {
        World.world.instantiate(this)
    }

    override fun start() {
        text = Text()
        text.text = "0"
        text.position = Vector2()
        players = World.world.findGameObjects()
        if (players.size == 0) {
            World.world.destroy(this)
            text.stop()
            return
        }
        player = secondPlayer ?: players[0]
        if (players.size > 1 && secondPlayer == null) {
            World.world.instantiate(UIManagerInGame(players[1]))
        }
        shape = ShapeRenderer()
    }

    override fun update(dt: Float) {
        updateCamera(Game.camera)
    }

    fun updateCamera(camera: Camera) {
        val useStandardUI = players.size == 1
        val offset = if (secondPlayer == null) 30f else { 90f }
        val deg = ((player!!.hp * 360f)) / 100f
        shape!!.color = Color.GREEN
        shape!!.begin(ShapeRenderer.ShapeType.Filled)
        lateinit var posPlayer: Vector3
        if (useStandardUI) {
            shape!!.arc(550.0f, Gdx.graphics.height - 60f, 60f, 90f, deg)
            shape!!.color = Color.BROWN
            shape!!.circle(550.0f, Gdx.graphics.height - 60f, 40f)
        } else {
            posPlayer = camera.project(V2V3(player!!.position))
            shape!!.arc(5f + offset, 30f, 20f, 90f, deg)
            shape!!.color = Color.BROWN
            shape!!.circle(5f + offset, 30f, 15f)
        }
        if (player!!.currentShot != Player.CurrentShot.NORMAL) {
            shape!!.color = Color.VIOLET
            val degShoot = (360f / 100) * ((player!!.currentAmmo * 100) / player!!.currentShot.ammo)
            if (useStandardUI) {
                shape!!.arc(Gdx.graphics.width - 60f, Gdx.graphics.height - 60f, 30f, 90f, degShoot)
                shape!!.color = Color.PURPLE
                shape!!.circle(Gdx.graphics.width - 60f, Gdx.graphics.height - 60f, 18f)
            } else {
                shape!!.arc(5f + offset + 55f, Gdx.graphics.height - 60f, 55f, 90f, degShoot)
                shape!!.color = Color.PURPLE
                shape!!.circle(5f + offset + 55f, Gdx.graphics.height - 60f, 30f)
            }
        }
        shape!!.end()

        text.text = "${player!!.score}"
        text.position = player!!.position
    }

    override fun onDispose() {
        if (shape != null) {
            shape!!.dispose()
            shape = null
        }
        text.stop()
    }
}
