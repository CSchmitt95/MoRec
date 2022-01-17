package de.carloschmitt.morec.recording;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.carloschmitt.morec.dialogs.MovementDialog;
import de.carloschmitt.morec.model.Data;
import de.carloschmitt.morec.model.Movement;

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

        Data.state = Data.State.CONNECTED;
        Log.d(TAG, "Recording Scheduler beendet.");
        MovementDialog.refreshTexts();
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
        future = scheduler.scheduleAtFixedRate(new RecordingRunner(latch, movement),0,1000/Data.SAMPLES_PER_SECOND, TimeUnit.MILLISECONDS);
        return future;
    }

}
