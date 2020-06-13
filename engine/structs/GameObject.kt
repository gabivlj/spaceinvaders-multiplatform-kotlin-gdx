package com.my.architecture.engine.structs

import architecture.engine.Renderer
import architecture.engine.World
import architecture.engine.structs.BoxCollider
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

open class GameObject(
        var sprites: Array<Sprite> = arrayOf(),
        var width: Float = 0f,
        var height: Float = 0f,
        var position: Vector2 = Vector2(0f, 0f)
) {
    var dontDestroy: Boolean = false
    var flagDestroyed: Boolean = false
    var flipX: Boolean = false
    var flipY: Boolean = false
    var initialized: Boolean = false
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
    fun activateCollisions(
            width: Float = colliderMutable.width,
            height: Float = colliderMutable.height,
            position: Vector2? = null,
            useGameObjectDimensions: Boolean = false
    ) {
        if (useGameObjectDimensions) {
            colliderMutable.useSprite = true
            colliderMutable.active = true
            return
        }
        if (position != null) {
            colliderMutable.position = position
        }
        colliderMutable.width = width
        colliderMutable.height = height
        colliderMutable.active = true
        colliderMutable.useSprite = false
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


    companion object {

        // Fast World access/modifiers

        /**
         * Instantiates a gameObject to world. If this world is the same as the current world, gameObject.start() will be fired.
         * @param gameObject GameObject to instantiate
         */
        fun <T : GameObject> instantiate(gameObject: T): T {
            return World.world.instantiate(gameObject)
        }

        /**
         * Finds all the instantiated gameObjects in the current World
         * @return gameObjects
         */
        inline fun <reified T : GameObject> findGameObjects(): com.badlogic.gdx.utils.Array<T> {
            val gameObjectsToReturn = com.badlogic.gdx.utils.Array<T>(World.world.gameObjects.size)
            for (element in World.world.currentIteration) {
                if (element is T) {
                    gameObjectsToReturn.add(element)
                }
            }
            return gameObjectsToReturn
        }

        /**
         * Destroys a gameObject from the world.
         * @param gameObject GameObject you want to destroy
         */
        fun destroy(gameObject: GameObject) {
            return World.world.destroy(gameObject)
        }
    }
    /**
     * Check if the gameObject is overlapping with another.
     * Will call onCollide on the gameObjects that it collides with
     * @return if it's overlapping
     */
    fun overlaps(): Boolean {
        return World.world.overlaps(this)
    }

    /**
     * Util method that centers the gameObject.
     */
    fun center(): Vector2 {
        position.x -= width / 2f
        position.y -= height / 2f
        return position
    }

    fun centerRight(): Vector2 {
        position.x += width / 2f
        position.y += height / 2f
        return position
    }

}