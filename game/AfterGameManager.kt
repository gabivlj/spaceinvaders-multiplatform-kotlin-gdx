package architecture.game

import architecture.engine.*
import architecture.engine.structs.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class AfterGameManager : GameObject(arrayOf(), 0f, 0f, Vector2(0f, 0f)) {
    var text: Text = Text()
    var started = false

    private lateinit var hoverSound: AudioID
    private lateinit var clickSound: AudioID
    private lateinit var menuMusic: AudioID

    override fun start() {
        hoverSound = Audio.add("button_hover.mp3", AudioType.TRACK)
        clickSound = Audio.add("button_click.mp3", AudioType.TRACK)
        menuMusic = Audio.add("menu_music.mp3", AudioType.MUSIC)
        Audio.play(menuMusic)

        Game.camera.position.set(0.0f, 0.0f, 0.0f)
        Game.camera.update()
        spawnStack()
        text = Text()
        text.render()
        text.position = Vector2(-55f, -55f)
        text.text = "Score: ${Config.scoreGainedInPhase} - Total: ${Config.currentScore}"
        text.width = 3f
        text.height = 3f
        val buttonNextPhase = UIButton(
            SpaceInvaders.spritesMenus.slice(0..0).toTypedArray(),
            130f,
            130f,
            Vector2(),
            "Start Next phase!",
            Vector2()
        ) {
            Audio.play(clickSound)
            if (started) return@UIButton
            if (Config.currentMapIdx + 1 == Config.maps.size) {
                SpaceInvaders.worlds[0].start()
                return@UIButton
            }
            started = true
            Config.currentMapIdx++
            SpaceInvaders.worlds[1].start()
        }
        buttonNextPhase.onHover = {
            Audio.play(hoverSound)
            it.text = "Start Next phase! (SELECTED)"
        }
        buttonNextPhase.stopHover = {
            it.text = "Start Next phase!"
        }
        val speedButton = UIButton(
            SpaceInvaders.spritesMenus.slice(0..0).toTypedArray(),
            130f,
            130f,
            Vector2(),
            "Buy more speed? Cost: 1000 score",
            Vector2()
        ) {
            Audio.play(clickSound)
            Config.buy(Item.SPEED)
        }
        speedButton.onHover = {
            Audio.play(hoverSound)
            it.text = "Buy more speed? Cost: 1000 score (SELECTED)"
        }
        speedButton.stopHover = {
            it.text = "Buy more speed? Cost: 1000 score"
        }
        val attackButton = UIButton(
            SpaceInvaders.spritesMenus.slice(0..0).toTypedArray(),
            130f,
            130f,
            Vector2(),
            "Buy more attack? Cost: 1000 score",
            Vector2()
        ) {
            Audio.play(clickSound)
            Config.buy(Item.ATTACK)
        }
        attackButton.onHover = {
            Audio.play(hoverSound)
            it.text = "Buy more attack? Cost: 1000 score (SELECTED)"
        }
        attackButton.stopHover = {
            it.text = "Buy more attack? Cost: 1000 score"
        }
        speedButton.useScreenCoords = false
        speedButton.textUI.width = 2f
        speedButton.textUI.height = 2f
        speedButton.position = Vector2(0f, 0f)
        attackButton.position = Vector2(0f, 160f)
        attackButton.useScreenCoords = false
        attackButton.textUI.width = 2f
        attackButton.textUI.height = 2f
        buttonNextPhase.position = Vector2(0f, 320f)
        buttonNextPhase.useScreenCoords = false
        World.world.instantiate(speedButton)
        World.world.instantiate(attackButton)
        World.world.instantiate(buttonNextPhase)
    }

    override fun update(dt: Float) {
        text.text = "Score: ${Config.scoreGainedInPhase} - Total: ${Config.currentScore}"
    }

    override fun onDispose() {
        text.stop()
    }
}
