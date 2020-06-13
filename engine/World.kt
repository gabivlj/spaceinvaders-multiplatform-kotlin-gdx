package architecture.engine

import architecture.engine.structs.BoxCollider
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.PerformanceCounter
import com.my.architecture.engine.structs.GameObject

class World {

    // region Private
    private var currentID: Int = 0
    private var _gameObjects: Array<GameObject> = Array(1000)
    private var _colliders: Array<BoxCollider> = Array(1000)

    var colliders: Array<BoxCollider> = Array()
        get() = _colliders

    // endregion

    // region Public
    val gameObjects: com.badlogic.gdx.utils.Array<GameObject>
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
        restarted = true
        gameObjects.forEach {
            if (it.dontDestroy) {
                return@forEach
            }
            it.initialized = false
            it.onDispose()
            it.disposed = true
        }
        _gameObjects = Array(_gameObjects.filter { it.dontDestroy }.toTypedArray())
        Game.renderer.reset()
        onFinish()
        Audio.dispose()
        Input.input.subscribers = mutableListOf()
    }

    lateinit var inputMultiplexer: InputMultiplexer

    /**
     * Starts this world and Game class will use it as the current world. Will call the onStart attribute so you can load all the initial gameObjects etc.
     * @see Game.render
     */
    fun start() {
        restarted = true
        inputMultiplexer = InputMultiplexer()
        val gesture = GestureDetector(_inputGesture)
        Controllers.addListener(_inputProcess)
        inputMultiplexer.addProcessor(gesture)
        inputMultiplexer.addProcessor(_inputProcess)
        Gdx.input.inputProcessor = inputMultiplexer
        currentInput = _inputProcess
        // If it isn't the first time that this is called, we dispose prev. world
        if (!firstStart) {
            world.dispose()
        }
        this._gameObjects = world._gameObjects
        world = this
        if (firstStart) {
            firstStart = false
            restarted = false
            onStart()

        }
    }

    /**
     * Destroys a gameObject from the world.
     * @param gameObject GameObject you want to destroy
     */
    fun destroy(gameObject: GameObject) {
        if (gameObject.dontDestroy) return
        if (gameObject.currentPass != currentPass && looping) gameObject.flagDestroyed = true
        else {
            if (!restarted) {

                gameObject.observeDestroy?.invoke(gameObject)
                gameObject.onDestroy()
            }
            gameObject.onDispose()
        }
        gameObjects.removeValue(gameObject, true)
    }

    var restarted = false


    /**
     * Instantiates a gameObject to world. If this world is the same as the current world, gameObject.start() will be fired.
     * @param gameObject GameObject to instantiate
     */
    fun <T: GameObject> instantiate(gameObject: T): T {
        if (restarted) return gameObject
        gameObject.disposed = false
        gameObject.instanceID = currentID++
        gameObjects.add(gameObject)
        changedDepth()
        return gameObject
    }
    var currentIteration: MutableList<GameObject> = mutableListOf()

    var performanceCounterMap: HashMap<String, Float> = hashMapOf()
    val performanceCounter: PerformanceCounter = PerformanceCounter("heh")

    var currentPass: Int = 0
    var looping = false
    /**
     * Executes .update() on every instantiated gameObject in the world
     */
    fun update() {
        val dt = Gdx.graphics.deltaTime
        currentIteration = mutableListOf()
        _colliders.clear()
        for (gameObject in gameObjects) {
            if (gameObject.active) {
                currentIteration.add(gameObject)
            }
            if (gameObject.active && gameObject.collider.active) {
                _colliders.add(gameObject.collider)
            }
        }

        looping = true
        for (gameObject in currentIteration) {

            if (gameObject.disposed) continue
            if (restarted) {
                gameObject.onDispose();
                continue
            }
            else if (gameObject.flagDestroyed) {
                gameObject.onDestroy()
                gameObject.observeDestroy?.invoke(gameObject)
                gameObject.onDispose()
                continue
            }
            gameObject.currentPass = currentPass
            if (!gameObject.initialized) {
                gameObject.start()
                gameObject.initialized = true
            }
            gameObject.update(dt)
        }
        looping = false
        currentPass++
        if (restarted) {
            restarted = false
            if (!firstStart) {
                onStart()
            }
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
        // NOTE: Consider deleting this!
        gameObject.sprite().setPosition(gameObject.position.x, gameObject.position.y)
        for (collider in _colliders) {
            if (!collider.active) continue
            val element = collider.gameObject
            if (element.instanceID == gameObject.instanceID) continue
            // Reset because maybe the sprite has been compromised sharing references with other GameObjects
            element.sprite().setPosition(element.position.x, element.position.y)
            gameObject.sprite().setPosition(gameObject.position.x, gameObject.position.y)
            if (!gameObject.collider.overlaps(collider)) continue
            gameObject.onCollide(element)
            element.onCollide(gameObject)
            overlaps = true
        }
        return overlaps
    }

    /**
     * Finds all the instantiated gameObjects in the current World
     * @return gameObjects
     */
    inline fun <reified T: GameObject> findGameObjects(): com.badlogic.gdx.utils.Array<T> {
        val gameObjectsToReturn = Array<T>(gameObjects.size)
        for (element in currentIteration) {
            if (element is T) {
                gameObjectsToReturn.add(element)
            }
        }
        return gameObjectsToReturn
    }

    fun changedDepth() {
        world.gameObjects.sort { o1, o2 ->  o1.depth - o2.depth }
    }
}