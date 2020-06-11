package architecture.engine.structs

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.Vector2
import kotlin.math.abs

enum class ToListen {
    LEFT_FACE,
    RIGHT_FACE,
    TOP_FACE,
    BOTTOM_FACE,

    RIGHT_BUMPER,
    LEFT_BUMPER,

    // For gamepads that have arrows (like the PS4)
    LEFT_BUTTON_1,
    RIGHT_BUTTON_1,
    TOP_BUTTON_1,
    DOWN_BUTTON_1,

    NONE,

    // Axis
    RIGHT_STICK,
    LEFT_STICK,
    LEFT_TRIGGER,
    RIGHT_TRIGGER,

    // For arcade type gamepads (tries from left to right and from top to bottom) (IF THERE ARE MORE BUTTONS LIKE A SECOND PLAYER THE SECOND PLAYER WILL BE
    // USE THE BUTTON_X_1 NOT BUTTON_X_0
    BUTTON_0_0,
    BUTTON_1_0,
    BUTTON_2_0,
    BUTTON_3_0,
    BUTTON_4_0,
    BUTTON_5_0,
    // Xinmotek: Center top button (left one)
    BUTTON_6_0,
    // Xinmotek: Coin button
    BUTTON_7_0,
    // Xinmotek: Top left buttons (right one)
    BUTTON_9_0,

    // Xinmotek: Right part
    BUTTON_0_1,
    BUTTON_1_1,
    BUTTON_2_1,
    BUTTON_3_1,
    BUTTON_4_1,
    BUTTON_5_1,
    // Xinmotek: Center top button (right one)
    BUTTON_6_1,
    // Xinmotek: Top right button
    BUTTON_7_1
};

enum class XinmotekButtonMappings {
    ;
    companion object {
        fun from(buttonCode: Int, controllerString: String): ToListen {
            val idx = if (controllerString == "#0 – Xinmotek Dual Controller") 0 else if (controllerString == "#1 – Xinmotek Dual Controller") 1 else -1
            if (idx <= -1) {
                return ToListen.NONE
            }
            return ToListen.valueOf("BUTTON_${buttonCode}_$idx")
        }

        fun toNormalGamepad(buttonCode: Int, controllerString: String): ToListen {
            if (controllerString != "#0 – Xinmotek Dual Controller" && controllerString == "#1 – Xinmotek Dual Controller") return ToListen.NONE
            val values = ToListen.values()
            return if (buttonCode < values.size) values[buttonCode] else ToListen.NONE
        }
    }
}

/**
 * Maybe the enum thing wasn't the greatest of ideas but Imma keep it going
 */

/**
 * I can't test this until the COVID 19 pandemic ends LMAO.
 *
 * This is a special Gamepad because in its entirety it can have 2 joysticks but they can be
 * 2 different gamepads? My work around here is that the right axis is the 2nd gamepad
 * and the left axis is the 1st gamepad
 */
enum class XinmotekAxisMappings(val value: Int, val representation: ToListen) {
    NONE(-1, ToListen.NONE),
    LEFT_AXIS_HORIZONTAL(1, ToListen.LEFT_STICK),
    LEFT_AXIS_VERTICAL(2, ToListen.LEFT_STICK),
    RIGHT_AXIS_HORIZONTAL(1, ToListen.RIGHT_STICK),
    RIGHT_AXIS_VERTICAL(2, ToListen.RIGHT_STICK);

    companion object {
        fun returnDirection(xinmotekAxisMappings: XinmotekAxisMappings, value: Float, before: Vector2): Vector2 {
            return when (xinmotekAxisMappings) {
                LEFT_AXIS_HORIZONTAL -> Vector2(value, before.y)
                LEFT_AXIS_VERTICAL -> Vector2(before.x, -value)
                RIGHT_AXIS_HORIZONTAL -> Vector2(value, before.y)
                RIGHT_AXIS_VERTICAL -> Vector2(before.x, -value)
                else -> before.cpy()
            }
        }

        fun from(value: Int, controllerString: String): XinmotekAxisMappings {
            // I think there can be smarter ways to do this but at the moment it will work
            if (controllerString == "#0 – Xinmotek Dual Controller") {
                // We filter because the are more than 1 values
                val find = values().filter { it.value == value }
                if (find.size <= 0 || find.size > 2) {
                    return NONE
                }
                return find[0]
            }
            if (controllerString == "#1 – Xinmotek Dual Controller") {
                // We filter because the are more than 1 values
                val find = values().filter { it.value == value }
                if (find.size <= 0 || find.size > 2) {
                    return NONE
                }
                return find[1]
            }
            return NONE
        }
    }
}

enum class PS4ButtonMappings(val value: Int, val representation: ToListen) {
    SQUARE_BUTTON(0, ToListen.LEFT_FACE),
    TRIANGLE_BUTTON(2, ToListen.TOP_FACE),
    CIRCLE_BUTTON(3, ToListen.RIGHT_FACE),
    CROSS_BUTTON(1, ToListen.BOTTOM_FACE),
    NONE(-1, ToListen.NONE);

    companion object {
        /**
         * TODO: This might be inefficient
         */
        fun from(value: Int, controllerString: String): PS4ButtonMappings {
            // THIS should be always a PS4 controller (I think)
            if (controllerString != "Sony Computer Entertainment Wireless Controller") return PS4ButtonMappings.NONE
            // WARNING: This crashes with the R2 and L2 axis (not implemented yet)
            val find = PS4ButtonMappings.values().find { it.value == value }
            return find ?: PS4ButtonMappings.NONE
        }
    }
}

