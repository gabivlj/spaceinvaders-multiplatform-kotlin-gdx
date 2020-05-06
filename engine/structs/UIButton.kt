package architecture.engine.structs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

enum class ButtonState(val index: Int) {
    IDLE(0),
    ACTIVE(1),
    HOVER(2),
}

/**
 * @param sprites 0 -> IDLE, 1 -> ACTIVE, 2 -> HOVER
 * @todo subscriber notifications
 * @todo move with IJoystick
 */
class UIButton(sprites: Array<Sprite>,
               w: Float, h: Float, position: Vector2, var text: String, var offsetText: Vector2, var onClick: (UIButton) -> Unit) : UIElement(sprites, w, h, position) {

    /**
     * The text element
     */
    lateinit var textUI: Text

    /**
     * Current state of this button
     */
    private var state: ButtonState = ButtonState.IDLE

    override fun start() {
        super.start()
        textUI = Text()
        textUI.text = text
        spriteIndex = state.index % sizeSprites
    }

    override fun update(dt: Float) {
        super.update(dt)
        textUI.position = position.cpy().add(offsetText)
        textUI.text = text + "state is ${state.name}"
        spriteIndex = state.index % sizeSprites
    }

    override fun hover() {
        when (state) {
            ButtonState.IDLE -> {
                state = ButtonState.HOVER
            }
            else -> { }
        }
        super.hover()
    }

    override fun touchDrag(pos: Vector2, pointer: Int) {}

    override fun touchDraggedOut(pointer: Int) {
        when (state) {
            ButtonState.ACTIVE -> {
                state = ButtonState.IDLE
            }
            else -> { }
        }
    }

    override fun touched(position: Vector2, pointer: Int) {
        when (state) {
            ButtonState.IDLE -> {
                state = ButtonState.ACTIVE
            }
            ButtonState.HOVER -> {
                state = ButtonState.ACTIVE
            }
            else -> { }
        }
    }

    override fun noHover() {
        when (state) {
            ButtonState.HOVER -> {
                state = ButtonState.IDLE
            }
            else -> { }
        }
    }

    override fun touchUp(position: Vector2, pointer: Int) {
        when (state) {
            ButtonState.ACTIVE -> {
                onClick(this)
                state = ButtonState.HOVER
            }
            else -> { /* Not supposed to happen */ }
        }
    }
}