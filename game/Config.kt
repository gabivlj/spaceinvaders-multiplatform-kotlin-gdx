package architecture.game

import com.badlogic.gdx.math.Vector2


class Config {

    companion object {
        fun config01(): Array<BasicEnemy> {
            return arrayOf(
                    EnemyFollow(Vector2()),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.8f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.8f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.8f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    )
            )
        }
        fun config02(): Array<BasicEnemy> {
            return arrayOf(
                    EnemyFollow(Vector2()),
                    EnemyFollow(Vector2()),
                    EnemyFollow(Vector2()),
                    EnemyFollow(Vector2()),
                    Enemy(Vector2(),
                            50f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 3f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.8f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.8f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.8f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(-0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    ),
                    Enemy(Vector2(),
                            40f,
                            arrayOf(
                                    Point(Vector2(0.4f, -0.6f), 100f, 10f),
                                    Point(Vector2(0.7f, -0.9f), 200f, 10f)
                            )
                    )
            )
        }

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
                                Enemy(Vector2(),  + Math.random().toFloat() * (70f - 30f) + 30f, randomSetOfPoint())
                            }
                            else -> EnemyFollow(Vector2())
                        }
                )
            }

            return enemies.toTypedArray()
        }

        private fun randomSetOfPoint(): Array<Point> {
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