/**
 * Bugs:
 *  -  The joystick seems to act like a RAW input, there is no sensibility at all. (I still don't know why)
 */
enum class PS4AxisMappings(val value: Int, val representation: ToListen) {
    NONE(-1, ToListen.NONE),
    LEFT_AXIS_HORIZONTAL(0, ToListen.LEFT_STICK),
    LEFT_AXIS_VERTICAL(1, ToListen.LEFT_STICK),
    RIGHT_AXIS_HORIZONTAL(2, ToListen.RIGHT_STICK),
    RIGHT_AXIS_VERTICAL(3, ToListen.RIGHT_STICK);

    companion object {
        fun returnDirection(ps4Mapping: PS4AxisMappings, value: Float, before: Vector2): Vector2 {
            return when (ps4Mapping) {
                LEFT_AXIS_HORIZONTAL -> Vector2(value, before.y)
                LEFT_AXIS_VERTICAL -> Vector2(before.x, -value)
                RIGHT_AXIS_HORIZONTAL -> Vector2(value, before.y)
                RIGHT_AXIS_VERTICAL -> Vector2(before.x, -value)
                else -> before.cpy()
            }
        }

        /**
         * TODO: This might be inefficient
         */
        fun from(value: Int, controllerString: String): PS4AxisMappings {
            // THIS should be always a PS4 controller (I think)
            if (controllerString != "Sony Computer Entertainment Wireless Controller") return NONE
            // WARNING: This crashes with the R2 and L2 axis (not implemented yet)
            val find = values().find { it.value == value }
            return find ?: NONE
        }
    }
}

/**
 * We'll use this as a keyboard and joystick listener
 * @param keyCodeFallback When the keyboard is used, this will be used to listen to key inputs. (x, -x, y, -y) MUST BE SIZE 4 or it will throw an assertion exception
 */
class PhysicalJoystick(
    private val whatToListen: ToListen,
    private val keyCodeFallback: Array<Int> = arrayOf(-1, -1, -1, -1),
    private val maxValueOnDist: Float = 3.0f
) : GameObjectInput(arrayOf(), 0.0f, 0.0f, Vector2(0.0f, 0.0f)), IJoystick {
    val subscribers: MutableList<GameObjectInput> = mutableListOf()

    var direction: Vector2 = Vector2()
    var totalValue: Vector2 = Vector2()
    var usingKeys: Boolean = false

    val usingJoystick: Boolean
        get() {
            return !usingKeys
        }

    init {
        assert(keyCodeFallback.size == 4)
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        val controllerString = controller.toString()
        val ps4Map = PS4ButtonMappings.from(buttonCode, controllerString)
        if (ps4Map.representation == whatToListen) {
            subscribers.forEach { it.buttonDownParsed(this, whatToListen) }
            return false
        }

        val xinmotekMap = if (
            whatToListen == ToListen.LEFT_FACE ||
            whatToListen == ToListen.RIGHT_FACE ||
            whatToListen == ToListen.TOP_FACE ||
            whatToListen == ToListen.BOTTOM_FACE
        )
            XinmotekButtonMappings.toNormalGamepad(buttonCode, controllerString)
        else XinmotekButtonMappings.from(buttonCode, controllerString)
        if (xinmotekMap == whatToListen) {
            subscribers.forEach { it.buttonDownParsed(this, whatToListen) }
            return false
        }
        return false
    }

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        var valueCheck = value
        // TODO: Let the user set this val?
        if (abs(value) < 0.2f) {
            valueCheck = 0.0f
        } else {
            usingKeys = false
        }
        val controllerString = controller.toString()
        val ps4Map = PS4AxisMappings.from(axisCode, controllerString)
        // Stop processing here
        if (ps4Map.representation == whatToListen) {
            totalValue = PS4AxisMappings.returnDirection(ps4Map, valueCheck, direction)
            // Set the new direction
            direction = totalValue.nor()
            return false
        }
        // more inputs ... (todo)
        val xinmotekAxisMappings = XinmotekAxisMappings.from(axisCode, controllerString)
        if (xinmotekAxisMappings.representation == whatToListen) {
            totalValue = XinmotekAxisMappings.returnDirection(xinmotekAxisMappings, valueCheck, direction)
            // Set the new direction
            direction = totalValue.nor()
            return false
        }
        return false
    }

    val keyValues = arrayOf(0, 0, 0, 0)

    override fun update(dt: Float) {
        if (usingKeys) {
            direction.x = (keyValues[0] - keyValues[1]).toFloat()
            direction.y = (keyValues[2] - keyValues[3]).toFloat()
            direction = direction.nor()
            totalValue = direction.cpy()
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        val idx = keyCodeFallback.indexOf(keycode)
        if (idx < 0) {
            return false
        }

        keyValues[idx] = 0
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        val idx = keyCodeFallback.indexOf(keycode)
        if (idx < 0) {
            return false
        }
        keyValues[idx] = 1
        usingKeys = true

        // (NOTE): Maybe create a copy if the user decides to unsubscribe or change the subscriber
        //         array inside an event?
        subscribers.forEach { it.buttonDownParsed(this, whatToListen) }
        return false
    }

    override fun dist(): Float {
        if (totalValue.len() < 0.2f) {
            return 0f
        }
        // We multiply by maxValueOnDist because I know that the values of the magnitude would be between 0-1
        return totalValue.len() * maxValueOnDist
    }

    override fun dir(): Vector2 {
        return direction.cpy()
    }

    override fun subscribe(gameObjectInput: GameObjectInput) {
        subscribers.add(gameObjectInput)
    }

    override fun unsubscribe(gameObjectInput: GameObjectInput) {
        subscribers.remove(gameObjectInput)
    }
}
