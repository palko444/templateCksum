package com.dxc.command_executor;

//import com.dxc.omc.utils.Log;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.Duration;

public final class StreamGobbler {

    interface DaemonCallable<T> extends Callable<T> {
    }

    public static class WriteException extends IOException {

        public WriteException(IOException cause) {
            super(cause.getMessage(), cause.getCause());
        }
    }

    public static class ReadException extends IOException {

        public ReadException(IOException cause) {
            super(cause.getMessage(), cause.getCause());
        }
    }

    public static class QueueFullException extends IllegalStateException {

        public QueueFullException(IllegalStateException cause) {
            super(cause.getMessage(), cause.getCause());
        }
    }

    final ArrayBlockingQueue<Integer> q;
    final Future<Void> r;
    final Future<Void> w;
    final ExecutorService thread_pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("StreamGobbler");
            return t;
        }
    });

    public static StreamGobbler create(Reader reader, Writer writer, int queue_size) {
        return new StreamGobbler(reader, writer, queue_size);
    }

    StreamGobbler(final Reader reader, final Writer writer, int queue_size) {
        this.q = new ArrayBlockingQueue<>(64 * 1024);
        this.r = this.thread_pool.submit(new DaemonCallable<Void>() {
            @Override
            public Void call() throws WriteException {
                while (true) {
                    int ch;
                    try {
                        ch = reader.read();
                    } catch (IOException ex) {
                        throw new WriteException(ex);
                    }
                    q.add(ch);
                    if (ch == -1) {
                        return null;
                    }
                }
            }
        });

        this.w = this.thread_pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws ReadException {
                try {
                    int ch;
                    try {
                        while ((ch = q.take()) != -1) {
                            writer.write(ch);
                        }
                    } catch (IOException ex) {
                        throw new ReadException(ex);
                    }
                } catch (InterruptedException _ex) {
                    // just end
                }
                return null;
            }
        });
        // no new tasks will be submitted
        this.thread_pool.shutdown();
    }

    public StreamGobbler interrupt() {
        this.thread_pool.shutdownNow();
        return this;
    }

    public Future<Void> wait_completion(Duration timeout) throws WriteException, ReadException, QueueFullException {
        try {
            try {
                // wait for this.r for desired timeout
                // this timeout can be minimal as the thread should be finished by now
                // if not, it's probably hanging
//                Log.trace("Waiting for StreamGobbler-reader to finish");
                this.r.get(timeout.getMillis(), TimeUnit.MILLISECONDS);
//                Log.trace("StreamGobbler-reader finished");
            } catch (ExecutionException ex) {
                // if we got an exception, throw it
//                Log.trace("StreamGobbler-reader got exception");
                Throwable ec = ex.getCause();
                if (ec instanceof ReadException) {
                    throw (ReadException) ec;
                } else if (ec instanceof IllegalStateException) {
                    throw new QueueFullException((IllegalStateException) ec);
                }
                // this means a programmer error
                throw new AssertionError("Unexpected exception", ec);
            } catch (TimeoutException _ex) {
//                Log.trace("StreamGobbler-reader timed out");
            }

            try {
                // thread_pool.shutdownNow() already sent interruptedexception,
                // so this.w already finished or will shortly
//                Log.trace("Waiting for StreamGobbler-writer to finish");
                this.w.get(timeout.getMillis(), TimeUnit.MILLISECONDS);
//                Log.trace("StreamGobbler-writer finished");
            } catch (ExecutionException ex) {
//                Log.trace("StreamGobbler-writer got exception");
                // if we got an exception, throw it
                Throwable ec = ex.getCause();
                if (ec instanceof WriteException) {
                    throw (WriteException) ec;
                }
                // this means a programmer error
                throw new AssertionError("Unexpected exception", ec);
            } catch (TimeoutException ex) {
//                Log.trace("StreamGobbler-writer timed out");
            }
        } catch (InterruptedException ie) {
//            Log.trace("StreamGobbler was interrupted");
            this.r.cancel(true); // mayInterruptIfRunning is probably completely pointless here
            this.w.cancel(true); // but important here
        }
        return null;
    }
}
