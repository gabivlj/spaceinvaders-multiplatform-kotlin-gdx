package architecture.game

import architecture.engine.World
import com.badlogic.gdx.math.Vector2

class EnemyFollow(pos: Vector2) : BasicEnemy(pos, SpaceInvaders.sprites.slice(26..26).toTypedArray(), 200f, 100f, 50f) {
    var timeBullet: Float = 3.0f
    var timeBulletAcc: Float = 0.0f

    override fun update(dt: Float) {
        super.update(dt)

        if (hp <= 0) {
            World.world.destroy(this)
            return
        }
        position.y -= dt * 100f
        timeBulletAcc += dt
        if (timeBulletAcc >= timeBullet) {
            timeBulletAcc = 0.0f
            World.world.instantiate(BulletEnemy(position.cpy(), Vector2(), {}, SpaceInvaders.sprites.slice(25..25).toTypedArray(), 100.0f, 40f, 40f, 40f, true))
        }

        if (position.y + height <= LevelManager.level.bottomBounds) World.world.destroy(this)
    }
}