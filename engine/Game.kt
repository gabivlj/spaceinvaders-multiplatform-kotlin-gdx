package architecture.engine

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.PerformanceCounter


open class Game : ApplicationListener {

    // region Private variables
    private val renderer: Renderer = Renderer()
    val currentRenderer: Renderer
    get() = renderer

    companion object {
        fun restart() {
            World.world.start()
        }

        lateinit var currentGame: Game
        val camera: Camera
        get() = currentGame.currentRenderer.camera
        val renderer: Renderer
        get() = currentGame.currentRenderer

        fun SetPosCamera(v2: Vector2) {
            camera.position.set(0f, 0f, 0f)
            camera.viewportHeight = Renderer.sizeRenderer.y
            camera.viewportWidth = Renderer.sizeRenderer.x
            camera.update()
        }

        val isMobile: Boolean
            get() {
                return Gdx.app.type == Application.ApplicationType.Android || Gdx.app.type == Application.ApplicationType.Applet
            }
    }

    /**
     * We instantiate a world by default. But you can change it using World.SetCurrentWorld(yourOwnWorld) or yourOwnWorld.start()
     * @see World.setCurrentWorld static method
     * @see World.start instance method
     */
    private val world: World = World()
    // endregion

    /**
     * Your own initializer for the game, will fire before world.start() and renderer.start()
     */
    open fun start() {}

    // region Application overrides
    override fun create() {
        currentGame = this
        start()
        renderer.start()
        World.world.start()

    }

    override fun dispose() {
    }

    val performanceCounter: PerformanceCounter = PerformanceCounter("GAME")

    override fun render() {
        // We use the current world static variable because if the user wants to change to another world he can do it.
        renderer.renderOptimized(World.world)
        World.world.update()

    }

    override fun resize(width: Int, height: Int) {
//        Renderer.setSize(Vector2(width.toFloat(), height.toFloat()))
    }

    open override fun pause() {

    }

    open override fun resume() {

    }

    // endregion
}