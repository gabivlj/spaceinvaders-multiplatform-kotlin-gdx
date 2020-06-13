package architecture.engine.structs

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class BoxCollider(
        var width: Float,
        var height: Float,
        val gameObject: GameObject,
        var active: Boolean = false,
        var useSprite: Boolean = true
) {
    var position: Vector2? = null

    fun rectangle(): Rectangle {
        if (useSprite) {
            return Rectangle(gameObject.position.x, gameObject.position.y, gameObject.width, gameObject.height)
        }
        if (position == null) {
            return Rectangle(gameObject.position.x, gameObject.position.y, width, height)
        }
        val finalPos = position!!.cpy().add(gameObject.position)
        return Rectangle(finalPos.x, finalPos.y, width, height)
    }

    fun overlaps(collider: BoxCollider): Boolean {
        return Intersector.overlaps(collider.rectangle(), rectangle())
    }

    fun overlapPoint(point: Vector2): Boolean {
        return Intersector.overlaps(rectangle(), Rectangle(point.x, point.y, 1f, 1f))
    }
}