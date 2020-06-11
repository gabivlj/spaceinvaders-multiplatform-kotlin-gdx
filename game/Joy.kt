package architecture.game

import architecture.engine.structs.VirtualJoystick
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

class Joy(
    s: Sprite,
    s2: Sprite,
    w1: Float,
    w2: Float,
    val max: Float = 300f,
    val m: Pair<Int, Int> = Pair(0, 500),
    val pos: Vector2 = Vector2(100f, 500f),
    dir: () -> (Vector2),
    dist: (joy: VirtualJoystick) -> (Float),
    tag: String = "DEFAULT JOYSTICK"
) : VirtualJoystick(s, s2, tag, pos, w1, w2, 30f, m, 2.5f, dir, dist) {

    override fun update(dt: Float) {
        super.update(dt)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // Omit touches in the center because that's where the sliding occurs
        if (tag == "JOYSTICK ATTACK" && screenY <= Gdx.graphics.height / 2) {
            return true
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return super.touchDragged(screenX, screenY, pointer)
    }
}
