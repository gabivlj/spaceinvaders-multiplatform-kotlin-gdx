package architecture.engine.structs

import architecture.engine.Game
import architecture.engine.Renderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.my.architecture.engine.structs.GameObject

fun V2V3(v: Vector2): Vector3 {
    return Vector3(v.x, v.y, 0.0f)
}

fun V3V2(v: Vector3): Vector2 {
    return Vector2(v.x, v.y)
}

/**
 * UIElement is a UI Helper element class which uses screen to camera coords.
 */
open class UIElement(sprites: Array<Sprite>, w: Float, h: Float, val initialPosition: Vector2) : GameObjectInput(sprites, w, h, initialPosition) {
    private var posCamera = Vector3()

    private val subscribers = mutableListOf<GameObjectInput>()


    /**
     * Stores all the current pointers handled by this UIElement (true if it's inside, false if it's done processing it or outside)
     *
     * @proposal Maybe change this to Array to obtain better performance.
     */
    private val pointers: HashMap<Int, Boolean> = hashMapOf()

    override fun start() {
        super.start()
        position = V3V2(Game.camera.unproject(V2V3(initialPosition)))
        posCamera = Game.camera.position.cpy()
        depth = Renderer.MAX_DEPTH
    }

    override fun update(dt: Float) {
        // If camera moves, change UI.
        if (posCamera.x != Game.camera.position.x || posCamera.y != Game.camera.position.y) {
            position = V3V2(Game.camera.unproject(V2V3(initialPosition)))
            posCamera = Game.camera.position.cpy()
        }
    }

    fun subscribe(gameObjectInput: GameObjectInput) {
        subscribers.add(gameObjectInput)
    }

    fun unsubscribe(gameObjectInput: GameObjectInput) {
        subscribers.remove(gameObjectInput)
    }

    open fun touched(position: Vector2, pointer: Int) {}

    open fun touchUp(position: Vector2, pointer: Int) {}

    open fun hover() {}

    open fun noHover() {}

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        var touchPosition = Vector2(screenX.toFloat(), screenY.toFloat())
        touchPosition = V3V2(Game.camera.unproject(V2V3(touchPosition)))
        if (sprite().boundingRectangle.contains(touchPosition)) {
            pointers[pointer] = true
            touched(touchPosition, pointer)
        }
        return false
    }

    /**
     * When a pointer that is being dragged is dragged out of the bounding box of the button.
     */
    open fun touchDraggedOut(pointer: Int) {}

    open fun touchDrag(pos: Vector2, pointer: Int) {}

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        var touchPosition = Vector2(screenX.toFloat(), screenY.toFloat())
        touchPosition = V3V2(Game.camera.unproject(V2V3(touchPosition)))
        if (sprite().boundingRectangle.contains(touchPosition)) {
            hover()
        } else {
            noHover()
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        var touchPosition = Vector2(screenX.toFloat(), screenY.toFloat())
        touchPosition = V3V2(Game.camera.unproject(V2V3(touchPosition)))
        val value = pointers[pointer]
        Gdx.app.log("TOUCH_UP", "${value}, $touchPosition")
        if (value != null && value) {
            touchUp(touchPosition, pointer)
            pointers[pointer] = false
        }

        return false
    }



    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val value = pointers[pointer] ?: return false
        if (!value) return false
        var touchPosition = Vector2(screenX.toFloat(), screenY.toFloat())
        touchPosition = V3V2(Game.camera.unproject(V2V3(touchPosition)))
        if (sprite().boundingRectangle.contains(touchPosition)) {
            touchDrag(touchPosition, pointer)
        } else {
            touchDraggedOut(pointer)
            pointers[pointer] = false
        }
        return false
    }
}