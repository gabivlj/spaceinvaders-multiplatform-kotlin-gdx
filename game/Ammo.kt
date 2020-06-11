package architecture.game

import architecture.engine.World
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class Ammo(pos: Vector2) : GameObject(SpaceInvaders.sprites.slice(27..29).toTypedArray(), 100f, 50f, pos) {
    enum class Type(val spriteIndex: Int, val rangeForSelection: ClosedFloatingPointRange<Float>) {
        HEALTH(2, 0f..1f),
        ENERGY(0, 1f..2f),
        MISSILE(1, 2f..3f)
    }
    val random = Math.random().toFloat() * 3f
    val type: Type = when (random) {
        in Type.HEALTH.rangeForSelection -> Type.HEALTH
        in Type.ENERGY.rangeForSelection -> Type.ENERGY
        in Type.MISSILE.rangeForSelection -> Type.MISSILE
        else -> Type.HEALTH
    }

    override fun start() {
        activateCollisions()
        super.start()
        spriteIndex = type.spriteIndex
    }

    override fun update(dt: Float) {
        if (LevelManager.outOfBounds(position, width, height)) World.world.destroy(this)
    }
}
