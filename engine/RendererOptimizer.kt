package architecture.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.Logger
import java.nio.IntBuffer

/**
 * Represents texture location in the big texture.
 */
class TextureInfo {
    var x: Int = 0
    var y: Int = 0
    var w: Int = 0
    var h: Int = 0

    constructor(xi: Int, yi: Int, width: Int, height: Int) {
        x = xi
        y = yi
        w = width
        h = height
    }

    constructor() {}
}

/**
 * Texture optimizer saves textures in a big pixmap.
 */
class TextureOptimizer() {

    var currentX: Int = 0
    var currentY: Int = 0
    var maxX: Int = 0
    var maxY: Int = 0
    var size: Int = 10000
    var pixmap: Pixmap
    var tInfos: MutableList<TextureInfo> = mutableListOf()

    init {
        val intBuffer: IntBuffer = BufferUtils.newIntBuffer(16)
        Gdx.gl20.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, intBuffer)
        size = intBuffer.get()
        log.error("$size")
        pixmap = Pixmap(2000, 2000, Pixmap.Format.RGBA8888)
    }
    companion object {
        val log = Logger("renderer")
    }

    fun dispose() {
        pixmap.dispose()
    }

    /**
     * Adds a texture to the big texture of TextureOptimizer
     */
    fun addTexture(t: Texture): TextureInfo? {
        if (t.width > size || t.height > size) {
            return null
        }
        if (t.height + currentY >= size) {
            return null
        }
        // Reset the currentX if it's gonna go out of sizing the texture
        if (t.width + currentX >= size) {
            currentX = 0
            maxY = maxY.coerceAtLeast(t.height) // Math.max(maxY, height)
            currentY += maxY + 1
        }
        // Prepare the texture data for pixmap
        val tData = t.textureData
        if (!tData.isPrepared) tData.prepare()
        var tPixmap = tData.consumePixmap()
        // Add each pixel to our own pixel map
        for (i in currentX..t.width+currentX) {
            // The X of the local texture
            val currentXT = i - currentX
            for (j in currentY..t.height+currentY) {
                // The Y of the local texture
                val currentYT = j - currentY
                // Map local pixel to global pixel
                pixmap.setColor(tPixmap.getPixel(currentXT, currentYT))
                pixmap.drawPixel(i, j)
            }
        }
        // Generate information of the Texture we have drawn and add it to the array of information
        val tInfo = TextureInfo(currentX, currentY, t.width, t.height)
        tInfos.add(tInfo)
        // Check again everything is alright with the dimensions
        maxY = maxY.coerceAtLeast(t.height)
        currentX += t.width + 1
        // Reset the currentX if it's going out of sizing..
        maxX = maxX.coerceAtLeast(currentX)
        if (currentX >= size) {

            currentX = 0
            currentY += maxY + 1
            maxY = 0
        }
        return tInfo
    }
}

/**
 * RendererOptimizer is a class that you can use to load different files into a big texture so later on you won't have batch misses.
 * It lets you load the big texture on runtime or save the consumed sprites into a separate file with a JSON config and a PNG that you can use to
 * consume faster and efficiently when the game starts.
 *
 * Each RendererOptimizer can be considered as a structure that stores in memory the sprites and the texture.
 */
class RendererOptimizer {

    var spriteStore: MutableList<Sprite> = mutableListOf()

    /**
     * Big texture that has all the sprites when consumeSprites is called
     */
    private lateinit var bigTexture: Texture
    /**
     * TextureOptimizer that stores in a PixMap all the sprites for creating a texture by later saving it in the system.
     */
    private var pixmap: TextureOptimizer = TextureOptimizer()

    /**
     * Loads a sprite to the big texture so you can consume it later. Take into mind that loading sprites this way will be a heavy load to the CPU.
     * Recommendation is that you save the consumed sprites with the function saveConsumedSprites and then use that JSON file and png to load the sprites faster...
     * Another recommendation is saving from smaller height sprites to larger for a more concise and optimized texture. NOTE: Later on this won't be a problem
     * @param src Path to the texture
     * @return If the sprite was successfully added. Probably if it returns false it's because the texture cannot handle more textures!
     */
    fun sprite(src: String): Boolean {
        val texture = Texture(src)
        return pixmap.addTexture(texture) != null
    }

    /**
     * Saves the consumed sprites in the filesystem generating a JSON File and the correspondent texture images so next time the process of consuming is faster.
     */
    fun saveConsumedSprites(name: String) {
        val fl = FileHandle("$name.png")
        // (OPTIMIZATION) Save again the image if we can to a smaller image
        val lastPix = Pixmap(pixmap.maxX, pixmap.currentY + pixmap.maxY, Pixmap.Format.RGBA8888)
        for (i in 0..pixmap.size) {
            for (j in 0..pixmap.currentY + pixmap.maxY) {
                lastPix.setColor(pixmap.pixmap.getPixel(i, j))
                lastPix.drawPixel(i, j)
            }
        }
        PixmapIO.writePNG(fl, lastPix)
        val json = Json()
        val jsonFile = FileHandle("$name.json")
        pixmap.pixmap.dispose()
        jsonFile.writeString(json.prettyPrint(pixmap.tInfos),false)
    }

    /**
     * Consumes the sprites that you added with sprite()
     * @return The list of sprites ordered in which sprite was called to load first.
     */
    fun consumeSprites(): MutableList<Sprite> {
        bigTexture = Texture(pixmap.pixmap)
        return consume()
    }

    var regions: MutableList<TextureRegion> = mutableListOf()

    /**
     * Gets whatever is stored in bigTexture depending on tInfos
     */
    private fun consume(): MutableList<Sprite> {
        val arr: MutableList<Sprite> = mutableListOf()
        for (textureInfo in pixmap.tInfos) {
            regions.add(TextureRegion(bigTexture, textureInfo.x, textureInfo.y, textureInfo.w, textureInfo.h))
            arr.add(Sprite(regions.last()))
        }
        //spriteStore.addAll(arr)
        return arr
    }

    fun dispose() {
        pixmap.dispose()
        bigTexture.dispose()
        regions.last().texture.dispose()
        regions = mutableListOf()
    }

    /**
     * Consumes the sprites that you added with sprite()
     * @return The list of sprites ordered in which sprite was called to load first.
     */
    fun consumeSprites(texture: Texture): MutableList<Sprite> {
        bigTexture = texture
        return consume()
    }

    /**
     * Consume the sprites from an already made texture in the filesystem with the JSONConfig and the PNG file. (The recommended way)
     * If you want a JSONConfig file use first sprite(src) calls, then consumeSprites() and finally saveConsumedSprites to save them in the filesystem
     * @param name The path to the jsonConfig file so RendererOptimizer can load the big texture and return the sprite list fine.
     * @return The list of sprites ordered like in the JSON config.
     */
    fun consumeSprites(name: String): MutableList<Sprite> {
        try {
            val json = Json()
            val list: Array<TextureInfo> = json.fromJson(
                    Array<TextureInfo>::class.java,
                    Gdx.files.internal("$name.json")
            )
            if (list.isEmpty()) {
                return mutableListOf()
            }
            pixmap.tInfos = list.toMutableList()
            val t = Texture(Gdx.files.internal("$name.png"))
            val sprites = consumeSprites(t)
            spriteStore.addAll(sprites)
            return sprites
        } catch (e: Throwable) {
            Gdx.app.log("RendererOptimizer $this","Error consuming from JSON. Handle the empty list if this is the first time consuming! ${e.message}")
            return mutableListOf()
        }
    }


}