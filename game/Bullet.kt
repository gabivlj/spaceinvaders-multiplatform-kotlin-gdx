package architecture.game

import architecture.engine.Audio
import architecture.engine.World
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

open class Bullet(
    pos: Vector2,
    private var parent: Player? = null,
    private val dir: Vector2,
    private val onHit: () -> (Unit),
    sprites: Array<Sprite>,
    private val speed: Float,
    private val damage: Float,
    w: Float = 50f,
    h: Float = 75f
) :
    GameObject(sprites, w, h, pos.cpy()) {

    override fun start() {
        activateCollisions()
    }

    override fun update(dt: Float) {
        if (LevelManager.outOfBounds(position, height, width)) {
            World.world.destroy(this)
            return
        }

        position.y += dt * speed * dir.y
        position.x += dt * speed * dir.x
        World.world.overlaps(this)
    }

    /**
     * Standard behaviour
     */
    override fun onCollide(other: GameObject) {
        when (other) {
            is BasicEnemy -> {
                other.hp -= damage
                if (other.hp <= 0) {
                    if (BasicEnemy.explosionSound != null) {
                        Audio.apply<Sound>(BasicEnemy.explosionSound!!) {
                            val id = Audio.play(BasicEnemy.explosionSound!!)
                            it.setVolume(id, .24f)
                        }
                    }
                    if (parent != null) {
                        parent!!.score += Config.sumScore
                    }
                }
                onHit()
                World.world.destroy(this)
            }
        }
    }
}
