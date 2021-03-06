package architecture.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

enum class Difficulty(
    val multiplierDamage: Float,
    val multiplierHp: Float,
    val multiplierNOfEnemies: Int,
    val spriteRoute: IntRange,
    val msg: String
) {
    EASY(
        .7f,
        .5f,
        1,
        0..0,
        "So you are going with the easy way"
    ),
    MEDIUM(1.0f, .65f, 1, 1..1, "It's ok"),
    HARD(2.0f, .675f, 2, 2..2, "Up for a challenge?"),
    REALLY_HARD(3.0f, .675f, 3, 3..3, "Death!"),
    HOLY_THIS_IS_REAAAALLY_HARD(6.0f, .675f, 5, 4..4, "Impossible."),
}

enum class Item(val cost: Float, val sum: Float) {
    ATTACK(1000f, 1.5f),
    SPEED(1000f, 1.5f)
}

class Config {

    companion object {
        // In what number will be the score increased.
        val sumScore: Float = 500f
        var nPlayers: Int = 1
        var colorOfShip: Color = Color.WHITE
        var maps: Array<Map> = arrayOf()
        var mapSpritesIdx = 0..0
        var currentMapIdx: Int = 0
        val currentMap: Map
            get() = maps[currentMapIdx]
        var scoreGainedInPhase: Float = 0.0f
        var currentScore: Float = 0.0f
        var difficulty = Difficulty.MEDIUM
        var speed = 1.0f
        var attack = 1.0f
        fun buy(what: Item) {
            if (what.cost > currentScore) {
                return
            }
            currentScore -= what.cost
            when (what) {
                Item.ATTACK -> {
                    attack += what.sum
                }
                Item.SPEED -> {
                    speed += what.sum
                }
            }
        }
        /**
         * @deprecated
         */
        fun randomConfig(size: Int): Array<BasicEnemy> {
            val enemies: MutableList<BasicEnemy> = mutableListOf()
            for (i in 1..size) {
                val random = Math.random().toFloat()
                enemies.add(
                    when (random) {
                        in 0f..0.5f -> {
                            EnemyFollow(Vector2())
                        }
                        in 0.5f..1f -> {
                            Enemy(Vector2(), + Math.random().toFloat() * (70f - 30f) + 30f, randomSetOfPoint())
                        }
                        else -> EnemyFollow(Vector2())
                    }
                )
            }

            return enemies.toTypedArray()
        }

        fun randomSetOfPoint(): Array<Point> {
            val points: MutableList<Point> = mutableListOf()
            for (i in 0..2) {
                points.add(
                    Point(
                        Vector2(
                            Math.random().toFloat() - Math.random().toFloat(),
                            -Math.random().toFloat()
                        ),
                        Math.random().toFloat() * (200f - 100f) + 100f,
                        Math.random().toFloat() * (20f - 10f) + 10f
                    )
                )
            }
            return points.toTypedArray()
        }
    }
}
