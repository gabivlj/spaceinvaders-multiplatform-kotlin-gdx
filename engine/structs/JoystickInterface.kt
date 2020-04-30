package architecture.engine.structs

import com.badlogic.gdx.math.Vector2

interface IJoystick {
    fun dist(): Float
    fun dir(): Vector2
    fun subscribe(gameObjectInput: GameObjectInput)
    fun unsubscribe(gameObjectInput: GameObjectInput)
}