package architecture.engine

import com.my.architecture.engine.structs.GameObject

/**
 * An Animation will try to change the sprite index of the desired gameObject depending on the configuration
 * If you are not gonna use the animation anymore call dispose() or it will stay in memory forever.
 */
class Animation(private val gameObject: GameObject, private val start: Int, private val end: Int, private val framesBetween: Int, private val repeat: Boolean) {
    private var running: Boolean = false
    private var currentFrame: Int = 0

    public var finished = false


    fun update() {
        if (!running) {
            currentFrame = 0
            gameObject.spriteIndex = start
        }

        if (currentFrame >= framesBetween) {
            gameObject.spriteIndex++
            currentFrame = 0
        }

        if (gameObject.spriteIndex > end && repeat) {
            currentFrame = 0
            gameObject.spriteIndex = currentFrame
            return
        }

        if (gameObject.spriteIndex > end) {
            gameObject.spriteIndex = end
            finished = true
            return
        }
        currentFrame++
    }

    /**
     * Start the animation
     */
    fun start() {
        running = true
        currentFrame = 0
        gameObject.spriteIndex = start
    }

    /**
     * End the animation
     */
    fun end() {
        running = false
        currentFrame = 0
        gameObject.spriteIndex = start
    }

    /**
     * Free the animation
     */
    fun dispose() {
        Animator.destroy(this)
    }

}

class Animator {

    companion object {
        var animations: MutableList<Animation> = mutableListOf()

        /**
         * Generate an animation
         * @param gameObject The gameObject to animate
         * @param start index to start (inclusive)
         * @param end index to end (inclusive)
         * @param framesBetween how many update cycles between animations
         * @param repeat if the animation should be repeated
         */
        fun animate(gameObject: GameObject, start: Int, end: Int, framesBetween: Int, repeat: Boolean): Animation {
            animations.add(Animation(gameObject, start, end, framesBetween, repeat))
            return animations.last()
        }

        /**
         * Destroy the animation
         * @param animation to destroy
         */
        fun destroy(animation: Animation) {
            animations.remove(animation)
        }
    }

}