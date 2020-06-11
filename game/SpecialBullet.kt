package architecture.game

import com.badlogic.gdx.math.Vector2

class SpecialBullet(pos: Vector2, parent: Player, dir: Vector2, onHit: () -> (Unit)) :
    Bullet(
        pos,
        parent,
        dir,
        onHit,
        SpaceInvaders.sprites.slice(23..23).toTypedArray(),
        1500f,
        50f,
        130f,
        130f
    ) {
    override fun update(dt: Float) {
        super.update(dt)
        rotation += dt * 10f
    }
}
