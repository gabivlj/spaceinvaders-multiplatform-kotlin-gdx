package architecture.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound

enum class AudioType {
    MUSIC, TRACK
}

class AudioID(val ID: Int, val type: AudioType) {
}

/**
 * Audio class handles disposing, playing, stopping and loading music/track files in one single place
 *
 * It's cool to use this class instead of handling by yourself the sounds because it will syncronize with the
 * application lifecycle.
 */
class Audio {
    companion object {
        var muted: Boolean = false
        set(value) {
            if (value) {
                mutableSounds.forEach { it.pause() }
                Gdx.app.log("PAUSE", " PAUSAAA ")
                mutableMusic.forEach { it.pause() }
            } else {
                mutableMusic.forEach { it.play() }
            }
            field = value
        }
        private val mutableSounds: MutableList<Sound> = mutableListOf()

        val sounds: Array<Sound>
            get() { return mutableSounds.toTypedArray() }

        private val mutableMusic: MutableList<Music> = mutableListOf()
        val music: Array<Music>
            get() { return mutableMusic.toTypedArray() }

        /**
         * Loads a new audio of the type @param type
         * @param path The path to the file
         * @param type Type of the audio. Use AudioType.Track for small sized sounds.
         * @return generated AudioID
         */
        fun add(path: String, type: AudioType): AudioID {
            return when (type) {
                AudioType.MUSIC -> {
                    val music = Gdx.audio.newMusic(Gdx.files.internal(path))
                    mutableMusic.add(music)
                    AudioID(mutableMusic.size - 1, type)
                }
                AudioType.TRACK -> {
                    val sound = Gdx.audio.newSound(Gdx.files.internal(path))
                    mutableSounds.add(sound)
                    AudioID(mutableSounds.size - 1, type)
                }
            }
        }

        /**
         * Plays an AudioID
         * @return The instance ID if the AudioID is type of Track, will return 1 if the AudioID type is Music. If Audio is muted returns -1
         * @throws Exception if the ID is not correct
         */
        fun play(id: AudioID): Long {
            if (muted) return -1
            if (id.ID < 0 || id.ID >= mutableSounds.size) {
                throw Exception("Error, bad ID ${id.ID}")
            }
            return when (id.type) {
                AudioType.TRACK -> {
                    val sound = mutableSounds[id.ID]
                    sound.play()
                }
                AudioType.MUSIC -> {
                    val music = mutableMusic[id.ID]
                    if (music.isPlaying) {
                        music.position = 0.0f
                        return 1
                    }
                    music.play()
                    1
                }

            }
        }

        /**
         * Stops the desired audio
         * @param id AudioID to stop
         * @param instance If you are passing an AudioID of type Track, you need to pass this param to stop a single instance, otherwise it will stop all track instances of this AudioID
         */
        fun stop(id: AudioID, instance: Long = Long.MIN_VALUE) {
            when (id.type) {
                AudioType.TRACK -> {
                    val sound = mutableSounds[id.ID]
                    if (instance != Long.MIN_VALUE) {
                        sound.stop(instance)
                        return
                    }
                    sound.stop()
                }
                AudioType.MUSIC -> {
                    val music = mutableMusic[id.ID]
                    music.stop()
                }
            }
        }

        /**
         * Executes the passed function with the element of the type that is the AudioID
         *
         * If class is muted, it does not do anything
         */
        inline fun <reified T>apply(id: AudioID, fn: (T) -> Unit) {
            if (muted) return
            when (T::class) {
                Sound::class ->  {
                    fn(sounds[id.ID] as T)
                }
                Music::class -> {
                    fn(music[id.ID] as T)
                }
            }
        }

        fun dispose() {
            mutableSounds.forEach { it.dispose() }
            music.forEach { it.dispose() }
        }
    }
}