package architecture.engine

import architecture.engine.structs.Text
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Logger
import com.my.architecture.engine.structs.GameObject

class Renderer {
    /**
     * Texture atlas in which all of the particle effects are stored
     */
    var particleAtlas: TextureAtlas = TextureAtlas()
    var effects: MutableList<ParticleEffect> = mutableListOf()

    private lateinit var batch: SpriteBatch
    /**
     * The main camera of the camera that this library sets up for you
     */
    lateinit var camera: OrthographicCamera
    /**
     * This is cameras, the list of cameras in your game that will try to render all
     * at the same time (split screen for you)!
     *
     * Take into mind that the more cameras are in the game more rendering work is gonna happen.
     */
    lateinit var cameras: MutableList<OrthographicCamera>

    init {
        log.level = Logger.DEBUG
    }
    companion object {
        const val MAX_DEPTH: Int = 1000
        private var acc = 0.0f
        private var times = 0
        public val log = Logger("rendererx")
        private var spritesStore: HashMap<String, Sprite> = HashMap()
        private var textures: MutableList<TextureRegion> = mutableListOf(TextureRegion())
        /**
         * Fill this if you are gonna have empty arrays in sprites in some objects
         */
        public var fallback: () -> Sprite? = { null }
        /**
         * Render size. The camera will be affected by this.
         */
        var sizeRenderer: Vector2 = Vector2(1921.0f, 1080.0f)

        var viewport: Vector2
            get() {
                return sizeRenderer
            }
            set(value) {
                sizeRenderer = value
                Game.camera.viewportHeight = sizeRenderer.y
                Game.camera.viewportWidth = sizeRenderer.x
                Game.camera.update()
            }

        /**
         * Returns the specified sprite depending on the texture path. If it doesn't exist in the RAM storage it stores it.
         * @param src Path
         * @return Sprite
         */
        fun sprite(src: String): Sprite {
            return spritesStore.getOrElse(src) {
                var spr = Sprite(Texture(src))
                spritesStore[src] = spr
                return spr
            }
        }

        fun proceduralSprite(g: GameObject): Sprite {
            val pm = Pixmap(g.width.toInt(), g.height.toInt(), Pixmap.Format.RGB888)
            pm.setColor(Color.RED)
            pm.fillRectangle(0, 0, g.width.toInt(), g.height.toInt())
            pm.setColor(Color.GREEN)
            pm.drawRectangle(0, 0, g.width.toInt(), g.height.toInt())
            val t = Texture(pm)
            pm.dispose()
            return Sprite(t)
        }
    }

    /**
     * @deprecated
     */
    fun render(world: World) {
        times++
        var beg = System.nanoTime()
        Gdx.gl.glClearColor(0f, 1.0f, 1.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (camera.viewportHeight != sizeRenderer.y) {
            camera.viewportHeight = sizeRenderer.y
        }

        if (camera.viewportWidth != sizeRenderer.x) {
            camera.viewportWidth = sizeRenderer.x
        }

        batch.setProjectionMatrix(camera.combined)
        // 629892ns 1000 objects
        val a = HashMap<Sprite, MutableList<GameObject>>()
        for (g in world.gameObjects) {
            val spr = g?.sprite()
            if (a.containsKey(spr)) a[spr]?.add(g)
            else {
                spr?.let { a.put(it, arrayListOf(g)) }
            }
        }
        batch.begin()
        for (sprite in a) {
            var gameObjects = sprite.value
            var spr = sprite.key
            for (gameObject in gameObjects) {
                spr.setOrigin(gameObject.width / 2, gameObject.height / 2)
                spr.rotation = gameObject.rotation
                spr.setBounds(gameObject.position.x, gameObject.position.y, gameObject.width, gameObject.height)
                spr.setPosition(
                    if (gameObject.flipX)
                        gameObject.position.x + gameObject.width
                    else
                        gameObject.position.x,
                    gameObject.position.y
                )
                spr.setScale(if (gameObject.flipX) -1.0f else 1.0f, 1.0f)
                spr.draw(batch)
            }
        }
        batch.end()
        log.info(batch.renderCalls.toString())
        acc += System.nanoTime() - beg
        log.info((acc / times).toString() + "ns")
    }

    var textUI: MutableList<Text> = mutableListOf()
    var textInputs: MutableList<TextField> = mutableListOf()

    fun reset() {
        textInputs = mutableListOf()
        textUI = mutableListOf()
        Animator.animations = mutableListOf()
        cameras = mutableListOf(camera)
        effects.forEach { it.dispose() }
        particleAtlas.dispose()
        camera.viewportWidth = sizeRenderer.x
        camera.viewportHeight = sizeRenderer.y
    }

    fun renderOptimized(world: World) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Animator.animations.forEach { it.update() }
        batch.begin()
        for ((nCamera, cameraItem) in cameras.withIndex()) {
            renderCamera(cameraItem, nCamera, world)
        }
        batch.end()
        Gdx.app.log("FPS", "${Gdx.graphics.framesPerSecond}")
    }

    private fun renderCamera(cameraRender: OrthographicCamera, nCamera: Int, world: World) {
        batch.projectionMatrix = cameraRender.combined
        Gdx.gl.glViewport(
            nCamera * Gdx.graphics.width / cameras.size,
            0,
            Gdx.graphics.width / cameras.size,
            Gdx.graphics.height
        )

        for (gameObject in world.gameObjects) {
            if (!gameObject.active) continue
            val sprite = gameObject.sprite()
            sprite.setOrigin(gameObject.width / 2, gameObject.height / 2)
            sprite.rotation = gameObject.rotation
            sprite.setBounds(gameObject.position.x, gameObject.position.y, gameObject.width, gameObject.height)
            sprite.setPosition(gameObject.position.x, gameObject.position.y)
            sprite.setScale(if (gameObject.flipX) -1.0f else 1.0f, 1.0f)
            sprite.color = gameObject.tint
            sprite.draw(batch)
        }
        for (text in textUI) {
            text.font.data.scaleX = text.width
            text.font.data.scaleY = text.height
            text.font.draw(batch, text.text, text.position.x, text.position.y)
        }
        for (textInput in textInputs) {
            textInput.draw(batch, 1f)
        }
        for (particle in effects) {
            particle.draw(batch, 0.01f)
        }
    }
    fun start() {
        batch = SpriteBatch(8191)
        camera = OrthographicCamera()
        cameras = mutableListOf(camera)
        camera.position.set(0f, 0f, 0f)
        camera.viewportHeight = sizeRenderer.y
        camera.viewportWidth = sizeRenderer.x
        camera.update()
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    fun addCamera(): OrthographicCamera {
        val cameraToAdd = OrthographicCamera()
        camera.position.set(0f, 0f, 0f)
        cameras.add(cameraToAdd)
        for (camera in cameras) {
            camera.viewportHeight = sizeRenderer.y / cameras.size
            camera.viewportWidth = sizeRenderer.x / cameras.size
        }
        return cameraToAdd
    }
}
