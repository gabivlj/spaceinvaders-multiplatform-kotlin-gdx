package architecture.game

import architecture.engine.Animation
import architecture.engine.Animator
import architecture.engine.World
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class Explosion(pos: Vector2, size: Float = 100f, spritesExplosion: Array<Sprite> = SpaceInvaders.sprites.slice(9..17).toTypedArray()) : GameObject(
    spritesExplosion,
    size,
    size,
    pos
) {
    lateinit var animation: Animation

    override fun start() {
        animation = Animator.animate(this, 0, 8, 3, false)
        animation.start()
    }

    override fun update(dt: Float) {
        if (animation.finished) {
            World.world.destroy(this)
            animation.end()
            animation.dispose()
        }
    }
}
