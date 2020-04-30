package architecture.engine

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.input.GestureDetector
import com.my.architecture.engine.structs.GameObject
import com.badlogic.gdx.math.Intersector

class World {
    // region Private
    private var currentID: Int = 0
    private var _gameObjects: MutableList<GameObject> = mutableListOf()

    // endregion

    // region Public
    val gameObjects: MutableList<GameObject>
    get() = _gameObjects
    // endregion

    private var _inputGesture: Input = Input()
    private var _inputProcess: Input = Input()

    private var _onDispose: () -> Unit = {}

    /**
     * Will always be called when the game is changed to this world
     */
    var onStart: () -> Unit = {}

    /**
     * This function is set by the user.
     * Will be called when the world is changed to another one.
     */
    var onFinish: () -> Unit = {}

    init {
        currentID = 1
    }

    companion object {

        private var currentInput = Input()
        val input: Input
        get() = currentInput

        /**
         * Previous world
         * It's used to call .dispose() in the start of the next update() and set it to null again
         */
        var prevWorld: World? = null

        /**
         * Current world
         */
        var world: World = World()

        var firstStart = true

        /**
         * Set the current world which Game class will use in render. Use this in Game.start(), if you are gonna change a world in update use world.start()
         * @see Game.render
         */
        fun setCurrentWorld(w: World) {
            world = w
        }
    }

    /**
     * Will be called when this world is stopped being used and will reset everything (Sprites, Animations etc.) so the next world isn't being affected
     */
    private fun dispose() {
        _gameObjects = mutableListOf()
        Game.renderer.reset()
        onFinish()
    }

    /**
     * Starts this world and Game class will use it as the current world. Will call the onStart attribute so you can load all the initial gameObjects etc.
     * @see Game.render
     */
    fun start() {
        val inputs: InputMultiplexer = InputMultiplexer()
        val gesture = GestureDetector(_inputGesture)
        Controllers.addListener(_inputProcess)
        // Add the gesture listener
        inputs.addProcessor(gesture)
        // Add the input listener
        inputs.addProcessor(_inputProcess)
        Gdx.input.inputProcessor = inputs
        currentInput = _inputProcess
        if (!firstStart) {
            world.dispose()
        }
        world = this
        onStart()
        firstStart = false
    }

    /**
     * Destroys a gameObject from the world.
     * @param gameObject GameObject you want to destroy
     */
    fun destroy(gameObject: GameObject) {
        _gameObjects = _gameObjects.filter { g -> g != gameObject }.toMutableList()
        gameObject.onDestroy()
    }

    fun restart() {
        Input.input.subscribers = mutableListOf()
        _gameObjects = mutableListOf()
    }

    /**
     * Instantiates a gameObject to world. If this world is the same as the current world, gameObject.start() will be fired.
     * @param gameObject GameObject to instantiate
     */
    fun <T: GameObject> instantiate(gameObject: T): T{
        gameObject.instanceID = currentID++
        _gameObjects.add(gameObject)
        changedDepth()
        return gameObject
    }

    /**
     * Will store the current update() copy of gameObjects so it's safe to make alterations to the original gameObject array in updates() and onCollides() etc.
     */
    var currentCopyOfGameObjects: MutableList<GameObject> = arrayListOf()

    /**
     * Executes .update() on every instantiated gameObject in the world
     */
    fun update() {
        val dt = Gdx.graphics.deltaTime
        currentCopyOfGameObjects = _gameObjects.toMutableList()
        for (gameObject in currentCopyOfGameObjects) {
            if (!gameObject.active) continue
            if (!gameObject.initialized) {
                gameObject.start()
                gameObject.initialized = true
            }
            gameObject.update(dt)
        }
    }

    /**
     * Check if the gameObject is overlapping with another. TODO: Have a GameObject collider and optimize the loop
     * Will call onCollide on the gameObjects that it collides with
     * @param gameObject to check
     * @return if it's overlapping
     */
    fun overlaps(gameObject: GameObject): Boolean {
        var overlaps = false
        gameObject.sprite()?.setPosition(gameObject.position.x, gameObject.position.y)
        for (otherGameObject in currentCopyOfGameObjects) {
            if (otherGameObject.instanceID == gameObject.instanceID) continue
            // Reset because maybe the sprite has been compromised sharing references with other GameObjects
            otherGameObject.sprite()?.setPosition(otherGameObject.position.x, otherGameObject.position.y)
            otherGameObject.sprite()?.setSize(otherGameObject.width, otherGameObject.height)
            gameObject.sprite()?.setPosition(gameObject.position.x, gameObject.position.y)
            if (!Intersector.overlaps(otherGameObject.sprite()?.boundingRectangle, gameObject.sprite()?.boundingRectangle)) continue
            gameObject.onCollide(otherGameObject)
            otherGameObject.onCollide(gameObject)
            overlaps = true
        }
        return overlaps
    }

    /**
     * Finds all the instantiated gameObjects in the current World
     * @return gameObjects
     */
    inline fun <reified T: GameObject> findGameObjects(): Array<T> {
        val gameObjectsToReturn = mutableListOf<T>()
        for (gameObject in gameObjects) {
            if (gameObject is T) {
                gameObjectsToReturn.add(gameObject)
            }
        }
        return gameObjectsToReturn.toTypedArray()
    }

    fun changedDepth() {
        world.gameObjects.sortBy { gameObject -> gameObject.depth }
    }
}