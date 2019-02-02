package com.mybaltazar.baltazar2.events;

public class CoinChangedEvent
{
    public final int amount;

    public CoinChangedEvent(int amount) {
        this.amount = amount;
    }
}
