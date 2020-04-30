package architecture.game

import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class Planet(val pos: Vector2, val speed: Float = Math.random().toFloat() * (50f - 10f) + 10f, size: Float = Math.random().toFloat() * (200f - 100f) + 100f)
    : GameObject(SpaceInvaders.sprites.slice(32..45).toTypedArray(), size, size, pos) {

    val spriteRandom: Int = (Math.random() * sizeSprites).toInt()

    override fun start() {
        spriteIndex = spriteRandom
        depth = 4
    }

    override fun update(dt: Float) {
        position.y -= dt * speed
//        position.y += dt * speed
    }
}