package architecture.game

import architecture.engine.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Array
import com.my.architecture.engine.structs.GameObject

class SpecialAttackBar(var secondPlayer: Player?) : GameObject(arrayOf(), 0.0f, 0.0f) {
    var shape: ShapeRenderer? = null
    var player: Player? = null
    var players: Array<Player>? = null
    var joy: Joy? = null

    override fun start() {
        players = World.world.findGameObjects()
        player = secondPlayer ?: players!![0]
        if (players!!.size > 1 && secondPlayer == null) {
            World.world.instantiate(SpecialAttackBar(players!![1]))
        }
        val joys = World.world.findGameObjects<Joy>().filter { joy -> joy.tag == "JOYSTICK ATTACK" }
        if (joys.isNotEmpty()) joy = joys[0]
        shape = ShapeRenderer()

    }

    override fun update(dt: Float) {
        renderPlayer(player!!, if (player == secondPlayer) { 300f } else { 0f })
    }

    private fun renderPlayer(player: Player, offset: Float) {
        val deg = ((player.accumulatorSpecialAttack * 360f) + 1) / 100f
        shape!!.color = Color.GOLD
        shape!!.begin(ShapeRenderer.ShapeType.Filled)
        shape!!.arc(200.0f + offset, Gdx.graphics.height - 60f, 60f, 90f, deg)
        shape!!.color = Color.GOLDENROD
        shape!!.circle(200.0f + offset, Gdx.graphics.height - 60f, 40f)
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
        if (shape != null) {
            shape!!.dispose()
            shape = null
        }
    }

}