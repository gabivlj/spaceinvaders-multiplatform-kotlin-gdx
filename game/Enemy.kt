package architecture.game

import architecture.engine.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class Point(val dir: Vector2, val speed: Float, val duration: Float)

open class BasicEnemy(
    vecPos: Vector2 = Vector2(),
    sprites: Array<Sprite> = SpaceInvaders.sprites.slice(19..19).toTypedArray(),
    w: Float,
    h: Float,
    var hp: Float
) :
    GameObject(
        sprites,
        w,
        h,
        vecPos
    ) {
    lateinit var player: Player

    companion object {
        var explosionSound: AudioID? = null
    }
    var effect: ParticleEffect? = null
    override fun start() {
        if (Game.renderer.particleAtlas.regions.size < 3) {
            Game.renderer.particleAtlas.addRegion("cloud_1", Texture("smoke.png"), 0, 0, 400, 300)
        }

        tint = Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), .6f)
        effect = ParticleEffect()
        effect?.load(Gdx.files.internal("smoketrail.p"), Game.renderer.particleAtlas)
        effect?.start()
        Game.renderer.effects.add(effect!!)
        effect?.flipY()
        activateCollisions()
        explosionSound = Audio.add("explosion.mp3", AudioType.TRACK)
        hp *= Config.difficulty.multiplierHp
        player = World.world.findGameObjects<Player>().last()
    }

    override fun onDestroy() {
        val centerPos: Vector2 = Vector2(position.x, position.y)
        World.world.instantiate(Explosion(centerPos, width.coerceAtLeast(height) / 1.3f))

        if (Math.random() > 0.4f)
            World.world.instantiate(Ammo(centerPos))
    }

    override fun onDispose() {
        super.onDispose()
        effect?.dispose()
        Game.renderer.effects.remove(effect)
    }
}

class Enemy(
    vecPos: Vector2,
    hp: Float = 70.0f,
    val points: Array<Point> = arrayOf(),
    sprites: Array<Sprite> = SpaceInvaders.sprites.slice(19..19).toTypedArray()
) :
    BasicEnemy(vecPos, sprites, 100f, 200f, 50.0f) {

    var currentPoint = 0
    var duration = 0.0f
    val timeBetweenShooting = 2.0f
    var accumulatorTimeBetweenShooting = 0.0f

    override fun update(dt: Float) {
        effect?.setPosition(position.x + width / 2, position.y + height / 1.1f)
        if (hp <= 0) {
            destroy()
        }

        processWayPoint(dt)
        accumulatorTimeBetweenShooting += dt
        if (timeBetweenShooting <= accumulatorTimeBetweenShooting) {
            World.world.instantiate(
                BulletEnemy(
                    position,
                    player.position.cpy().sub(position).nor(),
                    {},
                    sprites,
                    1000.0f,
                    20.0f,
                    50.0f,
                    20.0f,
                    false
                )
            )
            accumulatorTimeBetweenShooting = 0.0f
        }

        if (position.y + height <= LevelManager.level.bottomBounds) {
            World.world.destroy(this)
        }
    }

    private fun processWayPoint(dt: Float) {
        if (points.isEmpty()) return

        val point = points[currentPoint]
        position.x += point.speed * dt * point.dir.x
        position.y += point.speed * dt * point.dir.y

        duration += dt

        if (duration >= point.duration) {
            currentPoint++
        }

        currentPoint = MathUtils.clamp(currentPoint, 0, points.size - 1)
    }

    fun destroy() {
        World.world.destroy(this)
        if (explosionSound != null) {
            Audio.play(explosionSound!!)
        }
        effect?.dispose()
        Game.renderer.effects.remove(effect)
    }
}
