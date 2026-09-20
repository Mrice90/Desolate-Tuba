package com.infiniteconquest.gui;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/** Plays curated CC0 cues bundled in game-gui resources. */
final class SoundEffects {
    enum Cue { MOVE, DEPLOY, MELEE, RANGED, SPELL, DAMAGE, PENALTY, DESTROY, VICTORY, DEFEAT }
    private SoundEffects() { }

    static void play(Cue cue) {
        Thread thread = new Thread(() -> playResource("/audio/" + cue.name().toLowerCase() + ".wav"),
                "infinite-conquest-sound");
        thread.setDaemon(true);
        thread.start();
    }

    private static void playResource(String path) {
        try (InputStream resource = SoundEffects.class.getResourceAsStream(path)) {
            if (resource == null) return;
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(resource))) {
                Clip clip = AudioSystem.getClip();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) clip.close();
                });
                clip.open(audio);
                clip.start();
            }
        } catch (Exception ignored) {
            // Audio feedback is optional; unavailable output must never interrupt a match.
        }
    }
}
