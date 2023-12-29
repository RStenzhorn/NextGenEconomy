package de.rjst.nextgeneconomy.config.async;

public interface AsyncExecutor {

    void accept(final Runnable runnable);

}
