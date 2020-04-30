package architecture.game

import architecture.engine.World
import architecture.engine.structs.Text
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class UIManager : GameObject(arrayOf(), 0.0f, 0.0f) {
    val shape: ShapeRenderer = ShapeRenderer()
    var player: Player? = null
    var text: Text = Text()

    init {
        World.world.instantiate(this)
    }

    override fun start() {
        text.text = "0"
        text.position = Vector2()

        player = World.world.findGameObjects<Player>()[0]
        if (player == null) World.world.destroy(this)

    }

    override fun update(dt: Float) {
        val deg = ((player!!.hp * 360f)) / 100f
        shape.color = Color.GREEN
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.arc(550.0f, Gdx.graphics.height - 60f, 60f, 90f, deg)
        shape.color = Color.BROWN
        shape.circle(550.0f, Gdx.graphics.height - 60f, 40f)

        if (player!!.currentShot != Player.CurrentShot.NORMAL) {
            shape.color = Color.VIOLET
            val degShoot =  (360f / 100) * ((player!!.currentAmmo * 100) / player!!.currentShot.ammo)
            shape.arc(Gdx.graphics.width - 60f, Gdx.graphics.height - 60f, 60f, 90f, degShoot)
            shape.color = Color.PURPLE
            shape.circle(Gdx.graphics.width - 60f, Gdx.graphics.height - 60f, 40f)
        }
        shape.end()

        text.text = "${player!!.score}"
        text.position = player!!.position
    }

}