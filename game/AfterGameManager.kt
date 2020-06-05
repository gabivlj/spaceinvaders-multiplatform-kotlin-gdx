package architecture.game

import architecture.engine.Game
import architecture.engine.World
import architecture.engine.structs.Text
import architecture.engine.structs.UIButton
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.my.architecture.engine.structs.GameObject

class AfterGameManager : GameObject(arrayOf(), 0f, 0f, Vector2(0f, 0f)) {
    var text: Text = Text()
    var started = false
    override fun start() {
        text = Text()
        text.render()
        text.position = Vector2(0f, -55f)
        text.text = "Score: ${Config.scoreGainedInPhase} - Total: ${Config.currentScore}"


        text.width = 5f
        text.height = 5f
        val buttonNextPhase = UIButton(
                SpaceInvaders.spritesMenus.slice(0..0).toTypedArray(),
                50f,
                50f,
                Vector2((Gdx.graphics.width / 2f) - (100f / 4), Gdx.graphics.height / 4f),
                "Start Next phase!",
                Vector2()) {
            if (started) return@UIButton
            if (Config.currentMapIdx + 1 == Config.maps.size) {
                SpaceInvaders.worlds[0].start()
                return@UIButton
            }
            started = true
            Config.currentMapIdx++
            SpaceInvaders.worlds[1].start()
        }
        val speedButton = UIButton(
                SpaceInvaders.spritesMenus.slice(0..0).toTypedArray(),
                50f,
                50f,
                Vector2((Gdx.graphics.width / 2f) - (100f / 4), Gdx.graphics.height / 2f),
                "Buy more speed? Cost: 1000 score",
                Vector2()) {
            Gdx.app.log("sdasdadas", "dajfsdjfdsjfsdajfdas")
            Config.buy(Item.SPEED)
        }
        speedButton.textUI.width = 2f
        speedButton.textUI.height = 2f
        World.world.instantiate(speedButton)
        World.world.instantiate(buttonNextPhase)
        Game.camera.position.set(0.0f, 0.0f, 0.0f)
        Game.camera.update()
    }


    override fun update(dt: Float) {
        text.text = "Score: ${Config.scoreGainedInPhase} - Total: ${Config.currentScore}"
    }

    override fun onDispose() {
        text.stop()
    }
}