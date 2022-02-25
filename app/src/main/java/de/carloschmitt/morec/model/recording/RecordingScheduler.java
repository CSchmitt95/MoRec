package de.carloschmitt.morec.model.recording;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.carloschmitt.morec.model.State;
import de.carloschmitt.morec.ApplicationController;

public class RecordingScheduler implements Runnable{
    private final String TAG = "RecordingScheduler";
    public CountDownLatch latch;
    ScheduledFuture<?> future;
    Movement movement;
    public RecordingScheduler(Movement movement){
        this.movement = movement;
    }

    @Override
    public void run() {
        Log.d(TAG, "Recording Scheduler gestartet.");
        latch = new CountDownLatch(1);
        schedulePolling();
        stopWhenDone();

        ApplicationController.state = State.CONNECTED;
        Log.d(TAG, "Recording Scheduler beendet.");
        //RecordingDialogue.refreshTexts();
    }

    private void stopWhenDone(){
        try {
            latch.await();
            future.cancel(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ScheduledFuture<?> schedulePolling(){
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        future = scheduler.scheduleAtFixedRate(new RecordingRunner(latch, movement),0,1000/ ApplicationController.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);
        return future;
    }

}
