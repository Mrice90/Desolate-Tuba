package com.infiniteconquest.gui;

import javax.sound.sampled.*;

/** Small generated cues keep the prototype self-contained and require no audio assets. */
final class SoundEffects {
    enum Cue { MOVE, DEPLOY, MELEE, RANGED, SPELL, DAMAGE, PENALTY, DESTROY, VICTORY, DEFEAT }
    private static final float RATE = 22_050f;
    private SoundEffects() { }

    static void play(Cue cue) {
        Thread thread = new Thread(() -> synthesize(cue), "infinite-conquest-sound");
        thread.setDaemon(true);
        thread.start();
    }

    private static void synthesize(Cue cue) {
        double[] frequencies = switch (cue) {
            case MOVE -> new double[]{330, 440}; case DEPLOY -> new double[]{220, 330, 494};
            case MELEE -> new double[]{120, 75}; case RANGED -> new double[]{760, 420};
            case SPELL -> new double[]{520, 690, 880}; case DAMAGE -> new double[]{180, 140};
            case PENALTY -> new double[]{210, 175, 140}; case DESTROY -> new double[]{150, 90, 55};
            case VICTORY -> new double[]{392, 494, 587, 784}; case DEFEAT -> new double[]{294, 233, 175};
        };
        int noteMs = switch (cue) { case VICTORY, DEFEAT -> 115; case DESTROY -> 90; default -> 60; };
        byte[] audio = new byte[(int) (RATE * noteMs / 1000) * frequencies.length];
        int offset = 0;
        for (double frequency : frequencies) {
            int samples = (int) (RATE * noteMs / 1000);
            for (int i = 0; i < samples; i++) {
                double envelope = 1.0 - (double) i / samples;
                audio[offset++] = (byte) (Math.sin(2 * Math.PI * frequency * i / RATE) * 55 * envelope);
            }
        }
        AudioFormat format = new AudioFormat(RATE, 8, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format); line.start(); line.write(audio, 0, audio.length); line.drain();
        } catch (LineUnavailableException ignored) { }
    }
}
