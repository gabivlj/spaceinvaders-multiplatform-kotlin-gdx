package architecture.game

import architecture.engine.World
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class BulletEnemy(pos: Vector2,
                  val dir: Vector2,
                  onHit: () -> (Unit),
                  sprites: Array<Sprite>,
                  speed: Float,
                  damage: Float,
                  w: Float = 50f,
                  h: Float = 75f,
                  val follow: Boolean) : Bullet(pos, null, dir, onHit, sprites, speed, damage, w, h) {
    override fun start() {
        super.start()
        rotation = (MathUtils.atan2(dir.y, dir.x) * 180 / Math.PI.toFloat()) - 180
        if (!follow) return
        player = World.world.findGameObjects<Player>().last()
    }

    lateinit var player: Player

    override fun update(dt: Float) {
        if (LevelManager.outOfBounds(position, height, width)) {
            World.world.destroy(this)
            return
        }
        if (!follow) {
            super.update(dt)
            return
        }
        val dir = player.position.cpy().sub(position).nor()
        position.x += dir.x * dt * 400f
        position.y += dir.y * dt * 400f
    }

    override fun onCollide(other: GameObject) {
        if (other.get<NormalBullet>() != null && follow) {
            World.world.destroy(this)
            World.world.destroy(other)
        }
    }

}