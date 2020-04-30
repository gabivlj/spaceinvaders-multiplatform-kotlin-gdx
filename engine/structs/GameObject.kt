package com.my.architecture.engine.structs

import architecture.engine.Renderer
import architecture.engine.World
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2


open class GameObject(spritesSet: Array<Sprite> = arrayOf(), w: Float = 500.0f, h: Float = 500.0f, position: Vector2 = Vector2(100.0f, -100.0f)) {

    var flipX: Boolean = false
    var flipY: Boolean = false
    var initialized: Boolean = false
    var width: Float = w
    var height: Float = h
    var position: Vector2 = position
    var rotation: Float = 0.0f
    var active: Boolean = true
    var spriteIndex: Int = 0
    var tag: String = ""

    val sizeSprites: Int
    get() {
        return sprites.size
    }

    var depth: Int = 20
        set(value) {
            World.world.changedDepth()
            field = value
        }

    private var sprites: Array<Sprite> = spritesSet


    var instanceID: Int = 0

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
            sprites = sprites.copyInto(arrayOf(Renderer.proceduralSprite(this)))
            return sprites[0]
        }
        if (sprites.size <= spriteIndex || spriteIndex < 0) throw ArrayIndexOutOfBoundsException() as Throwable
        return sprites[spriteIndex]
    }

    fun dispose() {
        sprites = arrayOf()
    }


    open fun onDestroy() {}

}