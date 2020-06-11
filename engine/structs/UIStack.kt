package architecture.engine.structs

import architecture.engine.World
import com.badlogic.gdx.utils.Array
import com.my.architecture.engine.structs.GameObject
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * UIStack handles inputs from gamePads/keyboards and translates that information
 * to buttons.
 *
 * UIStack will search for all the buttons in the scene on start and put them in the UIStack, so you don't have
 * to pass them to this class.
 *
 * Maybe I will put an optional parameter for passing custom UIButtons and not letting this class
 * search the scene for buttons, but at the moment this handles pretty well my use case.
 *
 * @param moveJoystick What joystick to use for movement
 * @param clickJoystick What button/joystick to use for click
 * @see PhysicalJoystick
 * @see VirtualJoystick
 * @see IJoystick
 */
class UIStack(
    private val moveJoystick: IJoystick,
    private val clickJoystick: IJoystick
) : GameObject() {

    private var buttons: Array<UIButton> = Array()
    private var currentButton: Int = -1
    private var canUseAgain: Float = 0f
    private val timer: Float = 0.5f

    override fun start() {
        buttons = findGameObjects()
    }

    override fun update(dt: Float) {
        if (canUseAgain < timer) {
            canUseAgain += dt
            return
        }
        clickButtonHandle()
        if (currentButton == -1) {
            currentButton = 0
            hoverButton()
        }

        if (abs(moveJoystick.dist()) < 0.5f) {
            return
        }
        canUseAgain = 0f
        val dir = moveJoystick.dist().sign
        buttons[currentButton].noHover()
        currentButton = clampOver(currentButton + dir.toInt(), 0, buttons.size - 1)
        hoverButton()
    }

    private fun clickButtonHandle() {
        if (clickJoystick.dist().absoluteValue < 0.5f) {
            return
        }
        canUseAgain = 0.0f
        buttons[currentButton].clickedFromOutside()
    }

    private fun hoverButton() {
        buttons[currentButton].hover()
    }

    /**
     * Clamps over the value so that there is no value less than minimum, and more than max
     */
    private fun clampOver(value: Int, min: Int, max: Int): Int {
        if (value < min) {
            return max
        }
        if (value > max) {
            return min
        }
        return value
    }
}
