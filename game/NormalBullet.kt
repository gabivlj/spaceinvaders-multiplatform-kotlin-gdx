package architecture.game

import architecture.engine.Animation
import architecture.engine.Animator
import architecture.engine.World
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

class NormalBullet(pos: Vector2,
                   dir: Vector2,
                   parent: Player,
                   onHit: () -> (Unit),
                   spritesToUse: Array<Sprite> = SpaceInvaders.sprites.slice(3..8).toTypedArray(),
                   damage: Float = 10f,
                   speed: Float = 1000f
    )
    : Bullet(
        pos,
        parent,
        dir,
        onHit,
        spritesToUse,
        speed,
        damage
     ) {
    lateinit var animation: Animation

    override fun start() {
        super.start()
        animation = Animator.animate(this, 0, sizeSprites - 1, 6, true)
        animation.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Animator.destroy(animation)

        World.world.instantiate(Explosion(position.cpy(), width))

    }
}