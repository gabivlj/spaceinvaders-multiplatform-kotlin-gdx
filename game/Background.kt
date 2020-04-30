package architecture.game

import architecture.engine.Renderer
import architecture.engine.World
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class Background(sprite: Sprite) : GameObject(arrayOf(sprite), 5_000f, 10_000f, Vector2(0.0f, 0.0f)){

    companion object {
        lateinit var currentBackground: Background
    }
    init {
        currentBackground = this
        World.world.instantiate(this)
        depth = 0
    }
}