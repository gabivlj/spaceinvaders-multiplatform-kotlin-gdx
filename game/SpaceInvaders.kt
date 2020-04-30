package architecture.game

import architecture.engine.*
import architecture.engine.structs.IJoystick
import architecture.engine.structs.PhysicalJoystick
import architecture.engine.structs.ToListen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import java.lang.Exception
import kotlin.math.abs


class SpaceInvaders : Game() {

    // my worlds


    fun assert(good: Boolean) {
        if (!good) throw Exception("Error asserting, expected ${true}; result = $good")
    }

    companion object {
        val worlds: Array<World> = arrayOf(World(), World())
        lateinit var sprites: MutableList<Sprite>
        lateinit var spritesBackground: MutableList<Sprite>

        lateinit var spaceInvaders: SpaceInvaders
    }

    private fun startForGameplayWorld() {
        val rendererOpt = RendererOptimizer()
        val rendererBackground = RendererOptimizer()
        sprites = rendererOpt.consumeSprites("ASSETS_08")
        val baseURL = "assets/sprites/256px"
        if (sprites.isEmpty()) {
            // PLAYER SPRITE (0)
            assert(rendererOpt.sprite("$baseURL/PlayerRed_Frame_01_png_processed.png"))
            // ... FUTURE SPRITES BUT ALLOCATE THESE FOR FILLING SPACE FOR THE MOMENT (1, 2)
            assert(rendererOpt.sprite("$baseURL/PlayerRed_Frame_02_png_processed.png"))
            assert(rendererOpt.sprite("$baseURL/PlayerRed_Frame_03_png_processed.png"))
            // Shooting (3, 8) inclusive
            for (i in 1..6) assert(rendererOpt.sprite("$baseURL/Exhaust_Frame_0${i}_png_processed.png"))
            // Explosion (9, 17)
            for (i in 1..9) assert(rendererOpt.sprite("$baseURL/Explosion01_Frame_0${i}_png_processed.png"))

            // ENEMY (18)
            assert(rendererOpt.sprite("$baseURL/Enemy01_Red_Frame_1_png_processed.png"))

            // ENEMY (19)
            assert(rendererOpt.sprite("$baseURL/Enemy01_Teal_Frame_1_png_processed.png"))

            // ENEMY (20)
            assert(rendererOpt.sprite("$baseURL/Enemy02_Teal_Frame_1_png_processed.png"))

            // Joysticks (21, 22)
            assert(rendererOpt.sprite("joyyy.png"))
            assert(rendererOpt.sprite("circle.png"))

            // Shooting Special (23)
            assert(rendererOpt.sprite("$baseURL/Explosion02_Frame_06_png_processed.png"))

            // Shooting Enemy 24
            assert(rendererOpt.sprite("$baseURL/Laser_Large_png_processed.png"))
            // Shooting 25
            assert(rendererOpt.sprite("$baseURL/Explosion01_Frame_07_png_processed.png"))
            // Enemy 26
            assert(rendererOpt.sprite("$baseURL/Enemy02_Teal_Frame_1_png_processed.png"))
            // PowerUp 27 (Violet)
            assert(rendererOpt.sprite("$baseURL/Powerup_Energy_png_processed.png"))
            // PowerUp rockets 28 (Green)
            assert(rendererOpt.sprite("$baseURL/Powerup_Rockets_png_processed.png"))
            // PowerUp health 29
            assert(rendererOpt.sprite("$baseURL/Powerup_Health_png_processed.png"))

            // Shooting Player Violet 30
            assert(rendererOpt.sprite("$baseURL/Plasma_Medium_png_processed.png"))
            // Shooting Green 31
            assert(rendererOpt.sprite("$baseURL/Laser_Medium_png_processed.png"))

            // Planets 32..46
            for (i in 1..14) {
                assert(rendererOpt.sprite("assets/planets/$i.png"))
            }

            // Save them
            sprites = rendererOpt.consumeSprites()

            rendererOpt.saveConsumedSprites("ASSETS_08")
        }

        spritesBackground = rendererBackground.consumeSprites("ASSET_BACKGROUND3")
        if (spritesBackground.isEmpty()) {
            rendererBackground.sprite("assets/background/NebulaAqua-Pink.png")
            spritesBackground = rendererBackground.consumeSprites()
            rendererBackground.saveConsumedSprites("ASSET_BACKGROUND3")
        }

        instantiation()
        spaceInvaders = this
    }

    override fun start() {
        Gdx.graphics.setResizable(false)
        Gdx.graphics.setWindowedMode(1020, 530)
        World.setCurrentWorld(worlds[1])
        // Set the onStart of this world
        worlds[1].onStart =  { startForGameplayWorld() }
        worlds[1].onFinish = {
            for (sprite in sprites) {
                sprite.texture.dispose()
            }
            sprites = mutableListOf()
            for (sprite in spritesBackground) {
                sprite.texture.dispose()
            }
            spritesBackground = mutableListOf()
        }

        worlds[0].onStart =  { startForGameplayWorld() }
        worlds[0].onFinish = {
            for (sprite in sprites) {
                sprite.texture.dispose()
            }
            sprites = mutableListOf()
            for (sprite in spritesBackground) {
                sprite.texture.dispose()
            }
            spritesBackground = mutableListOf()
        }


    }

