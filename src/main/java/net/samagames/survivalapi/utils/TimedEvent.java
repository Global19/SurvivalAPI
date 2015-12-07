package net.samagames.survivalapi.utils;

import org.bukkit.ChatColor;

public class TimedEvent implements Runnable
{
    private final String name;
    private final ChatColor color;
    private final Runnable callback;
    private final boolean title;

    private int minutes;
    private int seconds;
    private boolean wasRun;

    public TimedEvent(int minutes, int seconds, String name, ChatColor color, boolean title, Runnable callback)
    {
        this.name = name;
        this.color = color;
        this.title = title;
        this.callback = callback;

        this.minutes = minutes;
        this.seconds = seconds;
        this.wasRun = false;
    }

    @Override
    public void run()
    {
        this.callback.run();
    }

    public void decrement()
    {
        this.seconds--;

        if (this.seconds < 0)
        {
            this.minutes--;
            this.seconds = 59;
        }

        if ((this.minutes < 0 || this.seconds == 0 && this.minutes == 0) && !this.wasRun)
        {
            this.wasRun = true;
            this.run();
        }
    }

    public TimedEvent copy(int minute, int seconds)
    {
        return new TimedEvent(minute, seconds, this.name, this.color, this.title, this.callback);
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public Runnable getCallback()
    {
        return this.callback;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isTitle()
    {
        return this.title;
    }

    public boolean isWasRun() {
        return wasRun;
    }
}