package architecture.engine

import architecture.engine.structs.GameObjectInput
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3



/**
 * All the listeners
 */
class Input : InputProcessor, GestureDetector.GestureListener, ControllerListener {


    companion object {
        /**
         * This is static because we share it across the inputs of the engine, like the InputProcessor or the GestureListener one.
         */

        val input: Input
        get() = World.input
    }

    var subscribers: MutableList<GameObjectInput> = mutableListOf()

    fun subscribe(g: GameObjectInput) {
        // We use input.subscribers so we add it to the Main InputProcessor of the current world. So we make sure that the GestureDetector also uses the same subscribers
        input.subscribers.add(g)
    }

    fun unsubscribe(g: GameObjectInput) {
        input.subscribers.remove(g)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        input.subscribers.forEach { g -> g.touchUp(screenX, screenY, pointer, button) }
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        input.subscribers.forEach { g -> g.mouseMoved(screenX, screenY) }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        // todo: Put this on every event
        val subscribersSafe = subscribers.toMutableList()
        subscribersSafe.forEach { g -> g.keyTyped(character) }
        input.subscribers = subscribersSafe.toMutableList()
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        input.subscribers.forEach { g -> g.scrolled(amount) }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        input.subscribers.forEach { g -> g.keyUp(keycode) }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        input.subscribers.forEach { g -> g.touchDragged(screenX, screenY, pointer) }
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        val subscribersSafe = input.subscribers.toMutableList()
        subscribersSafe.forEach { g -> g.keyDown(keycode) }
        input.subscribers = subscribersSafe.toMutableList()
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        input.subscribers.forEach { g -> g.touchDown(screenX, screenY, pointer, button) }
        return false
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        input.subscribers.forEach { g -> g.fling(velocityX, velocityY, button) }
        return false
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        input.subscribers.forEach { g -> g.zoom(initialDistance, distance) }
        return false
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        input.subscribers.forEach { g -> g.pan(x, y, deltaX, deltaY) }
        return false
    }

    override fun pinchStop() {
        input.subscribers.forEach { g -> g.pinchStop() } //To change body of created functions use File | Settings | File Templates.
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        input.subscribers.forEach { g -> g.tap(x, y, count, button) }
        return false
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        input.subscribers.forEach { g -> g.panStop(x, y, pointer, button) }
        return false
    }

    override fun longPress(x: Float, y: Float): Boolean {
        input.subscribers.forEach { g -> g.longPress(x, y) }
        return false
    }

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        input.subscribers.forEach { g -> g.touchDown(x, y, pointer, button) }
        return false
    }

    override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean {
        input.subscribers.forEach { g -> g.pinch(initialPointer1, initialPointer2, pointer1, pointer2) }
        return false
    }

    override fun connected(controller: Controller?) {
        input.subscribers.forEach { g -> g.connected(controller) }
    }

    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        input.subscribers.forEach { g -> g.buttonUp(controller, buttonCode) }
        return false
    }

    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        input.subscribers.forEach { it.ySliderMoved(controller, sliderCode, value) }
        return false
    }

    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        input.subscribers.forEach { it.accelerometerMoved(controller, accelerometerCode, value) }
        return false
    }

    /**
     * PS4:
     *  LEFT STICK HORIZONTAL: AXIS CODE 0
     *  LEFT STICK VERTICAL: AXIS CODE 1
     *  RIGHT STICK HORIZONTAL: AXIS CODE 2
     *  RIGHT STICK VERTICAL: AXIS CODE 3
     *
     *  SQUARE: 0
     *  CROSS: 1
     *  TRIANGLE: 2
     *  CIRCLE: 3
     */
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        input.subscribers.forEach { it.axisMoved(controller, axisCode, value) }
        return false
    }

    override fun disconnected(controller: Controller?) {
        input.subscribers.forEach { it.disconnected(controller) }
    }

    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        input.subscribers.forEach { it.xSliderMoved(controller, sliderCode, value) }
        return false
    }

    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        input.subscribers.forEach { it.povMoved(controller, povCode, value) }
        return false
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        input.subscribers.forEach { it.buttonDown(controller, buttonCode) }
        return false
    }
}