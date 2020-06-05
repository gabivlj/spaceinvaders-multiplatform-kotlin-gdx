package com.my.architecture.engine.structs

import architecture.engine.Renderer
import architecture.engine.World
import architecture.engine.structs.BoxCollider
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2



open class GameObject(
        var sprites: Array<Sprite> = arrayOf(),
        w: Float = 500.0f,
        h: Float = 500.0f,
        var position: Vector2 = Vector2(100.0f, -100.0f)
) {
    var flagDestroyed: Boolean = false
    var flipX: Boolean = false
    var flipY: Boolean = false
    var initialized: Boolean = false
    var width: Float = w
    var height: Float = h
    var rotation: Float = 0.0f
    var active: Boolean = true
    var spriteIndex: Int = 0
    var tag: String = ""
    var currentPass: Int = 0
    var observeDestroy: ((GameObject) -> Unit)? = null
    var tint: Color = Color.WHITE
    val sizeSprites: Int
    get() {
        return sprites.size
    }
    var disposed: Boolean = false

    var depth: Int = 20
        set(value) {
            field = value
            World.world.changedDepth()
        }
    var instanceID: Int = 0

    private var colliderMutable: BoxCollider = BoxCollider(width, height, this)

    val collider: BoxCollider
        get() = colliderMutable

    /**
     * Activates the collisions in the gameObject
     * @param width
     * @param height
     */
    fun activateCollisions(width: Float = colliderMutable.width, height: Float = colliderMutable.height, useGameObjectDimensions: Boolean = false) {
        if (useGameObjectDimensions) {
            colliderMutable.useSprite = true
            return
        }
        colliderMutable.width = width
        colliderMutable.height = height
        colliderMutable.active = true
    }

    fun deactivateCollisions() {
        colliderMutable.active = false
    }

    open fun update(dt: Float) {

    }

    open fun start() {}

    open fun onCollide(other: GameObject) {}

    /**
     * Gets the class that is in the inheritance tree of {this} gameObject. If it doesn't inherit or is the specified class, it will return null.
     * @return This gameObject casted to T
     */
    inline fun <reified T: GameObject> get(): T? {
        if (this is T) {
            return this as T
        }
        return null
    }

    inline fun <reified T: GameObject> check(actionWhenNotNull: (gameObject: T) -> (Unit)): Boolean {
        return if (this is T) {
            actionWhenNotNull(this)
            true
        } else false
    }

    fun sprite(): Sprite {
        if (sprites.isEmpty()) {
            sprites = arrayOf(Renderer.fallback()!!)
            return sprites[0]
        }
        if (sprites.size <= spriteIndex || spriteIndex < 0) throw ArrayIndexOutOfBoundsException() as Throwable
        return sprites[spriteIndex]
    }

    open fun onDispose() {

    }


    open fun onDestroy() {}

}