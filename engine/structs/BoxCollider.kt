package architecture.engine.structs

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.my.architecture.engine.structs.GameObject

class BoxCollider(
        var width: Float,
        var height: Float,
        val gameObject: GameObject,
        var active: Boolean = false,
        var useSprite: Boolean = true
) {
    fun rectangle(): Rectangle {
        if (useSprite) {
            return Rectangle(gameObject.position.x, gameObject.position.y, gameObject.width, gameObject.height)
        }
        return Rectangle(gameObject.position.x, gameObject.position.y, width, height)
    }

    fun overlaps(collider: BoxCollider): Boolean {
        return Intersector.overlaps(collider.rectangle(), this.rectangle())
    }
}