package architecture.game

import architecture.engine.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject
import java.util.logging.Level

open class Bullet(pos: Vector2,
                  private val dir: Vector2,
                  private val onHit: () -> (Unit),
                  sprites: Array<Sprite>,
                  private val speed: Float,
                  private val damage: Float,
                  w: Float = 50f,
                  h: Float = 75f)
    : GameObject(sprites, w, h, pos.cpy()) {

    override fun start() {
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
                    other.player.score += 125f
                }
                onHit()
                World.world.destroy(this)
            }
        }

    }
}