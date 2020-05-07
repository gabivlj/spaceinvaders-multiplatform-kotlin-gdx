package architecture.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.input.GestureDetector
import com.my.architecture.engine.structs.GameObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.PerformanceCounter

/**
 * TODO:
 *  - dont delete instances in that moment, wait to delete them all.
 *  - have various cameras. []cameras
 *  - have colliders
 *  - put healthbar, special bar next to player
 *  - have a variable called screenCameras which is an array of cameras, depending of the number of cameras
 *    in that array the screen is splitted differently
 *  - sounds
 *  - have menu that lets you choose n of players and map
 *  - have final boss
 *  - have end screen which tells you your score
 */
class World {
    // region Private
    private var currentID: Int = 0
    private var _gameObjects: com.badlogic.gdx.utils.Array<GameObject> = com.badlogic.gdx.utils.Array<GameObject>(1000)

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
        gameObjects.forEach {
            if (!it.active) it.onDispose()
            if (currentPass != it.currentPass)
                it.flagDestroyed = true
            else it.onDispose()
        }
        _gameObjects = Array(1000)
        Game.renderer.reset()
        onFinish()


    }

    /**
     * Starts this world and Game class will use it as the current world. Will call the onStart attribute so you can load all the initial gameObjects etc.
     * @see Game.render
     */
    fun start() {
        restarted = true
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
        if (gameObject.currentPass != currentPass) gameObject.flagDestroyed = true
        else {
            if (!restarted)
                gameObject.onDestroy()
            gameObject.onDispose()
        }
        gameObjects.removeValue(gameObject, true)
    }

    var restarted = false


    /**
     * Instantiates a gameObject to world. If this world is the same as the current world, gameObject.start() will be fired.
     * @param gameObject GameObject to instantiate
     */
    fun <T: GameObject> instantiate(gameObject: T): T{
        gameObject.instanceID = currentID++
        gameObjects.add(gameObject)
        changedDepth()
        return gameObject
    }
    var currentIteration: List<GameObject> = listOf()

    var performanceCounterMap: HashMap<String, Float> = hashMapOf()
    val performanceCounter: PerformanceCounter = PerformanceCounter("heh")

    var currentPass: Int = 0
    /**
     * Executes .update() on every instantiated gameObject in the world
     */
    fun update() {
        val dt = Gdx.graphics.deltaTime
        currentIteration = gameObjects.filter { it.active }
        for (gameObject in currentIteration) {
            if (restarted) gameObject.onDispose()
            else if (gameObject.flagDestroyed) {
                gameObject.onDestroy()
                gameObject.onDispose()
                continue
            }

            gameObject.currentPass = currentPass

            if (!gameObject.initialized) {
                gameObject.start()
                gameObject.initialized = true
            }
            performanceCounter.start()
            gameObject.update(dt)
            performanceCounter.stop()
            performanceCounterMap[gameObject.toString()] = performanceCounter.current
            performanceCounter.reset()
        }

        currentPass++

        performanceCounterMap = hashMapOf()

        restarted = false
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
        for (element in currentIteration) {
            if (element.instanceID == gameObject.instanceID) continue
            // Reset because maybe the sprite has been compromised sharing references with other GameObjects
            element.sprite()?.setPosition(element.position.x, element.position.y)
            element.sprite()?.setSize(element.width, element.height)
            gameObject.sprite()?.setPosition(gameObject.position.x, gameObject.position.y)
            if (!Intersector.overlaps(element.sprite()?.boundingRectangle, gameObject.sprite()?.boundingRectangle)) continue
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
        val gameObjectsToReturn = com.badlogic.gdx.utils.Array<T>(gameObjects.size)
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