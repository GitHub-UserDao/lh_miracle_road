package dev.lhkongyu.lhmiracleroad.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;

public class FakeSoulClientPlayer extends AbstractClientPlayer {
    public FakeSoulClientPlayer(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Override
    public boolean isBaby() { return false; }
}
