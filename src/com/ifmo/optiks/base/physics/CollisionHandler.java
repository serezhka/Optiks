package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Author: Aleksey Vladiev (Avladiev2@gmail.com)
 */
public interface CollisionHandler {
    void handle(final Contact contact, final LaserBullet bullet, final Body thing);
}
