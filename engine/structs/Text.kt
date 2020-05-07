package architecture.engine.structs

import architecture.engine.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2

class Text {
    var position: Vector2 = Vector2()
    var text: String = ""
    val font: BitmapFont
        get() { return bitmapFont }

    private val bitmapFont: BitmapFont = BitmapFont()

    init {
        Game.renderer.textUI.add(this)
    }

    /**
     * Stops rendering this text
     */
    fun stop() {
        bitmapFont.dispose()
        Game.renderer.textUI.remove(this)
    }

    /**
     * If you've used stop() on this text instance, you can render it again using render()
     */
    fun render() {
        Game.renderer.textUI.add(this)
    }

}