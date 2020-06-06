package architecture.engine.structs

import architecture.engine.Game
import architecture.engine.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.my.architecture.engine.structs.GameObject


class UITextInput(
        position: Vector2,
        var text: String,
        var onChange: (String) -> Unit
) : GameObject(
        arrayOf(),
        0f,
        0f,
        position
) {
    lateinit var style: TextFieldStyle
    lateinit var textfield: TextField
    override fun start() {
        style = TextFieldStyle()
        style.font = BitmapFont()
        style.fontColor = Color.WHITE

        textfield = TextField(text, style)
        textfield.width = 100f
        textfield.height = 30f
        style.font = BitmapFont()
        style.font.data.scaleX = 2f
        style.font.data.scaleY = 2f
        textfield.style = style
        textfield.setPosition(position.x, position.y)
        textfield.setTextFieldListener { textField, _ ->
            onChange(textField.text)
        }

    }

    override fun update(dt: Float) {
    }

    override fun onDispose() {
        textfield.isDisabled = true
        Game.renderer.textInputs.remove(textfield)
    }
}

