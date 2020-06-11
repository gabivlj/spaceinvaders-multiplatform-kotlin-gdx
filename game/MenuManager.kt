package architecture.game

import architecture.engine.*
import architecture.engine.structs.PhysicalJoystick
import architecture.engine.structs.ToListen
import architecture.engine.structs.UIButton
import architecture.engine.structs.UIStack
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

/**
 * Spawns the standard UIStack for this game
 */
fun spawnStack() {
    val buttonHandle = World.world.instantiate(
        PhysicalJoystick(
            ToListen.LEFT_FACE,
            arrayOf(
                Input.Keys.E,
                -1,
                Input.Keys.E,
                -1
            ),
            2.5f
        )
    )
    val moveHandle = World.world.instantiate(
        PhysicalJoystick(
            ToListen.LEFT_STICK,
            arrayOf(
                Input.Keys.D,
                Input.Keys.A,
                Input.Keys.W,
                Input.Keys.S
            )
        )
    )
    val stack = UIStack(
        moveHandle,
        buttonHandle
    )
    World.world.instantiate(stack)
}

class MenuManager : GameObject(
    arrayOf(),
    0f,
    0f,
    Vector2(0f, 0f)
) {
    private val widthButtonStart = 300f
    private val heightButtonStart = 300f

    private lateinit var hoverSound: AudioID
    private lateinit var clickSound: AudioID
    private lateinit var menuMusic: AudioID

    override fun start() {
        hoverSound = Audio.add("button_hover.mp3", AudioType.TRACK)
        clickSound = Audio.add("button_click.mp3", AudioType.TRACK)
        menuMusic = Audio.add("menu_music.mp3", AudioType.MUSIC)
        Audio.play(menuMusic)
        showMenu()
    }

    private fun showMenu() {
        Game.camera.position.set(0.0f, 0.0f, 0.0f)
        Game.camera.update()
        val buttonStart: UIButton = UIButton(
            SpaceInvaders.spritesMenus.slice(6..6).toTypedArray(),
            widthButtonStart,
            heightButtonStart,
            Vector2((Gdx.graphics.width / 2f) - (widthButtonStart / 4), Gdx.graphics.height / 2f),
            "",
            Vector2()
        ) {
            Audio.play(clickSound)
            showMapsToChoose()
        }.also { uiButton ->
            uiButton.onHover = {
                it.width *= 1.1f
                it.height *= 1.1f
                Audio.play(hoverSound)
            }
        }
            .also { uiButton ->
                uiButton.stopHover = {
                    it.width = widthButtonStart
                    it.height = heightButtonStart
                }
            }

        instantiate(buttonStart)
        var pos = 100f
        val difficulties = Difficulty.values()
        for ((i, difficulty) in difficulties.withIndex()) {
            val easyButton: UIButton = UIButton(
                SpaceInvaders.spritesMenus.slice(difficulty.spriteRoute).toTypedArray(),
                widthButtonStart / difficulties.size,
                heightButtonStart / 4,
                Vector2(),
                "",
                Vector2()
            ) {
                Config.difficulty = difficulty
                Audio.play(clickSound)
            }.also { b ->
                b.useScreenCoords = false
                b.position = Vector2(
                    ((widthButtonStart + Gdx.graphics.width / difficulties.size) / difficulties.size * i) - 150,
                    -50f
                )
                b.onHover = {
                    it.textUI.width = 2f
                    it.textUI.height = 2f
                    it.text = difficulty.msg
                    Audio.play(hoverSound)
                }
                b.stopHover = {
                    it.text = ""
                }
            }
            World.world.instantiate(easyButton)
            pos += 300f
        }
        spawnStack()
    }

    private fun showMapsToChoose() {
        Game.camera.position.set(Gdx.app.graphics.width / 2f, 0.0f, 0.0f)
        Game.camera.update()
        World.world.findGameObjects<GameObject>().forEach {
            World.world.destroy(it)
        }
        spawnStack()
        val sprite = SpaceInvaders.spritesBackground
        for ((idx, _) in sprite.withIndex()) {
            World.world.instantiate(
                UIButton(
                    arrayOf(sprite[idx]),
                    Gdx.app.graphics.width / sprite.size.toFloat(),
                    Gdx.app.graphics.width / sprite.size.toFloat(),
                    Vector2(),
                    "${idx + 1}. ${Config.maps[idx].name}",
                    Vector2()
                ) {
                    Config.currentMapIdx = idx
                    SpaceInvaders.worlds[1].start()
                }.also {
                    it.useScreenCoords = false; it.position = Vector2(
                        (Gdx.graphics.width / sprite.size.toFloat()) * idx,
                        0f
                    )
                    it.onHover = {
                        it.text = "${idx + 1} ${Config.maps[idx].name} (SELECTED)"
                    }
                    it.stopHover = {
                        it.text = "${idx + 1} ${Config.maps[idx].name}"
                    }
                }
            )
        }

        val buttonStart: UIButton = UIButton(
            SpaceInvaders.spritesMenus.slice(5..5).toTypedArray(),
            widthButtonStart / 2f,
            heightButtonStart / 2f,
            Vector2((Gdx.graphics.width / 2f) - (widthButtonStart / 4), Gdx.graphics.height / 1.2f),
            "Choose the color of your ship! (In Hexadecimal)",
            Vector2()
        ) {
            Audio.play(clickSound)
            showColorPrompt()
        }.also { uiButton ->
            uiButton.onHover = {
                it.position.y += 10f
                Audio.play(hoverSound)
            }
        }
            .also { uiButton ->
                uiButton.stopHover = {
                    it.position.y -= 10f
                }
                uiButton.useScreenCoords = false
                uiButton.position = Vector2(Gdx.graphics.width / 2f - widthButtonStart, -Gdx.graphics.height / 2f + 40f)
            }

        World.world.instantiate(buttonStart)

        val buttonSound: UIButton = UIButton(
            SpaceInvaders.spritesMenus.slice(8..8).toTypedArray(),
            widthButtonStart / 2f,
            heightButtonStart / 2f,
            Vector2((Gdx.graphics.width / 2f) - (widthButtonStart / 4), Gdx.graphics.height / 1.2f),
            "",
            Vector2()
        ) {
            Audio.play(clickSound)
            Audio.muted = !Audio.muted
        }.also { uiButton ->
            uiButton.onHover = {
                Audio.play(hoverSound)
                if (Audio.muted) {
                    it.text = "Unmute the audio!"
                } else {
                    it.text = "Mute the audio!"
                }
            }
            uiButton.stopHover = {
                it.text = ""
            }
        }
            .also { uiButton ->
                uiButton.useScreenCoords = false
                uiButton.position = Vector2(Gdx.graphics.width / 2f, -Gdx.graphics.height / 2f + 40f)
                uiButton.textUI.position = uiButton.position.cpy().add(0.0f, 10.0f)
            }
        instantiate(buttonSound)
        val buttonPlayers: UIButton = UIButton(
            SpaceInvaders.spritesMenus.slice(7..7).toTypedArray(),
            widthButtonStart / 2f,
            heightButtonStart / 2f,
            Vector2((Gdx.graphics.width / 2f) - (widthButtonStart / 4), Gdx.graphics.height / 1.2f),
            "",
            Vector2()
        ) {
            Audio.play(clickSound)
            Config.nPlayers = if (Config.nPlayers == 1) 2 else { 1 }
        }.also { uiButton ->
            uiButton.onHover = {
                Audio.play(hoverSound)
                if (Config.nPlayers == 2) {
                    it.text = "Play as 1!"
                } else {
                    it.text = "Play as 2"
                }
            }
            uiButton.stopHover = {
                it.text = ""
            }
        }
            .also { uiButton ->
                uiButton.useScreenCoords = false
                uiButton.position = Vector2(Gdx.graphics.width / 2f + widthButtonStart + 10 / 2f, -Gdx.graphics.height / 2f + 40f)
            }
        World.world.instantiate(buttonPlayers)
    }

    private fun showColorPrompt() {
        Gdx.input.getTextInput(
            object : Input.TextInputListener {
                override fun input(text: String?) {
                    if (text == null) return
                    Config.colorOfShip = valueOf(text)
                }

                override fun canceled() {
                }
            },
            "Insert a custom color for your ship", "In Hex! (Cancel for default)", ""
        )
    }
}

fun valueOf(hexInput: String): Color {
    var hex = hexInput.trim()
    hex = if (hex[0] == '#') hex.substring(1) else hex
    val r = Integer.valueOf(hex.substring(0, 2), 16)
    val g = Integer.valueOf(hex.substring(2, 4), 16)
    val b = Integer.valueOf(hex.substring(4, 6), 16)
    val a = if (hex.length != 8) 255 else Integer.valueOf(hex.substring(6, 8), 16)
    return Color(r / 255f, g / 255f, b / 255f, a / 255f)
}
