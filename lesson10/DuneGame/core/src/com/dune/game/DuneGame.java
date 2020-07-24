package com.dune.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.screens.GameScreen;
import com.dune.game.screens.ScreenManager;

import java.io.*;

public class DuneGame extends Game {
    private SpriteBatch batch;

    // Домашнее задание:
    // 1. Разобраться с кодом
    // 2. Харвестеры AI должны собирать ресурсы и отвозить на базу

    /*
    1)  Уменьшить танки и харвестеры
2)  Поиск оптимального маршрута
3)  Поставить точки базы и выдать изначально допустим по 1 билдеру\харвестеру(появление привязать к базе
4)  Добавить новый тип Билдеры ( тип строители)
5)  Возможность билдерами возводить здания
6)  Добавить постройку нескольких зданий(пункта приема ресурсов(можно считать основной базой как в старкрафте), казарму для постройки танков, харвестеры строить на базе)
7)  Прикрутить минимальный интерфейс при выборе билдера (снизу появляется панелька с возможными постройками)
     */

    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}