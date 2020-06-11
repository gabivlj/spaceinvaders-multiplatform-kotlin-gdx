package architecture.engine.structs

import architecture.engine.Game.Companion.camera
import architecture.engine.Renderer
import architecture.engine.World
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.my.architecture.engine.structs.GameObject
import kotlin.math.abs

/**
 * VirtualJoystick gets input from the screen.
 * Provides a opportunity to pass optional callbacks that will be called when dir() and dist() are called and
 * the game isn't running on mobile devices
 */
open class VirtualJoystick(
    // Joystick sprite
    private val spriteForJoystick: Sprite,
    // Area sprite
    private val area: Sprite,
    // Tag of the joystick
    tag: String,
    private val initialPosition: Vector2,
    // Width of the Area
    private val widthOutside: Float,
    // Width of the Joystick
    private val widthInside: Float,
    // SensibilityThreshold represents the value that will be useful for checking if the joystick has more or less sensibility
    private val sensibilityThreshold: Float = 30f,
    // Range when there is a touch and the joystick isn't dragging. (min, max).
    private val touchRange: Pair<Int, Int> = Pair(0, 1000),
    public val maximumValueOnDistCall: Float = 5.0f,
    // Function that will be fired when dir() is called and the device is not mobile
    private val dirWhenNotMobile: () -> (Vector2) = { Vector2() },
    // Function that will be fired when dist() is called and the device is not mobile
    private val distWhenNotMobile: (j: VirtualJoystick) -> (Float) = { 0.0f }
) : GameObjectInput(), IJoystick {

    init {
        this.tag = tag
    }

    val subscribers: MutableList<GameObjectInput> = mutableListOf()

    val isMobile: Boolean = Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.Applet

    override fun subscribe(gameObject: GameObjectInput) {
        subscribers.add(gameObject)
    }

    override fun unsubscribe(gameObject: GameObjectInput) {
        subscribers.remove(gameObject)
    }

    private var currentPointer: Int = -1

    private enum class State() {
        STATIC(),
        MOVING(),
    }

    private class JoystickInside(sprite: Sprite, val newWidth: Float) : GameObject(arrayOf(sprite)) {
        override fun start() {
            super.start()
            height = newWidth
            width = newWidth
            depth = Renderer.MAX_DEPTH
        }
    }

    private class JoystickOutside(sprite: Sprite, val newWidth: Float) : GameObject(arrayOf(sprite)) {
        override fun start() {
            depth = Renderer.MAX_DEPTH - 1
            width = newWidth
            height = newWidth
        }
    }

    private lateinit var joystickInside: JoystickInside
    private lateinit var joystickOutside: JoystickOutside
    private var centerToOutsideDistance: Float = 0.0f
    // Pivot of the inside joystick that will be set when there is drag or touch
    private var currentPivotInside: Vector2 = Vector2()
    // Pivot of the out joystick that will be set when there is drag or touch and depending on the distance and position of the joystickInside
    private var currentPivotOutside: Vector2 = Vector2()
    private var currentState: State = State.STATIC
    private var initPos: Vector2 = Vector2(-1f, -1f)

    private var cameraPosOnTouch: Vector2 = Vector2()

    // region LIFECYCLES
    override fun start() {
        super.start()
        width = 0.0f
        height = 0.0f
        joystickInside = JoystickInside(area, widthInside)
        joystickOutside = JoystickOutside(spriteForJoystick, widthOutside)
        centerToOutsideDistance = joystickInside.width / 2
        currentPivotInside = initialPosition

        instantiate(joystickInside)
        instantiate(joystickOutside)

        joystickInside.position = Vector2(joystickOutside.position.x + widthOutside / 2, joystickOutside.position.y + widthOutside / 2)
        initPos = unproject(initialPosition)
    }

    var prevPositionInside: Vector2 = Vector2()

    override fun update(dt: Float) {
        if (!isMobile) {
            activate(false)
            return
        }

        val offsetCameraX = camera.position.x - cameraPosOnTouch.x
        val offsetCameraY = camera.position.y - cameraPosOnTouch.y
        when (currentState) {
            State.STATIC -> {
                initPos = unproject(initialPosition)
                // Center inside position
                joystickInside.position = Vector2(
                    (initPos.x + joystickOutside.width / 2) - joystickInside.width / 2,
                    (initPos.y + joystickOutside.height / 2) - joystickInside.height / 2
                )
                joystickOutside.position = Vector2(initPos.x, initPos.y)
            }
            State.MOVING -> {
                cameraPosOnTouch = Vector2(camera.position.x, camera.position.y)
                joystickOutside.position = currentPivotOutside
                joystickOutside.position.x += offsetCameraX
                joystickOutside.position.y += offsetCameraY
                joystickInside.position = currentPivotInside
                joystickInside.position.x += offsetCameraX
                joystickInside.position.y += offsetCameraY
                val centerOutside = centerPivot(joystickOutside)
                val centerInside = centerPivot(joystickInside)
                if (centerOutside.dst(centerInside) > joystickOutside.width / 2) {

                    val dirX = centerInside.x - centerOutside.x
                    val dirY = centerInside.y - centerOutside.y
                    val norm = Vector2(dirX, dirY).nor()
                    norm.x *= widthInside / 4 + abs(joystickInside.position.x - offsetCameraX - prevPositionInside.x)
                    norm.y *= widthInside / 4 + abs(joystickInside.position.y - offsetCameraY - prevPositionInside.y)
                    currentPivotOutside = currentPivotOutside.add(norm)
                }
            }
        }

        prevPositionInside = joystickInside.position
    }

    override fun onDestroy() {
        World.world.destroy(joystickOutside)
        World.world.destroy(joystickInside)
    }

    // endregion

    // region EVENTS

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (screenX < touchRange.first || screenX > touchRange.second) return super.touchDown(screenX, screenY, pointer, button)
        cameraPosOnTouch = Vector2(camera.position.x, camera.position.y)
        currentPointer = pointer

        if (currentState == State.STATIC) {
            currentState = State.MOVING
            var newPivot = Vector2(screenX.toFloat(), screenY.toFloat())
            newPivot = unproject(newPivot)
            if (newPivot.dst(centerPivot(initPos, joystickOutside.width, joystickOutside.width)) > joystickOutside.width) {
                currentPivotInside = Vector2(newPivot.x - joystickInside.width / 2, newPivot.y - joystickInside.height / 2)
                currentPivotOutside = Vector2(newPivot.x - joystickOutside.width / 2, newPivot.y - joystickOutside.height / 2)
            } else {
                currentPivotInside = Vector2(newPivot.x - joystickInside.width / 2, newPivot.y - joystickInside.height / 2)
                // Copy vector explicitly because JVM AND GARBAGE COLLECTORS :)!!!!!!!
                currentPivotOutside = initPos.cpy()
            }
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (currentPointer != pointer) return super.touchUp(screenX, screenY, pointer, button)
        currentPointer = -1

        if (subscribers.isEmpty()) {
            currentState = State.STATIC
            return super.touchUp(screenX, screenY, pointer, button)
        }
        val dir = dir()
        val dist = dist()
        callSubscribers(dir, dist)
        currentState = State.STATIC
        return super.touchUp(screenX, screenY, pointer, button)
    }

    fun callSubscribers(dir: Vector2, dist: Float) {
        val copy = subscribers.toTypedArray()
        for (element in copy) {
            element.touchUpJoystick(dir, dist, tag)
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (pointer != currentPointer) return true
        val newPivot = Vector2(screenX.toFloat(), screenY.toFloat())
        val unprojected = unproject(newPivot)
        unprojected.x -= joystickInside.width / 2
        unprojected.y -= joystickInside.height / 2
        currentPivotInside = unprojected
        return true
    }

    // endregion

    // region HELPERS
    private fun unproject(v2: Vector2): Vector2 {
        val unproj = camera.unproject(
            Vector3(v2.x, v2.y, 0.0f)
        )
        return Vector2(unproj.x, unproj.y)
    }

    private fun centerPivot(g: GameObject): Vector2 {
        return Vector2(g.position.x + g.width / 2f, g.position.y + g.height / 2f)
    }

    private fun centerPivot(pos: Vector2, w: Float, h: Float): Vector2 {
        return Vector2(pos.x + w / 2f, pos.y + h / 2f)
    }

    // endregion

    // region PUBLIC METHODS

    /**
     * Return the direction of the joystick
     */
    override fun dir(): Vector2 {
        if (!isMobile) {
            return dirWhenNotMobile()
        }
        if (currentState == State.STATIC) return Vector2()
        val centerOutside = centerPivot(joystickOutside)
        val centerInside = centerPivot(joystickInside)
        val dirX = centerInside.x - centerOutside.x
        val dirY = centerInside.y - centerOutside.y
        return Vector2(dirX, dirY).nor()
    }

    override fun dist(): Float {
        if (!isMobile) {
            return distWhenNotMobile(this)
        }
        val centerOutside = centerPivot(joystickOutside)
        val centerInside = centerPivot(joystickInside)
        val dirX = centerInside.x - centerOutside.x
        val dirY = centerInside.y - centerOutside.y
        val len = Vector2(dirX, dirY).len()
        val magnitude = if (len < sensibilityThreshold) 0f else len / sensibilityThreshold
        return MathUtils.clamp(magnitude, 0f, maximumValueOnDistCall)
    }

    fun activate(activateJoy: Boolean) {
        joystickInside.active = active
        joystickOutside.active = active
        active = activateJoy
    }

    // endregion
}
