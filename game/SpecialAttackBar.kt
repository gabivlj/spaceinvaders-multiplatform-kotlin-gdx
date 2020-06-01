package architecture.game

import architecture.engine.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.my.architecture.engine.structs.GameObject

class SpecialAttackBar : GameObject(arrayOf(), 0.0f, 0.0f) {
    var shape: ShapeRenderer? = null
    var player: Player? = null
    var joy: Joy? = null

    override fun start() {
        player = World.world.findGameObjects<Player>()[0]
        assert(player != null)
        if (player == null) {
            World.world.destroy(this)
        }
        val joys = World.world.findGameObjects<Joy>().filter { joy -> joy.tag == "JOYSTICK ATTACK" }
        if (joys.isNotEmpty()) joy = joys[0]
        shape = ShapeRenderer()

    }

    override fun update(dt: Float) {
        val deg = ((player!!.accumulatorSpecialAttack * 360f) + 1) / 100f
        shape!!.color = Color.GOLD
        shape!!.begin(ShapeRenderer.ShapeType.Filled)
        shape!!.arc(100.0f, Gdx.graphics.height - 60f, 60f, 90f, deg)
        shape!!.color = Color.GOLDENROD
        shape!!.circle(100.0f, Gdx.graphics.height - 60f, 40f)
        shape!!.end()

        // This doesnt affect logic, only that the virtual joystick should show if this is set to true.
        // The logic of the shooting is handled on the shooting function.
        if (deg < 360) {
            joy?.activate(false)
            return
        }
        joy?.activate(true)
    }

    override fun onDestroy() {

    }

    override fun onDispose() {
        Gdx.app.log("DISPOSED","DISPOSEDXD")
        if (shape != null) {
            shape!!.dispose()
            shape = null
        }
    }

}