package architecture.game

import architecture.engine.*
import architecture.engine.structs.GameObjectInput
import architecture.engine.structs.IJoystick
import architecture.engine.structs.PhysicalJoystick
import architecture.engine.structs.ToListen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject
import kotlin.math.abs

class Player(sprites: Array<Sprite>, private val movementJoystick: IJoystick, private val attackJoystick: IJoystick, private val specialAttackJoy: IJoystick) : GameObjectInput(sprites, 60f, 60f, Vector2(50f, 50f)) {

    enum class CurrentShot(
        val nBullets: Int,
        val damage: Float,
        val spritesBullet: IntRange,
        val margin: Float,
        val speed: Float = 1000f,
        val size: Vector2 = Vector2(50f, 100f),
        val ammo: Float = 1000f
    ) {
        NORMAL(1, 10f, 3..8, 0f),
        ENERGY(3, 8f, 30..30, 53f, 1000f, Vector2(20f, 100f)),
        MISSILE(1, 28f, 31..31, 0f, 1500f, Vector2(30f, 110f), 500f)
    }
    var currentShot: CurrentShot = CurrentShot.NORMAL
    var currentAmmo: Float = 1000f
    var score: Float = 0.0f
    var hp = 100.0f
    var accumulatorSpecialAttack = 0.0f

    private var cooldownAttack: Float = 1.0f
    private var canAttack: Float = 0.3f
    // We start -4 because we want immunity at the start of the game just in case
    private var cooldownAccReceivingDamage: Float = -4.0f
    private val cooldownReceivingDamage: Float = 1.0f
    private lateinit var audioShoot: AudioID

    override fun keyDown(keycode: Int): Boolean {
        return true
    }
    var effect: ParticleEffect? = null
    var trail: ParticleEffect? = null
    override fun start() {
        Game.renderer.particleAtlas = TextureAtlas()
        Game.renderer.particleAtlas.addRegion("star-real", Texture("star-real.png"), 0, 0, 400, 300)
        Game.renderer.particleAtlas.addRegion("rect", Texture("rect.png"), 0, 0, 400, 400)
        effect = ParticleEffect()
        trail = ParticleEffect()
        effect?.load(Gdx.files.internal("particle.p"), Game.renderer.particleAtlas)
        trail?.load(Gdx.files.internal("trail.p"), Game.renderer.particleAtlas)
        effect?.start()
        trail?.start()
        Game.renderer.effects.add(effect!!)
        Game.renderer.effects.add(trail!!)
        super.start()
        if (tint == Color.WHITE)
            tint = Config.colorOfShip
        specialAttackJoy.subscribe(this)
        effect?.scaleEffect(1.5f, 1.5f, 2f)
        audioShoot = Audio.add("shoot.mp3", AudioType.TRACK)
        activateCollisions()
    }

    override fun update(dt: Float) {
        effect?.setPosition(position.x, position.y)
        effect?.setDuration(1000)
        trail?.setPosition(position.x, position.y)
        if (effect?.isComplete!!) {
            effect?.reset()
        }

        if (hp <= 0f) {
            SpaceInvaders.worlds[1].start()
            return
        }
        cooldownAccReceivingDamage += dt
        overlaps()
        val direction = movementJoystick.dir()
        val distance = movementJoystick.dist()
        val canMove = position.x >= LevelManager.level.leftBounds &&
            position.x + width <= LevelManager.level.rightBounds &&
            position.y + height <= LevelManager.level.topBounds &&
            position.y > LevelManager.level.bottomBounds
        if (canMove) {
            position.x += direction.x * 200 * dt * distance
            position.y += direction.y * 200 * dt * distance
        }
        if (LevelManager.level.initialized) {
            position.x = MathUtils.clamp(position.x, LevelManager.level.leftBounds, LevelManager.level.rightBounds - width)
            position.y = MathUtils.clamp(position.y, LevelManager.level.bottomBounds + 1, LevelManager.level.topBounds - height)
        }
        flipX = direction.x > 0.0f
        spriteIndex = when {
            abs(direction.x) > 0.8f -> 2
            abs(direction.x) > 0.5f -> 1
            else -> 0
        }
        cooldownAttack += dt
        val dist = attackJoystick.dist()
        if (dist < 0.1f) {
            return
        }
        shoot()
    }

