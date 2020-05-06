package architecture.game

import architecture.engine.World
import architecture.engine.structs.Text
import architecture.engine.structs.UIButton
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class UIManagerInGame : GameObject(arrayOf(), 0.0f, 0.0f) {
    val shape: ShapeRenderer = ShapeRenderer()
    var player: Player? = null
    var text: Text = Text()

    /*
    val button: UIButton = UIButton(
            SpaceInvaders.sprites.slice(0..3).toTypedArray(),
            300f,
            100f,
            Vector2(Gdx.graphics.width / 2f, Gdx.graphics.height / 2f),
            "Test button",
            Vector2()) {
                it.text = "Clicked"
                Gdx.app.log("TEXT_CHANGE", "Click")
            }
    */

    init {
        World.world.instantiate(this)

        // World.world.instantiate(button)
    }

    override fun start() {
        text.text = "0"
        text.position = Vector2()

        player = World.world.findGameObjects<Player>()[0]
        if (player == null) {
            World.world.destroy(this)
            text.stop()
        }

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