    fun restart() {
        Game.restart()
        instantiation()
    }

    private fun instantiation() {
        instantiatePlayerAndJoysticks()
        World.world.instantiate(Enemy(Vector2(100f, 100f)))
        World.world.instantiate(SpecialAttackBar())

        Background(spritesBackground[0])
        UIManager()
        LevelManager()
    }

    fun instantiatePlayerAndJoysticks() {
        val sprite0 = sprites[21]
        val sprite1 = sprites[22]
        val diff = (Gdx.graphics.width / 3) / 2

        /**
         * There are 3 virutal joysticks
         *
         * The function that we are passing into the constructors are fallbacks for when there is no mobile device, so we implement our own dist and dir functions
         * for each joystick, for keyboard
         *
         * TODO: Delete keyboard code because we already handle that inside JoystickPhysical
         */
        val joy = Joy(
                sprite0,
                sprite1,
                200f,
                50f,
                0.0f,
                Pair(0, Gdx.graphics.width / 3),
                Vector2(
                        diff.toFloat(),
                        Gdx.graphics.height - Gdx.graphics.height / 10f
                ),
                // dir() function
                {
                    val x = if (Gdx.input.isKeyPressed(Input.Keys.D)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.A)) -1 else 0
                    val y = if (Gdx.input.isKeyPressed(Input.Keys.W)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.S)) -1 else 0
                    Vector2(x.toFloat(), y.toFloat()).nor()
                },
                // dist() function
                {
                    val x = if (Gdx.input.isKeyPressed(Input.Keys.D)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.A)) -1 else 0
                    val y = if (Gdx.input.isKeyPressed(Input.Keys.W)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.S)) -1 else 0
                    if (abs(x) > 0 || abs(y) > 0) it.maximumValueOnDistCall else 0.0f
                }
        )
        val joy2 = Joy(
                sprite0,
                sprite1,
                200f,
                50f,
                0.0f,
                Pair((Gdx.graphics.width * 2 + 1) / 3, Gdx.graphics.width),
                Vector2(
                        (Gdx.graphics.width * 2f / 3f) + diff,
                        Gdx.graphics.height - Gdx.graphics.height / 10f
                ),
                // dir()
                {
                    val x = if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) -1 else 0
                    val y = if (Gdx.input.isKeyPressed(Input.Keys.UP)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) -1 else 0
                    Vector2(x.toFloat(), y.toFloat()).nor()
                },
                // dist()
                {
                    val x = if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) -1 else 0
                    val y = if (Gdx.input.isKeyPressed(Input.Keys.UP)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) -1 else 0
                    if (abs(x) > 0 || abs(y) > 0) it.maximumValueOnDistCall else 0.0f
                }
        )

        val joy3AttackSpecial = Joy(
                sprite0,
                sprite1,
                100f,
                25f,
                0.0f,
                Pair((Gdx.graphics.width + 1) / 3, Gdx.graphics.width * 2 / 3),
                Vector2((Gdx.graphics.width / 3f) + diff,
                        Gdx.graphics.height - Gdx.graphics.height / 10f),
                // dir() *** we dont use this callback ***
                {
                    Vector2()
                },
                /**
                 * We use this for non mobile platforms
                 * When joy3AttackSpecial.dist() is called, this will be fired! (Only when not mobile)
                 *
                 * .dist() is called inside Player for getting inputs
                 */
                {
                    val x = if (Gdx.input.isKeyPressed(Input.Keys.D)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.A)) -1 else 0
                    val y = if (Gdx.input.isKeyPressed(Input.Keys.W)) 1 else if (Gdx.input.isKeyPressed(Input.Keys.S)) -1 else 0
                    // call subscribers manually
                    it.callSubscribers(Vector2(x.toFloat(), y.toFloat()).nor(), if (Gdx.input.isKeyPressed(Input.Keys.E)) it.maximumValueOnDistCall else 0.0f)
                    0.0f
                },
                "JOYSTICK ATTACK")
        lateinit var joystick01: IJoystick
        lateinit var joystick02: IJoystick
        lateinit var joystick03: IJoystick
        joystick01 = World.world.instantiate(joy)
        joystick02 = World.world.instantiate(joy2)
        joystick03 = World.world.instantiate(joy3AttackSpecial)
        if (!isMobile) {
            joystick01 = World.world.instantiate(PhysicalJoystick(ToListen.LEFT_STICK, arrayOf(Input.Keys.D, Input.Keys.A, Input.Keys.W, Input.Keys.S), 2.5f))
            joystick02 = World.world.instantiate(PhysicalJoystick(ToListen.RIGHT_STICK, arrayOf(Input.Keys.RIGHT, Input.Keys.LEFT, Input.Keys.UP, Input.Keys.DOWN), 2.5f))
            // fuck (TODO: MAP BUTTONS)
            joystick03 = World.world.instantiate(PhysicalJoystick(ToListen.LEFT_BUTTON, arrayOf(Input.Keys.E, -1, Input.Keys.E, -1), 2.5f))
        }

        World.world.instantiate(Player(sprites.slice(0..2).toTypedArray(), joystick01, joystick02, joystick03))
    }
}