package architecture.game

import architecture.engine.Audio
import architecture.engine.AudioID
import architecture.engine.AudioType
import architecture.engine.World
import architecture.engine.structs.UIButton
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

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
        val buttonStart: UIButton = UIButton(
                SpaceInvaders.spritesMenus.slice(6..6).toTypedArray(),
                widthButtonStart,
                heightButtonStart,
                Vector2((Gdx.graphics.width / 2f) - (widthButtonStart / 4), Gdx.graphics.height / 2f),
                "",
                Vector2()) {
            Audio.play(clickSound)
//            SpaceInvaders.worlds[1].start()
            showMapsToChoose()
        }.also { uiButton -> uiButton.onHover = {
            it.width *= 1.1f;
            it.height *= 1.1f
            Audio.play(hoverSound)
        } }
                .also { uiButton -> uiButton.stopHover = {
                    it.width = widthButtonStart;
                    it.height = heightButtonStart;
                } }

        World.world.instantiate(buttonStart)
        var pos = 100f
        for (difficulty in Difficulty.values()) {
            val easyButton: UIButton = UIButton(
                    SpaceInvaders.spritesMenus.slice(difficulty.spriteRoute).toTypedArray(),
                    widthButtonStart / 2,
                    heightButtonStart / 4,
                    Vector2(pos, Gdx.graphics.height / 1.2f),
                    "",
                    Vector2()) {
                Config.difficulty = difficulty
                Audio.play(clickSound)
            }.also { b ->
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
    }

   private fun showMapsToChoose() {
        World.world.findGameObjects<GameObject>().forEach {
            World.world.destroy(it)
        }
       val sprite = SpaceInvaders.spritesBackground
        for ((idx, map) in sprite.withIndex()) {
            World.world.instantiate(UIButton(
                    arrayOf(sprite[idx]),
                    400f,
                    400f,
                    Vector2(idx * 401 + 10f, Gdx.graphics.height / 2f),
                    "${idx + 1}. ",
                    Vector2()
            ) {
                Config.currentMapIdx = idx
                SpaceInvaders.worlds[1].start()
            })
        }


       val buttonStart: UIButton = UIButton(
               SpaceInvaders.spritesMenus.slice(5..5).toTypedArray(),
               widthButtonStart / 2f,
               heightButtonStart / 2f,
               Vector2((Gdx.graphics.width / 2f) - (widthButtonStart / 4), Gdx.graphics.height / 1.2f),
               "Choose the color of your ship! (In Hexadecimal)",
               Vector2()) {
           Audio.play(clickSound)
           showColorPrompt()
       }.also { uiButton ->
            uiButton.onHover = {
               it.position.y += 10f
               Audio.play(hoverSound)
            }
       }
       .also { uiButton -> uiButton.stopHover = {
           it.position.y -= 10f
       } }

       World.world.instantiate(buttonStart)
    }


    private fun showColorPrompt() {
        Gdx.input.getTextInput(object : Input.TextInputListener {
            override fun input(text: String?) {
                if (text == null) return
                Config.colorOfShip = valueOf(text)
            }

            override fun canceled() {

            }
        }, "Insert a custom color for your ship", "In Hex! (Cancel for default)", "")

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