    /**
     *
     */
    private fun shoot() {
        val dir = attackJoystick.dir().cpy()
        rotation = (MathUtils.atan2(dir.y, dir.x) * 180 / Math.PI.toFloat()) - 90
        if (cooldownAttack < canAttack) {
            return
        }
        for (i in 1..currentShot.nBullets) {
            val bullet = NormalBullet(
                Vector2(),
                dir,
                this,
                { accumulatorSpecialAttack += 10f },
                SpaceInvaders.sprites.slice(currentShot.spritesBullet).toTypedArray(),
                currentShot.damage,
                currentShot.speed
            )
            bullet.width = currentShot.size.x
            bullet.height = currentShot.size.y
            val bull = World.world.instantiate(bullet)
            // -90 deg because the sprite is already looking up
            bull.rotation = (MathUtils.atan2(dir.y, dir.x) * 180 / Math.PI.toFloat()) - 90
            val pos = position.cpy()
            pos.x += i * ((width / 2) - (bull.width / 2) + currentShot.margin) / currentShot.nBullets.toFloat()
            pos.x -= currentShot.margin / 2

            if (abs(dir.x) > 0 && abs(dir.y) < 0.2f) {
                pos.y += i * ((height / 2) - (bull.width / 2) + currentShot.margin) / currentShot.nBullets.toFloat()
                pos.y -= currentShot.margin / 2
            }
            bull.position = pos
        }
        if (currentShot != CurrentShot.NORMAL) {
            currentAmmo -= 50f
        }
        cooldownAttack = 0.0f
        if (currentAmmo <= 0) {
            currentShot = CurrentShot.NORMAL
        }
        Audio.play(audioShoot)
    }

    override fun buttonDownParsed(controller: PhysicalJoystick, whatToListen: ToListen) {
        if (accumulatorSpecialAttack < 100) {
            return
        }
        if (whatToListen != ToListen.LEFT_FACE) {
            return
        }
        accumulatorSpecialAttack = 0.0f
        World.world.instantiate(
            SpecialBullet(
                position.cpy(),
                this,
                movementJoystick.dir()
            ) { accumulatorSpecialAttack += 10f }
        )
    }

    /**
     * Called by the Joystick/Button of the special attack
     * because of: specialAttackJoy.subscribe(this)
     */
    override fun touchUpJoystick(direction: Vector2, dist: Float, joystickTag: String) {
        if (dist <= 0 || direction.len() <= .5f || accumulatorSpecialAttack < 100) {
            return
        }
        accumulatorSpecialAttack = 0.0f
        World.world.instantiate(
            SpecialBullet(
                position.cpy(),
                this,
                direction
            ) { accumulatorSpecialAttack += 10f }
        )
    }

    override fun onDispose() {
        specialAttackJoy.unsubscribe(this)
        Game.renderer.effects.remove(effect)
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        Gdx.app.log("tap", "tapped")
        return super.tap(x, y, count, button)
    }

    override fun onCollide(other: GameObject) {
        when (other) {
            is Ammo -> {
                currentShot = when (other.type) {
                    Ammo.Type.HEALTH -> {
                        hp += 20f
                        hp = hp.coerceAtMost(100f)
                        currentShot
                    }
                    Ammo.Type.MISSILE -> Player.CurrentShot.MISSILE
                    Ammo.Type.ENERGY -> Player.CurrentShot.ENERGY
                }
                currentAmmo = currentShot.ammo
                World.world.destroy(other)
            }
            is BulletEnemy -> {
                hp -= 10 * Config.difficulty.multiplierDamage
                World.world.destroy(other)
            }
            is BasicEnemy -> {
                if (cooldownAccReceivingDamage <= cooldownReceivingDamage) return
                cooldownAccReceivingDamage = 0.0f
                hp -= 20f * Config.difficulty.multiplierDamage
            }
        }
    }
}
