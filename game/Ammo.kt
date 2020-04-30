package architecture.game

import architecture.engine.World
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject



class Ammo(pos: Vector2) : GameObject(SpaceInvaders.sprites.slice(27..29).toTypedArray(), 100f, 50f, pos) {
    enum class Type(val spriteIndex: Int) {
        HEALTH(2),
        ENERGY(0),
        MISSILE(1)
    }
    val random = Math.random().toFloat() * 3f
    val type: Type = when (random) {
            in 0f..1f -> Type.HEALTH
            in 1f..2f -> Type.ENERGY
            in 2f..3f -> Type.MISSILE
            else -> Type.HEALTH
        }

    override fun start() {
        super.start()
        spriteIndex = type.spriteIndex
    }

    override fun update(dt: Float) {
        if (LevelManager.outOfBounds(position, width, height)) World.world.destroy(this)
    }
}